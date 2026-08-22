#!/usr/bin/env python3
"""
Unit tests for the release automation engine (.github/scripts/release.py).
Tests parse_semver, format_semver, CommitInfo, determine_bump_type,
calculate_next_version, promote_changelog, generate_fallback_notes,
get_current_pom_version, and write_github_output.
"""

import os
import sys
import tempfile
import unittest
from unittest.mock import MagicMock, patch

# Ensure .github/scripts is in python path
sys.path.insert(0, os.path.dirname(__file__))

import release
from release import (  # type: ignore # noqa: E402
    CommitInfo,
    SemVer,
    calculate_next_version,
    determine_bump_type,
    extract_release_notes,
    format_semver,
    generate_fallback_notes,
    get_commits_since_tag,
    get_current_pom_version,
    get_latest_git_tag,
    main,
    parse_semver,
    promote_changelog,
    run_command,
    update_pom_versions,
    write_github_output,
    write_release_notes_file,
)


class TestSemVerParsingAndFormatting(unittest.TestCase):
    def test_parse_semver_standard(self):
        semver = parse_semver("1.2.3")
        self.assertEqual(semver.major, 1)
        self.assertEqual(semver.minor, 2)
        self.assertEqual(semver.patch, 3)
        self.assertIsNone(semver.prerelease)
        # Tuple unpacking support
        major, minor, patch, prerelease = semver
        self.assertEqual((major, minor, patch, prerelease), (1, 2, 3, None))

    def test_parse_semver_with_v_prefix(self):
        semver_lower = parse_semver("v0.3.0")
        self.assertEqual(semver_lower, SemVer(0, 3, 0, None))

        semver_upper = parse_semver("V2.1.5")
        self.assertEqual(semver_upper, SemVer(2, 1, 5, None))

    def test_parse_semver_with_prerelease(self):
        semver_snapshot = parse_semver("0.4.0-SNAPSHOT")
        self.assertEqual(semver_snapshot, SemVer(0, 4, 0, "SNAPSHOT"))

        semver_rc = parse_semver("v1.0.0-rc.1")
        self.assertEqual(semver_rc, SemVer(1, 0, 0, "rc.1"))

    def test_parse_semver_invalid(self):
        invalid_versions = ["", "abc", "1.2", "1.2.3.4", "v1.2.x", "none"]
        for invalid in invalid_versions:
            with self.subTest(invalid=invalid):
                with self.assertRaises(ValueError):
                    parse_semver(invalid)

    def test_format_semver(self):
        self.assertEqual(format_semver(1, 2, 3), "1.2.3")
        self.assertEqual(format_semver(0, 3, 0, prefix_v=True), "v0.3.0")
        self.assertEqual(format_semver(1, 0, 0, prerelease="SNAPSHOT"), "1.0.0-SNAPSHOT")
        self.assertEqual(
            format_semver(2, 0, 1, prerelease="rc.2", prefix_v=True), "v2.0.1-rc.2"
        )


class TestCommitInfoParsing(unittest.TestCase):
    def test_parse_conventional_feat(self):
        commit = CommitInfo.from_raw("abc1234", "feat(auth): add JWT token refresh")
        self.assertEqual(commit.hash, "abc1234")
        self.assertEqual(commit.type, "feat")
        self.assertEqual(commit.scope, "auth")
        self.assertEqual(commit.description, "add JWT token refresh")
        self.assertFalse(commit.breaking)

    def test_parse_conventional_fix_without_scope(self):
        commit = CommitInfo.from_raw("def5678", "fix: prevent NullPointerException in user lookup")
        self.assertEqual(commit.type, "fix")
        self.assertIsNone(commit.scope)
        self.assertEqual(commit.description, "prevent NullPointerException in user lookup")
        self.assertFalse(commit.breaking)

    def test_parse_breaking_change_with_bang(self):
        commit = CommitInfo.from_raw("789abcd", "feat(api)!: drop legacy v1 endpoints")
        self.assertEqual(commit.type, "feat")
        self.assertEqual(commit.scope, "api")
        self.assertEqual(commit.description, "drop legacy v1 endpoints")
        self.assertTrue(commit.breaking)

    def test_parse_breaking_change_in_body(self):
        msg = (
            "refactor(database): restructure permission tables\n\n"
            "BREAKING CHANGE: permissions now use UUID instead of integer IDs"
        )
        commit = CommitInfo.from_raw("1112223", msg)
        self.assertEqual(commit.type, "refactor")
        self.assertEqual(commit.scope, "database")
        self.assertTrue(commit.breaking)

    def test_parse_non_conventional_commit(self):
        commit = CommitInfo.from_raw("3334445", "update readme documentation")
        self.assertEqual(commit.type, "")
        self.assertIsNone(commit.scope)
        self.assertEqual(commit.description, "update readme documentation")
        self.assertFalse(commit.breaking)


