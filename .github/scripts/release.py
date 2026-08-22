#!/usr/bin/env python3
"""
gPatri Release Automation Engine

This script automates versioning and changelog management following:
- Semantic Versioning (SemVer 2.0.0)
- Conventional Commits 1.0.0
- Keep a Changelog format

Key functionalities:
1. Parse git tags and commits since last tag
2. Parse and validate SemVer
3. Determine bump type (major, minor, patch, none)
4. Update Maven project version (pom.xml)
5. Promote [Não Lançado] / [Unreleased] section in CHANGELOG.md
6. Generate fallback release notes from commits
7. Output GitHub Actions variables (GITHUB_OUTPUT) and release_notes.md
"""

from __future__ import annotations

import argparse
from dataclasses import dataclass
from datetime import datetime
import os
from pathlib import Path
import re
import subprocess
import sys
from typing import NamedTuple, Optional, Sequence
import xml.etree.ElementTree as ET


class SemVer(NamedTuple):
    major: int
    minor: int
    patch: int
    prerelease: Optional[str] = None


@dataclass
class CommitInfo:
    hash: str
    type: str = ""
    scope: Optional[str] = None
    description: str = ""
    breaking: bool = False
    body: str = ""
    raw: str = ""
    author_name: str = ""
    author_email: str = ""

    @classmethod
    def from_raw(
        cls,
        commit_hash: str,
        message: str,
        author_name: str = "",
        author_email: str = "",
    ) -> "CommitInfo":
        message = message.strip()
        lines = message.split("\n", 1)
        header = lines[0].strip()
        body = lines[1].strip() if len(lines) > 1 else ""

        # Conventional Commits regex pattern:
        # type(scope)!: description OR type!: description OR type(scope): description OR type: description
        pattern = r"^(?P<type>[a-zA-Z0-9_-]+)(?:\((?P<scope>[^)]+)\))?(?P<breaking>!)?:\s*(?P<desc>.+)$"
        match = re.match(pattern, header)

        breaking_in_body = bool(
            re.search(r"(?:^|\n)BREAKING[ -]CHANGE:\s*", message, re.IGNORECASE)
        )

        if match:
            commit_type = match.group("type").lower()
            scope = match.group("scope")
            breaking_bang = bool(match.group("breaking"))
            description = match.group("desc").strip()
            breaking = breaking_bang or breaking_in_body

            return cls(
                hash=commit_hash,
                type=commit_type,
                scope=scope,
                description=description,
                breaking=breaking,
                body=body,
                raw=message,
                author_name=author_name,
                author_email=author_email,
            )

        return cls(
            hash=commit_hash,
            type="",
            scope=None,
            description=header,
            breaking=breaking_in_body,
            body=body,
            raw=message,
            author_name=author_name,
            author_email=author_email,
        )


def parse_semver(version_str: str) -> SemVer:
    """Parses a version string into a SemVer tuple (major, minor, patch, prerelease)."""
    clean_version = version_str.strip()
    pattern = r"^[vV]?(?P<major>\d+)\.(?P<minor>\d+)\.(?P<patch>\d+)(?:-(?P<prerelease>[0-9A-Za-z.-]+))?$"
    match = re.match(pattern, clean_version)
    if not match:
        raise ValueError(f"Invalid semver version: '{version_str}'")

    major = int(match.group("major"))
    minor = int(match.group("minor"))
    patch_ver = int(match.group("patch"))
    prerelease = match.group("prerelease")

    return SemVer(major, minor, patch_ver, prerelease)


def format_semver(
    major: int,
    minor: int,
    patch: int,
    prerelease: Optional[str] = None,
    prefix_v: bool = False,
) -> str:
    """Formats major, minor, patch and optional prerelease into a SemVer string."""
    prefix = "v" if prefix_v else ""
    suffix = f"-{prerelease}" if prerelease else ""
    return f"{prefix}{major}.{minor}.{patch}{suffix}"


def compare_semver(v1_str: str, v2_str: str) -> int:
    """Compares two SemVer strings. Returns 1 if v1 > v2, 0 if v1 == v2, -1 if v1 < v2."""
    v1 = parse_semver(v1_str)
    v2 = parse_semver(v2_str)
    t1 = (v1.major, v1.minor, v1.patch)
    t2 = (v2.major, v2.minor, v2.patch)
    if t1 > t2:
        return 1
    elif t1 < t2:
        return -1
    return 0


