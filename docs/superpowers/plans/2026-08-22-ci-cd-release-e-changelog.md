# CI/CD Release Automatizado, Versionamento Semântico e Manutenção de Changelog - Plano de Implementação

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implementar o fluxo automatizado no GitHub Actions para bump de versão SemVer, atualização coordenada de todos os `pom.xml` Maven, promoção da seção `[Não Lançado]` no `CHANGELOG.md`, publicação de release e criação de diretrizes agnósticas para agentes de IA e contribuidores.

**Architecture:** O ecossistema é composto por um script Python 3 determinístico e testado via TDD (`.github/scripts/release.py`), acionado pelo workflow `.github/workflows/release.yml` no merge na `main`. O script sincroniza os 5 `pom.xml` com `./mvnw versions:set` e promove o `CHANGELOG.md` no padrão Keep a Changelog. As diretrizes para agentes e humanos são formalizadas em `AGENTS.md`, `.agents/rules/changelog-rules.md` e `.github/PULL_REQUEST_TEMPLATE.md`.

**Tech Stack:** GitHub Actions, Python 3 (unittest, re, subprocess, pathlib), Maven Wrapper (`./mvnw versions:set`), Java 25, Git, Markdown (Keep a Changelog).

**Spec:** [`docs/superpowers/specs/2026-08-22-ci-cd-release-e-changelog-design.md`](file:///home/thiago/Projetos/gPatri/docs/superpowers/specs/2026-08-22-ci-cd-release-e-changelog-design.md)

## Global Constraints

- Compatibilidade estrita com Java 25 e Maven multi-módulo (`gPatri`, `ms-patrimonio`, `ms-usuarios`, `ms-eureka`, `ms-gateway`).
- Padrão [Keep a Changelog 1.0.0](https://keepachangelog.com/pt-BR/1.0.0/) em português (`### Adicionado`, `### Modificado`, `### Corrigido`, `### Removido`, `### Segurança`).
- Padrão [Conventional Commits 1.0.0](https://www.conventionalcommits.org/pt-br/v1.0.0/) para cálculo de SemVer.
- O commit gerado pela automação deve conter `[skip ci]` para prevenir loops de CI.
- As regras de agentes em `AGENTS.md` e `.agents/rules/changelog-rules.md` devem ser estritamente generalistas e agnósticas de ferramentas proprietárias.

---

### Task 1: Diretrizes Generalistas para Agentes e Template de Pull Request

**Files:**
- Create: `AGENTS.md`
- Create: `.agents/rules/changelog-rules.md`
- Create: `.github/PULL_REQUEST_TEMPLATE.md`

**Interfaces:**
- Consumes: Estrutura do projeto gPatri e especificação de changelog.
- Produces: Contrato de regras para agentes e checklist de PR no GitHub.

- [ ] **Step 1: Criar o arquivo de regras modular generalista `.agents/rules/changelog-rules.md`**

Criar o arquivo com as regras essenciais de changelog e conventional commits para ambientes que leem regras na pasta `.agents/`:

```markdown
# Regras de Manutenção de Changelog e Commits

## 1. Regra Obrigatória do Changelog
Toda tarefa ou branch que adicionar código, alterar regras existentes, corrigir defeitos, refatorar ou modificar dependências **DEVE obrigatoriamente** registrar as alterações no arquivo `CHANGELOG.md` sob a seção `## [Não Lançado]`.

## 2. Categorias Permitidas (Keep a Changelog em Português)
- `### Adicionado`: Novas funcionalidades, novos endpoints, novas entidades, novos scripts Flyway.
- `### Modificado`: Alterações em comportamentos existentes, refatorações, atualização de dependências.
- `### Corrigido`: Correções de bugs ou tratamento de exceções.
- `### Removido`: Recursos, rotas ou código descontinuado/removido.
- `### Segurança`: Correções de vulnerabilidades ou melhorias em autenticação/autorização.

## 3. Nível de Detalhe Exigido
- Especificar o microserviço impactado (ex: `ms-usuarios`, `ms-patrimonio`).
- Referenciar nomes de classes, DTOs, endpoints REST e arquivos de migração Flyway.

## 4. Padrão de Commits (Conventional Commits)
Utilizar a convenção de commits para todas as alterações:
- `feat(modulo): ...` -> Nova funcionalidade (MINOR)
- `fix(modulo): ...` -> Correção de bug (PATCH)
- `refactor(modulo): ...` -> Refatoração interna (PATCH)
- `perf(modulo): ...` -> Desempenho (PATCH)
- `docs: ...` -> Documentação (PATCH)
- `chore: ...` -> Manutenção, dependências, build (PATCH)
- `feat!: ...` ou `fix!: ...` -> Quebra de compatibilidade (MAJOR)
```

- [ ] **Step 2: Criar o guia canônico universal `AGENTS.md` na raiz**

Criar `AGENTS.md` contendo as orientações completas para qualquer agente de IA ou desenvolvedor:

```markdown
# Guia de Desenvolvimento e Instruções para Agentes de IA - gPatri

Este documento estabelece as diretrizes canônicas para qualquer agente de inteligência artificial (Cursor, Claude Code, Antigravity, Copilot, Cline, Aider, etc.) e desenvolvedores humanos trabalhando no monorepo **gPatri**.

---

## 1. Visão Geral da Arquitetura
O **gPatri** é um sistema de gerenciamento de patrimônio estruturado como monorepo Maven multi-módulo em **Java 25** e Spring Boot 4:
- `ms-eureka`: Service Discovery (Netflix Eureka).
- `ms-gateway`: API Gateway com Spring Cloud Gateway.
- `ms-usuarios`: Gestão de usuários, perfis, permissões dinâmicas (RBAC) e autenticação JWT.
- `ms-patrimonio`: Gestão patrimonial, inventário, movimentações e empréstimos.

---

## 2. Regra de Ouro do CHANGELOG
> **Toda e qualquer alteração de código DEVE ser documentada na seção `## [Não Lançado]` do arquivo [`CHANGELOG.md`](CHANGELOG.md).**

1. **Nunca crie novos cabeçalhos de versão manual**: A promoção de versão (`## [X.Y.Z] - Data`) é realizada **exclusivamente pelo pipeline de CI/CD**.
2. **Adicione suas mudanças sob `## [Não Lançado]`** respeitando os subtítulos do padrão [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/):
   - `### Adicionado`
   - `### Modificado`
   - `### Corrigido`
   - `### Removido`
   - `### Segurança`
3. **Seja específico e técnico**: Mencione os microserviços, classes, controllers, entidades, migrações Flyway e DTOs alterados.

---

## 3. Padrão de Commits (Conventional Commits 1.0.0)
Todos os commits devem seguir a especificação de Conventional Commits:
- `feat(<escopo>): <descrição>`: Nova funcionalidade (incrementa MINOR).
- `fix(<escopo>): <descrição>`: Correção de defeito (incrementa PATCH).
- `refactor(<escopo>): <descrição>`: Refatoração sem mudança comportamental externa (incrementa PATCH).
- `perf(<escopo>): <descrição>`: Otimização de performance (incrementa PATCH).
- `chore(<escopo>): <descrição>`: Ajustes de build, dependências ou tarefas operacionais (incrementa PATCH).
- `docs(<escopo>): <descrição>`: Alterações em documentação (incrementa PATCH).
- `test(<escopo>): <descrição>`: Adição ou modificação de testes (incrementa PATCH).
- `feat!: <descrição>` ou `BREAKING CHANGE: <descrição>`: Mudança que quebra compatibilidade (incrementa MAJOR).

---

## 4. Higiene e Testes
- Sempre execute `./mvnw test` antes de concluir uma tarefa para garantir que todos os módulos compilam e passam nos testes.
- Não introduza código morto, dependências desnecessárias ou commits sem formatação adequada.
```

- [ ] **Step 3: Criar o template de Pull Request `.github/PULL_REQUEST_TEMPLATE.md`**

Criar `.github/PULL_REQUEST_TEMPLATE.md`:

```markdown
## Descrição
<!-- Descreva de forma concisa o objetivo e o escopo desta alteração -->

## Tipo de Alteração
- [ ] `feat`: Nova funcionalidade
- [ ] `fix`: Correção de bug
- [ ] `refactor`: Refatoração de código
- [ ] `perf`: Melhoria de desempenho
- [ ] `docs`: Alteração em documentação
- [ ] `test`: Adição ou ajuste de testes
- [ ] `chore` / `ci`: Tarefas de build, dependências ou automação
- [ ] ⚠️ **BREAKING CHANGE**: Mudança que quebra compatibilidade anterior

## Microserviços / Módulos Impactados
- [ ] `ms-usuarios`
- [ ] `ms-patrimonio`
- [ ] `ms-eureka`
- [ ] `ms-gateway`
- [ ] `raiz` / `ci` / `docker`

## Checklist de Qualidade e Rastreabilidade
- [ ] As mensagens de commit seguem a especificação **Conventional Commits** (ex: `feat(ms-usuarios): ...`).
- [ ] A seção `## [Não Lançado]` do [`CHANGELOG.md`](CHANGELOG.md) foi devidamente atualizada com as alterações desta branch.
- [ ] Todos os testes automatizados passaram localmente (`./mvnw test`).
- [ ] Novas migrações Flyway (se aplicável) foram validadas.
```

- [ ] **Step 4: Verificar arquivos criados e comitar**

```bash
git add AGENTS.md .agents/rules/changelog-rules.md .github/PULL_REQUEST_TEMPLATE.md
git commit -m "docs: adicionar diretrizes de changelog para agentes e template de PR"
```

---

### Task 2: Script de Cálculo SemVer, Promoção de Changelog e Sincronização Maven

**Files:**
- Create: `.github/scripts/test_release.py`
- Create: `.github/scripts/release.py`

**Interfaces:**
- Consumes: Histórico Git, arquivo `CHANGELOG.md`, arquivo `pom.xml`.
- Produces: Bump de versão no SemVer, atualização de `CHANGELOG.md`, `release_notes.md`, execução de `./mvnw versions:set` e variáveis para `$GITHUB_OUTPUT`.

- [ ] **Step 1: Escrever testes unitários em `.github/scripts/test_release.py`**

Criar a suite de testes cobrindo todas as funções da engine de release:

```python
import unittest
from datetime import date
from .release import (
    parse_semver,
    format_semver,
    determine_bump_type,
    calculate_next_version,
    promote_changelog,
    generate_fallback_notes,
    CommitInfo,
)


class TestReleaseEngine(unittest.TestCase):

    def test_parse_and_format_semver(self):
        self.assertEqual(parse_semver("0.3.0"), (0, 3, 0))
        self.assertEqual(parse_semver("v1.2.4"), (1, 2, 4))
        self.assertEqual(format_semver(1, 2, 3), "1.2.3")

    def test_determine_bump_type_breaking_change(self):
        commits = [
            CommitInfo("hash1", "feat!: breaking change endpoint", ""),
            CommitInfo("hash2", "fix: small bug", ""),
        ]
        self.assertEqual(determine_bump_type(commits), "MAJOR")

        commits_body_breaking = [
            CommitInfo("hash1", "refactor: rename table", "BREAKING CHANGE: schema altered"),
        ]
        self.assertEqual(determine_bump_type(commits_body_breaking), "MAJOR")

    def test_determine_bump_type_minor(self):
        commits = [
            CommitInfo("hash1", "feat(ms-usuarios): novo endpoint", ""),
            CommitInfo("hash2", "fix: ajuste em dto", ""),
        ]
        self.assertEqual(determine_bump_type(commits), "MINOR")

    def test_determine_bump_type_patch(self):
        commits = [
            CommitInfo("hash1", "fix(ms-patrimonio): corrigir calculo", ""),
            CommitInfo("hash2", "chore: atualizar deps", ""),
        ]
        self.assertEqual(determine_bump_type(commits), "PATCH")

    def test_determine_bump_type_ignored_commits(self):
        commits = [
            CommitInfo("hash1", "chore(release): v0.3.0 [skip ci]", ""),
        ]
        self.assertIsNone(determine_bump_type(commits))

    def test_calculate_next_version(self):
        self.assertEqual(calculate_next_version("0.3.0", "MAJOR"), "1.0.0")
        self.assertEqual(calculate_next_version("0.3.0", "MINOR"), "0.4.0")
        self.assertEqual(calculate_next_version("0.3.0", "PATCH"), "0.3.1")

    def test_generate_fallback_notes(self):
        commits = [
            CommitInfo("h1", "feat(usuarios): login social", ""),
            CommitInfo("h2", "fix(patrimonio): corrigir status", ""),
        ]
        notes = generate_fallback_notes(commits)
        self.assertIn("### Adicionado", notes)
        self.assertIn("- feat(usuarios): login social", notes)
        self.assertIn("### Corrigido", notes)
        self.assertIn("- fix(patrimonio): corrigir status", notes)

    def test_promote_changelog_with_existing_unreleased(self):
        sample_changelog = """# Changelog

## [Não Lançado]

### Adicionado
- Nova funcionalidade X

---

## [0.3.0] - 2026-08-22
"""
        today = date.today().strftime("%Y-%m-%d")
        new_content, release_notes = promote_changelog(
            changelog_text=sample_changelog,
            new_version="0.4.0",
            commits=[],
            release_date=today
        )
        self.assertIn("## [Não Lançado]\n\n---\n\n## [0.4.0] - " + today, new_content)
        self.assertIn("### Adicionado\n- Nova funcionalidade X", new_content)
        self.assertIn("### Adicionado\n- Nova funcionalidade X", release_notes)

    def test_promote_changelog_with_empty_unreleased_uses_fallback(self):
        sample_changelog = """# Changelog

## [Não Lançado]

---

## [0.3.0] - 2026-08-22
"""
        commits = [CommitInfo("h1", "feat: suporte a oauth2", "")]
        today = date.today().strftime("%Y-%m-%d")
        new_content, release_notes = promote_changelog(
            changelog_text=sample_changelog,
            new_version="0.4.0",
            commits=commits,
            release_date=today
        )
        self.assertIn("## [0.4.0] - " + today, new_content)
        self.assertIn("### Adicionado\n- feat: suporte a oauth2", new_content)
        self.assertIn("### Adicionado\n- feat: suporte a oauth2", release_notes)


if __name__ == "__main__":
    unittest.main()
```

- [ ] **Step 2: Executar o teste unitário para validar que falha antes da implementação**

```bash
python3 -m unittest .github/scripts/test_release.py
```
*Esperado: Falha com `ModuleNotFoundError` ou `ImportError`.*

- [ ] **Step 3: Implementar o script `.github/scripts/release.py`**

Criar `.github/scripts/release.py` implementando a lógica completa:

```python
#!/usr/bin/env python3
"""
Script de automação de release para o projeto gPatri.
Analisa commits convencionais, calcula o bump SemVer, sincroniza pom.xml via Maven
e promove a seção [Não Lançado] do CHANGELOG.md.
"""

from __future__ import annotations
import os
import re
import subprocess
import sys
from dataclasses import dataclass
from datetime import date
from pathlib import Path
from typing import List, Optional, Tuple


@dataclass
class CommitInfo:
    hash: str
    subject: str
    body: str


def run_command(cmd: List[str], check: bool = True) -> str:
    result = subprocess.run(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        check=check,
    )
    return result.stdout.strip()


def get_latest_git_tag() -> Optional[str]:
    try:
        tag = run_command(["git", "describe", "--tags", "--abbrev=0"])
        return tag if tag else None
    except subprocess.CalledProcessError:
        return None


def get_current_pom_version(pom_path: Path) -> str:
    content = pom_path.read_text(encoding="utf-8")
    match = re.search(r"<groupId>br\.edu\.ifg\.numbers</groupId>\s*<artifactId>gPatri</artifactId>\s*<version>([^<]+)</version>", content)
    if match:
        return match.group(1)
    match_fallback = re.search(r"<version>([^<]+)</version>", content)
    if match_fallback:
        return match_fallback.group(1)
    raise ValueError(f"Não foi possível identificar a versão em {pom_path}")


def get_commits_since_tag(tag: Optional[str]) -> List[CommitInfo]:
    git_range = f"{tag}..HEAD" if tag else "HEAD"
    cmd = ["git", "log", git_range, '--pretty=format:%H%x09%s%x09%b%x1e']
    raw_output = run_command(cmd, check=False)
    if not raw_output:
        return []

    commits: List[CommitInfo] = []
    entries = raw_output.split("\x1e")
    for entry in entries:
        clean_entry = entry.strip()
        if not clean_entry:
            continue
        parts = clean_entry.split("\x09")
        commit_hash = parts[0].strip() if len(parts) > 0 else ""
        subject = parts[1].strip() if len(parts) > 1 else ""
        body = parts[2].strip() if len(parts) > 2 else ""
        commits.append(CommitInfo(hash=commit_hash, subject=subject, body=body))
    return commits


def is_ignored_commit(commit: CommitInfo) -> bool:
    subject = commit.subject.lower()
    if subject.startswith("chore(release):") or "[skip ci]" in subject or "[ci skip]" in subject:
        return True
    return False


def determine_bump_type(commits: List[CommitInfo]) -> Optional[str]:
    relevant_commits = [c for c in commits if not is_ignored_commit(c)]
    if not relevant_commits:
        return None

    has_major = False
    has_minor = False
    has_patch = False

    for commit in relevant_commits:
        subject = commit.subject
        body = commit.body

        # Verifica breaking change
        if re.search(r"^[a-zA-Z]+(\([^\)]+\))?!:", subject) or "BREAKING CHANGE:" in body or "BREAKING-CHANGE:" in body:
            has_major = True
            break

        # Verifica feat (MINOR)
        if re.match(r"^feat(\([^\)]+\))?:", subject):
            has_minor = True
            continue

        # Verifica fix, refactor, perf, chore, docs, test, ci, style (PATCH)
        if re.match(r"^(fix|refactor|perf|chore|docs|test|ci|style|build)(\([^\)]+\))?:", subject):
            has_patch = True
            continue

        # Qualquer outro commit com mensagem não convencional também conta como patch
        has_patch = True

    if has_major:
        return "MAJOR"
    if has_minor:
        return "MINOR"
    if has_patch:
        return "PATCH"
    return None


def parse_semver(version_str: str) -> Tuple[int, int, int]:
    clean_ver = version_str.lstrip("v")
    match = re.match(r"^(\d+)\.(\d+)\.(\d+)", clean_ver)
    if not match:
        raise ValueError(f"Versão inválida para SemVer: {version_str}")
    return int(match.group(1)), int(match.group(2)), int(match.group(3))


def format_semver(major: int, minor: int, patch: int) -> str:
    return f"{major}.{minor}.{patch}"


def calculate_next_version(current_ver: str, bump_type: str) -> str:
    major, minor, patch = parse_semver(current_ver)
    if bump_type == "MAJOR":
        return format_semver(major + 1, 0, 0)
    elif bump_type == "MINOR":
        return format_semver(major, minor + 1, 0)
    elif bump_type == "PATCH":
        return format_semver(major, minor, patch + 1)
    raise ValueError(f"Tipo de bump desconhecido: {bump_type}")


def generate_fallback_notes(commits: List[CommitInfo]) -> str:
    added = []
    modified = []
    fixed = []

    for c in commits:
        if is_ignored_commit(c):
            continue
        subj = c.subject
        if subj.startswith("feat"):
            added.append(f"- {subj}")
        elif subj.startswith("fix"):
            fixed.append(f"- {subj}")
        else:
            modified.append(f"- {subj}")

    sections = []
    if added:
        sections.append("### Adicionado\n" + "\n".join(added))
    if modified:
        sections.append("### Modificado\n" + "\n".join(modified))
    if fixed:
        sections.append("### Corrigido\n" + "\n".join(fixed))

    if not sections:
        return "- Atualizações gerais e melhorias no projeto."
    return "\n\n".join(sections)


def promote_changelog(
    changelog_text: str,
    new_version: str,
    commits: List[CommitInfo],
    release_date: Optional[str] = None
) -> Tuple[str, str]:
    if release_date is None:
        release_date = date.today().strftime("%Y-%m-%d")

    # Localiza o bloco [Não Lançado]
    pattern = r"(##\s*\[Não Lançado\]\s*)([\s\S]*?)(?=\n---\s*\n##|\n##\s*\[|\Z)"
    match = re.search(pattern, changelog_text, re.IGNORECASE)

    unreleased_body = ""
    if match:
        unreleased_body = match.group(2).strip()

    # Se o unreleased estiver vazio ou sem itens, usa fallback
    if not unreleased_body or unreleased_body == "---":
        release_notes = generate_fallback_notes(commits)
    else:
        release_notes = unreleased_body

    new_release_header = f"## [{new_version}] - {release_date}"
    new_release_block = f"{new_release_header}\n\n{release_notes}\n"

    # Substitui a seção Não Lançado por um novo Não Lançado vazio + a nova versão
    replacement = f"## [Não Lançado]\n\n---\n\n{new_release_block}"

    if match:
        updated_changelog = changelog_text[:match.start()] + replacement + changelog_text[match.end():]
    else:
        # Se não achou ## [Não Lançado], insere logo após o cabeçalho inicial
        header_end = changelog_text.find("\n---\n")
        if header_end != -1:
            updated_changelog = changelog_text[:header_end] + f"\n---\n\n{replacement}" + changelog_text[header_end + 5:]
        else:
            updated_changelog = f"# Changelog\n\n{replacement}\n\n" + changelog_text

    return updated_changelog, release_notes


def set_github_output(name: str, value: str) -> None:
    output_file = os.environ.get("GITHUB_OUTPUT")
    if output_file:
        with open(output_file, "a", encoding="utf-8") as f:
            f.write(f"{name}={value}\n")
    print(f"[OUTPUT] {name}={value}")


def main() -> int:
    root_dir = Path(__file__).resolve().parent.parent.parent
    pom_path = root_dir / "pom.xml"
    changelog_path = root_dir / "CHANGELOG.md"

    tag = get_latest_git_tag()
    current_version = tag.lstrip("v") if tag else get_current_pom_version(pom_path)
    print(f"[INFO] Última tag: {tag} (Versão base: {current_version})")

    commits = get_commits_since_tag(tag)
    print(f"[INFO] Total de commits desde a última tag: {len(commits)}")

    bump_type = determine_bump_type(commits)
    if not bump_type:
        print("[INFO] Nenhum commit qualificável para release encontrado.")
        set_github_output("has_release", "false")
        return 0

    next_version = calculate_next_version(current_version, bump_type)
    next_tag = f"v{next_version}"
    print(f"[INFO] Bump detectado: {bump_type} -> Próxima versão: {next_version} ({next_tag})")

    # 1. Atualizar pom.xml via Maven Wrapper
    print(f"[INFO] Atualizando POMs Maven para a versão {next_version}...")
    mvnw_cmd = root_dir / ("mvnw.cmd" if os.name == "nt" else "mvnw")
    if not mvnw_cmd.exists():
        mvnw_cmd_str = "mvn"
    else:
        mvnw_cmd_str = str(mvnw_cmd)

    mvn_args = [
        mvnw_cmd_str,
        "versions:set",
        f"-DnewVersion={next_version}",
        "-DgenerateBackupPoms=false",
    ]
    run_command(mvn_args)

    # 2. Atualizar CHANGELOG.md
    print(f"[INFO] Atualizando {changelog_path}...")
    changelog_text = changelog_path.read_text(encoding="utf-8")
    updated_changelog, release_notes = promote_changelog(
        changelog_text=changelog_text,
        new_version=next_version,
        commits=commits
    )
    changelog_path.write_text(updated_changelog, encoding="utf-8")

    # 3. Gerar release_notes.md
    release_notes_path = root_dir / "release_notes.md"
    release_notes_path.write_text(release_notes, encoding="utf-8")
    print(f"[INFO] release_notes.md gerado com sucesso em {release_notes_path}")

    set_github_output("has_release", "true")
    set_github_output("version", next_version)
    set_github_output("tag", next_tag)
    return 0


if __name__ == "__main__":
    sys.exit(main())
```

- [ ] **Step 4: Executar os testes unitários para verificar se passam**

```bash
python3 -m unittest .github/scripts/test_release.py
```
*Esperado: Ran 7 tests in ... OK.*

- [ ] **Step 5: Comitar o script e seus testes**

```bash
git add .github/scripts/release.py .github/scripts/test_release.py
git commit -m "feat(ci): adicionar script python de release e calculo de semver com testes"
```

---

### Task 3: Workflow do GitHub Actions (`release.yml`)

**Files:**
- Create: `.github/workflows/release.yml`

**Interfaces:**
- Consumes: Trigger push na `main`, script `.github/scripts/release.py`.
- Produces: Execução no GitHub Actions, commit `chore(release): vX.Y.Z [skip ci]`, tag Git e GitHub Release.

- [ ] **Step 1: Criar o arquivo `.github/workflows/release.yml`**

```yaml
name: Release & Version Bump

on:
  push:
    branches:
      - main

permissions:
  contents: write

jobs:
  release:
    name: Release & Changelog Automation
    runs-on: ubuntu-latest
    if: "!contains(github.event.head_commit.message, '[skip ci]') && !contains(github.event.head_commit.message, '[ci skip]')"

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 0
          token: ${{ secrets.GITHUB_TOKEN }}

      - name: Set up Java 25
        uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'
          cache: 'maven'

      - name: Set up Python 3
        uses: actions/setup-python@v5
        with:
          python-version: '3.x'

      - name: Make Maven Wrapper executable
        run: chmod +x mvnw

      - name: Run release automation script
        id: release_step
        run: python3 .github/scripts/release.py

      - name: Commit, Tag and Push Release
        if: steps.release_step.outputs.has_release == 'true'
        run: |
          git config user.name "github-actions[bot]"
          git config user.email "github-actions[bot]@users.noreply.github.com"
          git add CHANGELOG.md pom.xml **/pom.xml
          git commit -m "chore(release): v${{ steps.release_step.outputs.version }} [skip ci]"
          git tag -a "v${{ steps.release_step.outputs.version }}" -m "Release v${{ steps.release_step.outputs.version }}"
          git push origin main --tags

      - name: Publish GitHub Release
        if: steps.release_step.outputs.has_release == 'true'
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: |
          gh release create "v${{ steps.release_step.outputs.version }}" \
            --title "Release v${{ steps.release_step.outputs.version }}" \
            --notes-file release_notes.md
```

- [ ] **Step 2: Validar a sintaxe do arquivo YAML**

```bash
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/release.yml'))"
```
*Esperado: Execução sem erros de sintaxe.*

- [ ] **Step 3: Comitar o workflow**

```bash
git add .github/workflows/release.yml
git commit -m "ci: adicionar workflow github actions para release e changelog automatico"
```

---

### Task 4: Validação Integrada e Verificação Final

**Files:**
- Test/Verify: `pom.xml`, `CHANGELOG.md`, `AGENTS.md`, `.agents/rules/changelog-rules.md`, `.github/workflows/release.yml`

- [ ] **Step 1: Executar suite de testes do script de release**

```bash
python3 -m unittest discover -s .github/scripts -p "test_*.py"
```
*Esperado: Todos os testes unitários passando (OK).*

- [ ] **Step 2: Executar testes dos microserviços Maven**

```bash
./mvnw clean test
```
*Esperado: `BUILD SUCCESS` em todos os módulos (`ms-eureka`, `ms-gateway`, `ms-patrimonio`, `ms-usuarios`).*

- [ ] **Step 3: Verificar status do Git e integridade dos arquivos**

```bash
git status
```
*Esperado: Workspace limpo ou com apenas os arquivos adicionados devidamente organizados.*