class TestDetermineBumpType(unittest.TestCase):
    def test_empty_commits_returns_none(self):
        self.assertEqual(determine_bump_type([]), "none")

    def test_only_patch_types(self):
        commits = [
            CommitInfo(hash="1", type="fix", description="fix bug"),
            CommitInfo(hash="2", type="chore", description="update dependencies"),
            CommitInfo(hash="3", type="docs", description="fix typo"),
        ]
        self.assertEqual(determine_bump_type(commits), "patch")

    def test_feat_triggers_minor(self):
        commits = [
            CommitInfo(hash="1", type="fix", description="fix bug"),
            CommitInfo(hash="2", type="feat", description="add new feature"),
            CommitInfo(hash="3", type="docs", description="update doc"),
        ]
        self.assertEqual(determine_bump_type(commits), "minor")

    def test_breaking_change_triggers_major(self):
        commits = [
            CommitInfo(hash="1", type="fix", description="fix bug"),
            CommitInfo(hash="2", type="feat", description="add new feature"),
            CommitInfo(hash="3", type="feat", description="breaking change", breaking=True),
        ]
        self.assertEqual(determine_bump_type(commits), "major")

    def test_unconventional_commits_default_to_patch(self):
        commits = [
            CommitInfo(hash="1", type="", description="miscellaneous update"),
        ]
        self.assertEqual(determine_bump_type(commits), "patch")


class TestCalculateNextVersion(unittest.TestCase):
    def test_patch_bump(self):
        self.assertEqual(calculate_next_version("0.3.0", "patch"), "0.3.1")
        self.assertEqual(calculate_next_version("v1.2.3", "patch"), "1.2.4")

    def test_minor_bump(self):
        self.assertEqual(calculate_next_version("0.3.0", "minor"), "0.4.0")
        self.assertEqual(calculate_next_version("1.5.9", "minor"), "1.6.0")

    def test_major_bump(self):
        self.assertEqual(calculate_next_version("0.3.0", "major"), "1.0.0")
        self.assertEqual(calculate_next_version("2.1.4", "major"), "3.0.0")

    def test_none_bump(self):
        self.assertEqual(calculate_next_version("0.3.0", "none"), "0.3.0")

    def test_with_prerelease(self):
        self.assertEqual(
            calculate_next_version("0.3.0", "minor", prerelease="SNAPSHOT"),
            "0.4.0-SNAPSHOT",
        )

    def test_invalid_bump_type(self):
        with self.assertRaises(ValueError):
            calculate_next_version("0.3.0", "invalid_bump")


class TestGenerateFallbackNotes(unittest.TestCase):
    def test_generate_categorized_notes(self):
        commits = [
            CommitInfo(hash="a1", type="feat", scope="usuarios", description="adicionar RBAC"),
            CommitInfo(hash="b2", type="fix", scope="patrimonio", description="corrigir calculo"),
            CommitInfo(hash="c3", type="refactor", description="limpeza de codigo"),
            CommitInfo(hash="d4", type="docs", description="atualizar README"),
            CommitInfo(hash="e5", type="feat", description="remover api legada", breaking=True),
        ]
        notes = generate_fallback_notes(commits)

        self.assertIn("### ⚠️ Breaking Changes", notes)
        self.assertIn("- remover api legada", notes)
        self.assertIn("### Adicionado", notes)
        self.assertIn("- **usuarios**: adicionar RBAC", notes)
        self.assertIn("### Corrigido", notes)
        self.assertIn("- **patrimonio**: corrigir calculo", notes)
        self.assertIn("### Modificado", notes)
        self.assertIn("- limpeza de codigo", notes)