def max_semver(v1_str: str, v2_str: str) -> str:
    """Returns the higher SemVer string between two versions."""
    return v1_str if compare_semver(v1_str, v2_str) >= 0 else v2_str



KNOWN_CONTRIBUTOR_EMAILS = {
    "thiagoferreira2004f@gmail.com": "@thiago-f-santos",
    "eduardoferreirabmatos@gmail.com": "@EduardoFerreiraB",
}


def is_ignored_commit(commit: CommitInfo) -> bool:
    """Returns True if the commit should be ignored for release bump and notes."""
    raw = commit.raw.lower()
    desc = commit.description.lower()
    return (
        "[skip ci]" in raw
        or "[ci skip]" in raw
        or raw.startswith("chore(release):")
        or desc.startswith("chore(release):")
    )


def determine_bump_type(commits: Sequence[CommitInfo]) -> str:
    """
    Determines whether a bump is 'major', 'minor', 'patch', or 'none' based on commits.
    Ignores commits containing [skip ci], [ci skip], or chore(release):.
    - Any breaking change -> major
    - Any 'feat' -> minor
    - Any other qualifying commit -> patch
    - No qualifying commits -> none
    """
    eligible = [c for c in commits if not is_ignored_commit(c)]
    if not eligible:
        return "none"

    if any(c.breaking for c in eligible):
        return "major"

    if any(c.type == "feat" for c in eligible):
        return "minor"

    return "patch"


def calculate_next_version(
    current_version: str,
    bump_type: str,
    prerelease: Optional[str] = None,
) -> str:
    """Calculates the next version string given current version and bump type."""
    semver = parse_semver(current_version)
    bump = bump_type.lower()

    if bump == "major":
        next_major, next_minor, next_patch = semver.major + 1, 0, 0
    elif bump == "minor":
        next_major, next_minor, next_patch = semver.major, semver.minor + 1, 0
    elif bump == "patch":
        next_major, next_minor, next_patch = semver.major, semver.minor, semver.patch + 1
    elif bump == "none":
        next_major, next_minor, next_patch = semver.major, semver.minor, semver.patch
    else:
        raise ValueError(
            f"Invalid bump type: '{bump_type}'. Must be 'major', 'minor', 'patch', or 'none'."
        )

    return format_semver(next_major, next_minor, next_patch, prerelease=prerelease)


def sanitize_mentions(text: str) -> str:
    """Wraps annotations and non-author @mentions in backticks to prevent unintentional GitHub user mentions."""
    return re.sub(r"(?<!by\s)(?<![`\w])@([a-zA-Z0-9_-]+)(?!`)", r"`@\1`", text)


def enrich_changelog_with_authors(
    unreleased_content: str, commits: Sequence[CommitInfo]
) -> str:
    """Appends 'by @User' to changelog bullet points if missing, based on commit authors."""
    eligible = [c for c in commits if not is_ignored_commit(c)]
    if not eligible:
        return unreleased_content

    # Collect author per scope and global authors
    scope_to_authors: dict[str, set[str]] = {}
    all_authors: list[str] = []
    for c in eligible:
        handle = get_author_handle(c)
        if handle:
            if handle not in all_authors:
                all_authors.append(handle)
            if c.scope:
                scope_to_authors.setdefault(c.scope.lower(), set()).add(handle)

    if not all_authors:
        return unreleased_content

    default_author = all_authors[0] if len(all_authors) == 1 else None

    lines = unreleased_content.splitlines()
    enriched_lines: list[str] = []
    scope_pattern = re.compile(r"^(\s*[-*]\s+\*\*([^*]+)\*\*:\s*)(.+)$")
    bullet_pattern = re.compile(r"^(\s*[-*]\s+)(.+)$")

    for line in lines:
        if "by @" in line:
            enriched_lines.append(line)
            continue

        match_scope = scope_pattern.match(line)
        if match_scope:
            prefix, scope, rest = match_scope.groups()
            scope_authors = list(scope_to_authors.get(scope.strip().lower(), []))
            if len(scope_authors) == 1:
                author_suffix = f" by {scope_authors[0]}"
            elif default_author:
                author_suffix = f" by {default_author}"
            elif all_authors:
                author_suffix = f" by {', '.join(all_authors)}"
            else:
                author_suffix = ""
            enriched_lines.append(f"{prefix}{rest.rstrip()}{author_suffix}")
            continue

        match_bullet = bullet_pattern.match(line)
        if match_bullet:
            prefix, rest = match_bullet.groups()
            if default_author:
                author_suffix = f" by {default_author}"
            elif all_authors:
                author_suffix = f" by {', '.join(all_authors)}"
            else:
                author_suffix = ""
            enriched_lines.append(f"{prefix}{rest.rstrip()}{author_suffix}")
            continue

        enriched_lines.append(line)

    return "\n".join(enriched_lines)