class TestPromoteChangelog(unittest.TestCase):
    def setUp(self):
        self.sample_changelog = (
            "# Changelog\n\n"
            "Todas as alterações notáveis neste projeto serão documentadas neste arquivo.\n\n"
            "---\n\n"
            "## [Não Lançado]\n\n"
            "### Adicionado\n"
            "- Suporte a autenticação OAuth2.\n\n"
            "### Corrigido\n"
            "- Tratamento de erro 404 em rotas inexistentes.\n\n"
            "---\n\n"
            "## [0.3.0] - 2026-08-22\n\n"
            "### Adicionado\n"
            "- RBAC Dinâmico.\n"
        )

    def test_promote_changelog_with_unreleased_content(self):
        promoted = promote_changelog(
            self.sample_changelog,
            next_version="0.4.0",
            release_date="2026-08-23",
        )

        self.assertIn("## [Não Lançado]\n\n---", promoted)
        self.assertIn("## [0.4.0] - 2026-08-23", promoted)
        self.assertIn("### Adicionado\n- Suporte a autenticação OAuth2.", promoted)
        self.assertIn("### Corrigido\n- Tratamento de erro 404 em rotas inexistentes.", promoted)
        self.assertIn("## [0.3.0] - 2026-08-22", promoted)

    def test_promote_changelog_when_unreleased_is_empty_uses_fallback(self):
        empty_unreleased_changelog = (
            "# Changelog\n\n"
            "---\n\n"
            "## [Não Lançado]\n\n"
            "---\n\n"
            "## [0.3.0] - 2026-08-22\n\n"
            "### Adicionado\n"
            "- RBAC Dinâmico.\n"
        )
        fallback = "### Adicionado\n- Nova funcionalidade automática."

        promoted = promote_changelog(
            empty_unreleased_changelog,
            next_version="0.4.0",
            release_date="2026-08-23",
            fallback_notes=fallback,
        )

        self.assertIn("## [Não Lançado]\n\n---", promoted)
        self.assertIn("## [0.4.0] - 2026-08-23", promoted)
        self.assertIn("### Adicionado\n- Nova funcionalidade automática.", promoted)
        self.assertIn("## [0.3.0] - 2026-08-22", promoted)

    def test_extract_release_notes(self):
        changelog = (
            "# Changelog\n\n"
            "## [0.4.0] - 2026-08-23\n\n"
            "### Adicionado\n"
            "- Nova funcionalidade.\n\n"
            "---\n\n"
            "## [0.3.0] - 2026-08-22\n\n"
            "### Modificado\n"
            "- Ajustes gerais.\n"
        )
        notes = extract_release_notes(changelog, "0.4.0")
        self.assertIn("### Adicionado\n- Nova funcionalidade.", notes)
        self.assertNotIn("0.3.0", notes)


class TestPomVersionAndGithubOutput(unittest.TestCase):
    def test_get_current_pom_version(self):
        sample_pom = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>br.edu.ifg.numbers</groupId>
    <artifactId>gPatri</artifactId>
    <version>0.3.0</version>
    <packaging>pom</packaging>
</project>"""
        with tempfile.NamedTemporaryFile(mode="w", delete=False, suffix=".xml") as f:
            f.write(sample_pom)
            temp_path = f.name

        try:
            version = get_current_pom_version(temp_path)
            self.assertEqual(version, "0.3.0")
        finally:
            os.remove(temp_path)

    def test_write_github_output(self):
        with tempfile.NamedTemporaryFile(mode="w", delete=False) as f:
            temp_path = f.name

        try:
            outputs = {
                "has_release": "true",
                "version": "0.4.0",
                "bump_type": "minor",
            }
            write_github_output(outputs, github_output_path=temp_path)

            with open(temp_path, "r", encoding="utf-8") as f:
                content = f.read()

            self.assertIn("has_release=true\n", content)
            self.assertIn("version=0.4.0\n", content)
            self.assertIn("bump_type=minor\n", content)
        finally:
            os.remove(temp_path)


class TestGitAndCliFunctions(unittest.TestCase):
    @patch("release.run_command")
    def test_get_latest_git_tag_found(self, mock_run):
        mock_run.return_value = MagicMock(returncode=0, stdout="v0.3.0\nv0.2.0\nv0.1.0\n")
        tag = release.get_latest_git_tag()
        self.assertEqual(tag, "v0.3.0")

    @patch("release.run_command")
    def test_get_latest_git_tag_none(self, mock_run):
        mock_run.return_value = MagicMock(returncode=0, stdout="")
        tag = release.get_latest_git_tag()
        self.assertIsNone(tag)

    @patch("release.run_command")
    def test_get_commits_since_tag(self, mock_run):
        # Format: %H\x1f%B\x1e
        sample_log = (
            "hash1\x1ffeat(auth): add OAuth2 support\x1e"
            "hash2\x1ffix(db): fix connection pool leak\n\nDetailed body\x1e"
        )
        mock_run.return_value = MagicMock(returncode=0, stdout=sample_log)

        commits = release.get_commits_since_tag("v0.3.0")
        self.assertEqual(len(commits), 2)
        self.assertEqual(commits[0].hash, "hash1")
        self.assertEqual(commits[0].type, "feat")
        self.assertEqual(commits[0].scope, "auth")
        self.assertEqual(commits[1].hash, "hash2")
        self.assertEqual(commits[1].type, "fix")
        self.assertEqual(commits[1].scope, "db")

    @patch("subprocess.run")
    def test_run_command(self, mock_subprocess_run):
        mock_subprocess_run.return_value = MagicMock(returncode=0, stdout="output")
        res = release.run_command(["echo", "hello"])
        self.assertEqual(res.returncode, 0)
        self.assertEqual(res.stdout, "output")

    @patch("release.run_command")
    def test_update_pom_versions(self, mock_run):
        mock_run.return_value = MagicMock(returncode=0)
        release.update_pom_versions("0.4.0")
        mock_run.assert_called_once()
        args = mock_run.call_args[0][0]
        self.assertIn("versions:set", args)
        self.assertIn("-DnewVersion=0.4.0", args)

    def test_write_release_notes_file(self):
        with tempfile.NamedTemporaryFile(mode="w", delete=False) as f:
            temp_path = f.name
        try:
            release.write_release_notes_file("### Adicionado\n- Feature X", temp_path)
            with open(temp_path, "r", encoding="utf-8") as f:
                content = f.read()
            self.assertEqual(content, "### Adicionado\n- Feature X\n")
        finally:
            os.remove(temp_path)

    @patch("release.get_current_pom_version", return_value="0.3.0")
    @patch("release.get_latest_git_tag", return_value="v0.3.0")
    @patch("release.get_commits_since_tag")
    @patch("release.update_pom_versions")
    def test_main_cli_dry_run(self, mock_update_pom, mock_get_commits, mock_tag, mock_pom):
        mock_get_commits.return_value = [
            CommitInfo(hash="1", type="feat", description="nova feature")
        ]

        with tempfile.TemporaryDirectory() as temp_dir:
            changelog_file = os.path.join(temp_dir, "CHANGELOG.md")
            with open(changelog_file, "w", encoding="utf-8") as f:
                f.write("# Changelog\n\n## [Não Lançado]\n\n---\n\n## [0.3.0] - 2026-08-22\n")

            code = release.main([
                "--repo-root", temp_dir,
                "--dry-run",
            ])

            self.assertEqual(code, 0)
            mock_update_pom.assert_not_called()

    @patch("release.get_current_pom_version", return_value="0.3.0")
    @patch("release.get_latest_git_tag", return_value="v0.3.0")
    @patch("release.get_commits_since_tag", return_value=[])
    def test_main_cli_no_commits_no_release(self, mock_get_commits, mock_tag, mock_pom):
        code = release.main(["--dry-run"])
        self.assertEqual(code, 0)


if __name__ == "__main__":
    unittest.main()