def get_author_handle(commit: CommitInfo) -> Optional[str]:
    """Returns the @username handle for the commit author, if valid and not a bot."""
    name = commit.author_name.strip()
    email = commit.author_email.strip()

    if not name and not email:
        return None

    # Ignore bots and automated actors
    low_name = name.lower()
    low_email = email.lower()
    if (
        "github-actions" in low_name
        or "github-actions" in low_email
        or "bot" in low_name
        or "bot" in low_email
        or "[bot]" in low_name
        or "[bot]" in low_email
        or low_name in {"web-flow", "github", "actions", "root"}
        or low_email in {"web-flow@github.com", "actions@github.com"}
    ):
        return None

    # Check mapped known emails
    if email.lower() in KNOWN_CONTRIBUTOR_EMAILS:
        return KNOWN_CONTRIBUTOR_EMAILS[email.lower()]

    # Extract github handle from noreply email (e.g. 12345+username@users.noreply.github.com)
    noreply = re.search(
        r"(?:\d+\+)?([a-zA-Z0-9_-]+)@users\.noreply\.github\.com", email
    )
    if noreply:
        handle = noreply.group(1)
        if "bot" not in handle.lower() and "github-actions" not in handle.lower():
            return f"@{handle}"

    # If the name is already in explicit @username format
    if name.startswith("@") and len(name) > 1:
        clean_name = name.lstrip("@")
        if re.match(r"^[a-zA-Z0-9_-]+$", clean_name) and "bot" not in clean_name.lower():
            return f"@{clean_name}"

    return None


def generate_fallback_notes(commits: Sequence[CommitInfo]) -> str:
    """Generates Keep-a-Changelog compatible release notes from commits with inline author attribution."""
    eligible = [c for c in commits if not is_ignored_commit(c)]
    if not eligible:
        return "- Melhorias e correções gerais."

    breaking = [c for c in eligible if c.breaking]
    feats = [c for c in eligible if not c.breaking and c.type == "feat"]
    fixes = [c for c in eligible if not c.breaking and c.type == "fix"]
    mods = [
        c for c in eligible if not c.breaking and c.type in {"refactor", "perf", "style"}
    ]
    others = [
        c
        for c in eligible
        if not c.breaking and c not in feats and c not in fixes and c not in mods
    ]

    sections: list[str] = []

    def format_list(items: list[CommitInfo]) -> list[str]:
        lines = []
        for item in items:
            desc = sanitize_mentions(item.description)
            author_handle = get_author_handle(item)
            author_suffix = f" by {author_handle}" if author_handle else ""
            if item.scope:
                lines.append(f"- **{item.scope}**: {desc}{author_suffix}")
            else:
                lines.append(f"- {desc}{author_suffix}")
        return lines

    if breaking:
        sections.append("### ⚠️ Breaking Changes\n" + "\n".join(format_list(breaking)))
    if feats:
        sections.append("### Adicionado\n" + "\n".join(format_list(feats)))
    if mods:
        sections.append("### Modificado\n" + "\n".join(format_list(mods)))
    if fixes:
        sections.append("### Corrigido\n" + "\n".join(format_list(fixes)))
    if others:
        sections.append("### Outros\n" + "\n".join(format_list(others)))

    return "\n\n".join(sections)


def promote_changelog(
    changelog_content: str,
    next_version: str,
    release_date: Optional[str] = None,
    fallback_notes: Optional[str] = None,
    commits: Optional[Sequence[CommitInfo]] = None,
) -> str:
    """
    Promotes the [Não Lançado] / [Unreleased] section to the new version header.
    If the unreleased section is empty, uses fallback_notes if provided.
    Enriches unreleased lines with author attribution if commits are supplied.
    """
    date_str = release_date or datetime.now().strftime("%Y-%m-%d")

    # Match the unreleased header and its section content
    header_pattern = re.compile(
        r"(##\s*\[(?:Não Lançado|Unreleased)\])([\s\S]*?)(?=(\n---\s*\n##\s*\[|\n##\s*\[|\Z))",
        re.IGNORECASE,
    )

    match = header_pattern.search(changelog_content)
    if not match:
        raise ValueError("Could not find '## [Não Lançado]' or '## [Unreleased]' section in CHANGELOG.")

    unreleased_header = match.group(1).strip()
    unreleased_body = match.group(2).strip()

    # Remove leading/trailing separator dashes if present in body
    unreleased_body_cleaned = re.sub(r"^---\s*", "", unreleased_body).strip()
    unreleased_body_cleaned = re.sub(r"\s*---\s*$", "", unreleased_body_cleaned).strip()

    if unreleased_body_cleaned:
        sanitized_body = sanitize_mentions(unreleased_body_cleaned)
        if commits:
            release_notes = enrich_changelog_with_authors(sanitized_body, commits)
        else:
            release_notes = sanitized_body
    elif fallback_notes and fallback_notes.strip():
        release_notes = sanitize_mentions(fallback_notes.strip())
    else:
        release_notes = "- Sem alterações detalhadas registradas."

    remainder = changelog_content[match.end(2):]
    if remainder.startswith("\n---"):
        new_release_section = (
            f"{unreleased_header}\n\n---\n\n"
            f"## [{next_version}] - {date_str}\n\n"
            f"{release_notes}"
        )
    else:
        new_release_section = (
            f"{unreleased_header}\n\n---\n\n"
            f"## [{next_version}] - {date_str}\n\n"
            f"{release_notes}\n\n---"
        )

    start_idx = match.start(1)
    end_idx = match.end(2)

    return changelog_content[:start_idx] + new_release_section + changelog_content[end_idx:]


def extract_release_notes(changelog_content: str, version: str) -> str:
    """Extracts the release notes for a specific version from CHANGELOG content."""
    clean_version = version.lstrip("vV")
    pattern = re.compile(
        rf"##\s*\[{re.escape(clean_version)}\](?:[^\n]*)\n([\s\S]*?)(?=(\n---\s*\n##|\n##\s*\[|\Z))",
        re.IGNORECASE,
    )
    match = pattern.search(changelog_content)
    if match:
        notes = match.group(1).strip()
        # Clean trailing separators
        return re.sub(r"\s*---\s*$", "", notes).strip()
    return ""


def run_command(
    cmd: list[str] | str,
    cwd: str = ".",
    check: bool = True,
) -> subprocess.CompletedProcess:
    """Runs an external command and captures standard output and error."""
    is_shell = isinstance(cmd, str)
    return subprocess.run(
        cmd,
        cwd=cwd,
        capture_output=True,
        text=True,
        check=check,
        shell=is_shell,
    )


def get_all_git_tags(cwd: str = ".") -> list[str]:
    """Retrieves all git tags in the repository sorted by version refname."""
    try:
        res = run_command(["git", "tag", "--sort=-v:refname"], cwd=cwd, check=False)
        if res.returncode == 0 and res.stdout.strip():
            return [t.strip() for t in res.stdout.strip().splitlines() if t.strip()]
    except Exception:
        pass
    return []


def get_latest_git_tag(cwd: str = ".") -> Optional[str]:
    """Retrieves the latest valid SemVer git tag in the repository sorted by version refname."""
    tags = get_all_git_tags(cwd=cwd)
    for tag in tags:
        try:
            parse_semver(tag)
            return tag
        except ValueError:
            continue
    return None



def get_current_pom_version(pom_path: str = "pom.xml") -> str:
    """Reads the current project version from the root pom.xml."""
    tree = ET.parse(pom_path)
    root = tree.getroot()

    # Maven POM default namespace
    ns = {"mvn": "http://maven.apache.org/POM/4.0.0"}

    # Look for version directly under project
    version_elem = root.find("mvn:version", ns)
    if version_elem is None:
        version_elem = root.find("version")

    if version_elem is not None and version_elem.text:
        return version_elem.text.strip()

    # Fallback to parent version if top-level version is missing
    parent_version = root.find("mvn:parent/mvn:version", ns)
    if parent_version is None:
        parent_version = root.find("parent/version")

    if parent_version is not None and parent_version.text:
        return parent_version.text.strip()

    raise ValueError(f"Could not find <version> in {pom_path}")


def get_commits_since_tag(
    tag: Optional[str] = None, cwd: str = "."
) -> list[CommitInfo]:
    """Retrieves commits since the specified git tag, or all commits if tag is None."""
    # Use ASCII unit and record separators to safely parse multi-line commit messages
    # %x1f is unit separator, %x1e is record separator
    if tag:
        cmd = ["git", "log", f"{tag}..HEAD", "--pretty=format:%H%x1f%an%x1f%ae%x1f%B%x1e"]
    else:
        cmd = ["git", "log", "--pretty=format:%H%x1f%an%x1f%ae%x1f%B%x1e"]

    res = run_command(cmd, cwd=cwd, check=False)
    if res.returncode != 0 or not res.stdout.strip():
        return []

    commits: list[CommitInfo] = []
    records = res.stdout.split("\x1e")
    for record in records:
        record = record.strip()
        if not record:
            continue
        parts = record.split("\x1f", 3)
        commit_hash = parts[0].strip()
        author_name = parts[1].strip() if len(parts) > 1 else ""
        author_email = parts[2].strip() if len(parts) > 2 else ""
        commit_body = parts[3].strip() if len(parts) > 3 else ""
        commits.append(
            CommitInfo.from_raw(
                commit_hash=commit_hash,
                message=commit_body,
                author_name=author_name,
                author_email=author_email,
            )
        )

    return commits


def update_pom_versions(new_version: str, repo_root: str = ".") -> None:
    """Updates the project and module versions across all pom.xml files using Maven."""
    mvn_executable = "./mvnw" if os.path.isfile(os.path.join(repo_root, "mvnw")) else "mvn"
    cmd = [
        mvn_executable,
        "versions:set",
        f"-DnewVersion={new_version}",
        "-DgenerateBackupPoms=false",
    ]
    run_command(cmd, cwd=repo_root, check=True)


def write_github_output(
    outputs: dict[str, str], github_output_path: Optional[str] = None
) -> None:
    """Writes key-value pairs to the GitHub Actions output file."""
    output_file = github_output_path or os.environ.get("GITHUB_OUTPUT")
    if not output_file:
        return

    with open(output_file, "a", encoding="utf-8") as f:
        for key, value in outputs.items():
            if "\n" in value:
                f.write(f"{key}<<EOF\n{value}\nEOF\n")
            else:
                f.write(f"{key}={value}\n")


def write_release_notes_file(notes: str, output_path: str = "release_notes.md") -> None:
    """Writes the release notes to a markdown file for GitHub Release creation."""
    sanitized = sanitize_mentions(notes)
    Path(output_path).write_text(sanitized.strip() + "\n", encoding="utf-8")


def main(argv: Optional[list[str]] = None) -> int:
    parser = argparse.ArgumentParser(description="gPatri Release Automation Engine")
    parser.add_argument("--repo-root", default=".", help="Root path of the repository")
    parser.add_argument("--pom-path", default="pom.xml", help="Path to root pom.xml")
    parser.add_argument(
        "--changelog-path", default="CHANGELOG.md", help="Path to CHANGELOG.md"
    )
    parser.add_argument(
        "--notes-output", default="release_notes.md", help="Output path for release notes"
    )
    parser.add_argument(
        "--force-bump",
        choices=["major", "minor", "patch", "none"],
        default=None,
        help="Force a specific bump type instead of calculating from commits",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Calculate versions and changelog without writing changes to disk",
    )
    args = parser.parse_args(argv)

    repo_root = os.path.abspath(args.repo_root)
    pom_path = os.path.join(repo_root, args.pom_path)
    changelog_path = os.path.join(repo_root, args.changelog_path)
    notes_output_path = os.path.join(repo_root, args.notes_output)

    print(f"==> Inspecting repository at {repo_root}")

    # 1. Determine current version from POM and latest git tag
    pom_version: Optional[str] = None
    try:
        pom_version = get_current_pom_version(pom_path)
        print(f"==> Current POM version: {pom_version}")
    except Exception as e:
        print(f"==> Warning: Could not read POM version: {e}")

    # 2. Get latest git tag and commits
    latest_tag = get_latest_git_tag(repo_root)
    print(f"==> Latest Git tag: {latest_tag or 'None (initial release)'}")

    tag_version = latest_tag.lstrip("vV") if latest_tag else None

    if pom_version and tag_version:
        try:
            current_version = max_semver(pom_version, tag_version)
        except ValueError:
            current_version = pom_version
    elif pom_version:
        current_version = pom_version
    elif tag_version:
        current_version = tag_version
    else:
        current_version = "0.0.0"

    print(f"==> Base version for release calculation: {current_version}")

    commits = get_commits_since_tag(latest_tag, cwd=repo_root)
    print(f"==> Found {len(commits)} commit(s) since last tag.")

    # 3. Determine bump type
    if args.force_bump:
        bump_type = args.force_bump
        print(f"==> Force bump type applied: {bump_type}")
    else:
        bump_type = determine_bump_type(commits)
        print(f"==> Calculated bump type from commits: {bump_type}")

    if bump_type == "none" and not args.force_bump:
        print("==> No bump required. Skipping release.")
        write_github_output(
            {
                "has_release": "false",
                "version": current_version,
                "bump_type": "none",
            }
        )
        return 0

    # 4. Calculate next version and guarantee tag uniqueness
    all_existing_tags = set(get_all_git_tags(repo_root))
    next_version = calculate_next_version(current_version, bump_type)
    tag_name = f"v{next_version}"

    while tag_name in all_existing_tags:
        print(f"==> Warning: Tag {tag_name} already exists. Advancing patch...")
        next_version = calculate_next_version(next_version, "patch")
        tag_name = f"v{next_version}"

    print(f"==> Target Release Version: {next_version} ({tag_name})")

    # 5. Process CHANGELOG.md
    changelog_file = Path(changelog_path)
    if changelog_file.exists():
        changelog_content = changelog_file.read_text(encoding="utf-8")
        fallback_notes = generate_fallback_notes(commits)
        updated_changelog = promote_changelog(
            changelog_content,
            next_version=next_version,
            fallback_notes=fallback_notes,
            commits=commits,
        )
        release_notes = extract_release_notes(updated_changelog, next_version)
    else:
        print(f"==> Warning: {changelog_path} not found. Creating standalone notes.")
        release_notes = generate_fallback_notes(commits)
        updated_changelog = f"# Changelog\n\n## [{next_version}] - {datetime.now().strftime('%Y-%m-%d')}\n\n{release_notes}\n"

    if not release_notes.strip():
        release_notes = generate_fallback_notes(commits) or "- Melhorias e correções gerais."

    print(f"==> Release Notes Generated:\n{release_notes}")

    # 6. Apply changes if not dry run
    if not args.dry_run:
        print(f"==> Writing updated {changelog_path}")
        changelog_file.write_text(updated_changelog, encoding="utf-8")

        print(f"==> Writing {notes_output_path}")
        write_release_notes_file(release_notes, notes_output_path)

        print(f"==> Updating Maven versions to {next_version}")
        try:
            update_pom_versions(next_version, repo_root=repo_root)
        except Exception as err:
            print(f"==> Warning: Failed to run mvn versions:set: {err}")
    else:
        print("==> Dry run mode active: skipped writing changes.")

    # 7. Set GitHub outputs
    write_github_output(
        {
            "has_release": "true",
            "version": next_version,
            "tag_name": tag_name,
            "bump_type": bump_type,
        }
    )

    print("==> Release automation completed successfully.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
