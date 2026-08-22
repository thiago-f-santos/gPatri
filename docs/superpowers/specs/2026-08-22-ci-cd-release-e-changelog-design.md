# Especificação Técnica: Automação de Release, Versionamento Semântico e Manutenção de Changelog

- **Data**: 2026-08-22
- **Status**: Aprovado
- **Autor**: Thiago Ferreira dos Santos
- **Alvo**: Monorepo de Microserviços `gPatri` (Java 25, Maven Multi-module)

---

## 1. Visão Geral e Objetivos

O objetivo deste projeto é estabelecer um fluxo automatizado e robusto de entrega contínua (CI/CD) para o projeto **gPatri**, garantindo:
1. **Versionamento Semântico Automatizado (SemVer)**: Cálculo determinístico de bumps de versão (`MAJOR`, `MINOR`, `PATCH`) a partir da análise de mensagens de commit baseadas na convenção **Conventional Commits 1.0.0**.
2. **Sincronização Atômica de Versões no Maven**: Atualização coordenada de versão no `pom.xml` raiz e em todos os microserviços filhos (`ms-patrimonio`, `ms-usuarios`, `ms-eureka`, `ms-gateway`).
3. **Rastreabilidade Total no Changelog**: Promoção automática da seção `## [Não Lançado]` do [`CHANGELOG.md`](file:///home/thiago/Projetos/gPatri/CHANGELOG.md) para a nova versão (`## [X.Y.Z] - AAAA-MM-DD`), preservando a riqueza das notas técnicas escritas por desenvolvedores e agentes de IA no padrão [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) em português.
4. **Publicação Automatizada de Releases**: Criação do commit de release `chore(release): vX.Y.Z [skip ci]`, tag Git anotada e publicação da GitHub Release com notas extraídas do changelog.
5. **Diretrizes Generalistas para Agentes de IA e Desenvolvedores**: Estabelecimento de regras claras em [`AGENTS.md`](file:///home/thiago/Projetos/gPatri/AGENTS.md) e [`.agents/rules/changelog-rules.md`](file:///home/thiago/Projetos/gPatri/.agents/rules/changelog-rules.md) para garantir que qualquer agente de IA ou membro do time sempre atualize o `CHANGELOG.md` e utilize commits convencionais.

---

## 2. Arquitetura do Sistema

```mermaid
flowchart TD
    A[Desenvolvedor / Agente de IA em Feature Branch] -->|1. Implementa alteração| B[Edita código]
    A -->|2. Atualiza seção Não Lançado| C[CHANGELOG.md]
    A -->|3. Commita no padrão convencional| D[Conventional Commits]
    D --> E[Pull Request & Merge na main]
    E --> F[GitHub Actions: release.yml]
    F -->|Fetch histórico completo| G[Script release.py]
    G -->|Analisa commits desde última tag| H{Há commits com impacto de bump?}
    H -->|Não| I[Finaliza sem release]
    H -->|Sim| J[Calcula nova versão SemVer]
    J --> K[Executa ./mvnw versions:set]
    K --> L[Atualiza pom.xml raiz e submódulos]
    J --> M[Promove [Não Lançado] para [vX.Y.Z] no CHANGELOG.md]
    M --> N[Cria commit chore release vX.Y.Z [skip ci]]
    N --> O[Cria Tag Git vX.Y.Z & Push main]
    O --> P[Cria GitHub Release via gh CLI com release_notes.md]
```

---

## 3. Especificação dos Componentes

### 3.1. Convenção de Commits e Mapeamento SemVer

O sistema suportará os seguintes tipos de Conventional Commits:

| Tipo | Descrição | Impacto no SemVer | Mapeamento no CHANGELOG |
| :--- | :--- | :--- | :--- |
| `feat!:` / `fix!:` ou `BREAKING CHANGE:` | Modificação que quebra compatibilidade anterior | **MAJOR** (`X.0.0`) | Destaque de Quebra / `### Modificado` ou `### Removido` |
| `feat:` / `feat(escopo):` | Nova funcionalidade retrocompatível | **MINOR** (`0.X.0`) | `### Adicionado` |
| `fix:` / `fix(escopo):` | Correção de bug | **PATCH** (`0.0.X`) | `### Corrigido` |
| `refactor:` / `refactor(escopo):` | Refatoração de código sem alterar regra externa | **PATCH** (`0.0.X`) | `### Modificado` |
| `perf:` / `perf(escopo):` | Melhoria de desempenho | **PATCH** (`0.0.X`) | `### Modificado` |
| `chore:` / `chore(escopo):` | Tarefas de build, dependências ou manutenção | **PATCH** (`0.0.X`) | `### Modificado` |
| `docs:` / `docs(escopo):` | Documentação | **PATCH** (`0.0.X`) | `### Modificado` |
| `test:` / `test(escopo):` | Adição ou alteração de testes | **PATCH** (`0.0.X`) | `### Modificado` |
| `ci:` / `ci(escopo):` | Configurações de integração contínua | **PATCH** (`0.0.X`) | `### Modificado` |

> **Nota de Filtragem**:
> - Commits cujo título inicie com `chore(release):` ou contenham `[skip ci]` ou `[ci skip]` serão desconsiderados no cálculo para evitar recursão.

---

### 3.2. Script de Automação de Release (`.github/scripts/release.py`)

O script será escrito em Python 3 puro (sem dependências externas) para máxima portabilidade no runner do GitHub Actions e em ambientes de desenvolvimento locais.

#### Responsabilidades:
1. **Identificação da Tag Base**:
   - Obtém a tag mais recente via `git describe --tags --abbrev=0` (ex: `v0.3.0`).
   - Caso não exista nenhuma tag, lê a versão base do `pom.xml` raiz.
2. **Coleta e Classificação de Commits**:
   - Executa `git log <ultima_tag>..HEAD --pretty=format:"%H%x09%s%x09%b%x1e"` para obter hash, assunto e corpo de todos os commits desde a tag.
   - Aplica expressões regulares para identificar breaking changes, feats, fixes e demais tipos.
   - Determina se o bump é `MAJOR`, `MINOR` ou `PATCH`.
3. **Cálculo da Próxima Versão**:
   - Converte `major.minor.patch` atual e aplica o incremento.
4. **Atualização dos Arquivos Maven (`pom.xml`)**:
   - Invoca `./mvnw versions:set -DnewVersion=<NOVA_VERSAO> -DgenerateBackupPoms=false`.
   - Valida que o comando Maven foi executado com sucesso e os 5 `pom.xml` foram sincronizados.
5. **Atualização do `CHANGELOG.md`**:
   - Localiza a seção `## [Não Lançado]`.
   - Se houver conteúdo registrado em `## [Não Lançado]`, extrai o texto.
   - Se a seção estiver vazia, gera automaticamente uma lista sintética agrupada por tipo a partir dos commits do range.
   - Converte a seção para `## [<NOVA_VERSAO>] - AAAA-MM-DD`.
   - Insere um novo cabeçalho `## [Não Lançado]` vazio no topo.
   - Atualiza links de comparação no rodapé do `CHANGELOG.md` se existirem.
   - Salva o fragmento desta versão em `release_notes.md` para uso na GitHub Release.
6. **Outputs para o GitHub Actions**:
   - Exporta variáveis para o `GITHUB_OUTPUT`: `has_release=true/false`, `version=X.Y.Z`, `tag=vX.Y.Z`.

---

### 3.3. Workflow do GitHub Actions (`.github/workflows/release.yml`)

#### Configuração:
- **Disparo**: `push` na branch `main`.
- **Condição**: `!contains(github.event.head_commit.message, '[skip ci]') && !contains(github.event.head_commit.message, '[ci skip]')`.
- **Permissões**:
  ```yaml
  permissions:
    contents: write
  ```

#### Etapas do Job:
1. `actions/checkout@v4` com `fetch-depth: 0`.
2. `actions/setup-java@v4` com `java-version: '25'`, `distribution: 'temurin'`, `cache: 'maven'`.
3. `actions/setup-python@v5` com `python-version: '3.x'`.
4. Execução do script: `python3 .github/scripts/release.py`.
5. Se `has_release == 'true'`:
   - Configuração do usuário Git:
     ```bash
     git config user.name "github-actions[bot]"
     git config user.email "github-actions[bot]@users.noreply.github.com"
     ```
   - Commit e Push:
     ```bash
     git add CHANGELOG.md pom.xml **/pom.xml
     git commit -m "chore(release): v${{ steps.release.outputs.version }} [skip ci]"
     git tag -a "v${{ steps.release.outputs.version }}" -m "Release v${{ steps.release.outputs.version }}"
     git push origin main --tags
     ```
   - Publicação da GitHub Release:
     ```bash
     gh release create "v${{ steps.release.outputs.version }}" \
       --title "Release v${{ steps.release.outputs.version }}" \
       --notes-file release_notes.md
     ```
     *(com `env: GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}`)*

---

### 3.4. Instruções Generalistas para Agentes e Contribuidores (`AGENTS.md`)

Será criado o arquivo [`AGENTS.md`](file:///home/thiago/Projetos/gPatri/AGENTS.md) na raiz do repositório, com instruções neutras e universais para qualquer agente de IA ou desenvolvedor:

1. **Protocolo Obrigatório de Changelog**:
   - Toda e qualquer tarefa que envolva adição de features, correção de bugs, refatorações ou mudanças em dependências **DEVE** incluir alterações na seção `## [Não Lançado]` do arquivo [`CHANGELOG.md`](file:///home/thiago/Projetos/gPatri/CHANGELOG.md).
   - As anotações devem seguir as categorias padrão em português: `### Adicionado`, `### Modificado`, `### Corrigido`, `### Removido`, `### Segurança`.
   - Devem conter referências explícitas aos módulos, classes, endpoints REST e scripts Flyway impactados.
2. **Padrão de Mensagens de Commit**:
   - Uso obrigatório de Conventional Commits (`feat(modulo):`, `fix(modulo):`, `refactor(modulo):`, etc.).
3. **Regra de Build e Testes**:
   - Todo commit deve manter a compilação limpa e os testes passando via `./mvnw test`.

Será mantido também o arquivo [`.agents/rules/changelog-rules.md`](file:///home/thiago/Projetos/gPatri/.agents/rules/changelog-rules.md) com conteúdo modular e generalista para ambientes compatíveis com regras na pasta `.agents/`.

---

### 3.5. Template de Pull Request (`.github/PULL_REQUEST_TEMPLATE.md`)

Arquivo estruturado para guiar a revisão humana e garantir que nenhum PR seja aceito sem o devido preenchimento do `CHANGELOG.md` e aderência aos commits convencionais:

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

---

## 4. Tratamento de Erros e Casos de Borda

1. **Push sem commits qualificáveis para bump**: Se um push contiver apenas commits sem alteração de versão (ou apenas `chore(release)` ignorados), o script detecta `has_release=false` e o workflow conclui sem falha e sem criar tags duplicadas.
2. **Seção `[Não Lançado]` vazia no momento do merge**: O script `release.py` implementa fallback automático gerando itens no changelog baseados nas mensagens dos commits do período.
3. **Concorrência / Loops de CI**: O commit de release inclui explicitamente `[skip ci]`, garantindo que o GitHub Actions não dispare novamente em cascata.
4. **Inconsistência de Submódulos Maven**: O uso do plugin `versions-maven-plugin` via `./mvnw versions:set` atualiza atomicamente o `pom.xml` pai e as tags `<parent><version>` e `<version>` de todos os 4 microserviços.

---

## 5. Estratégia de Testes e Validação

1. **Testes Locais do Script Python (`release.py`)**:
   - Execução em modo dry-run / teste com tags simuladas para validar o cálculo SemVer e a manipulação do `CHANGELOG.md`.
2. **Teste de Sincronização Maven**:
   - Execução do comando `./mvnw versions:set` e verificação da integridade de todos os `pom.xml`.
3. **Validação de Sintaxe dos Workflows**:
   - Validação do arquivo YAML `.github/workflows/release.yml`.
4. **Verificação de Regras e Documentação**:
   - Garantir que `AGENTS.md`, `.agents/rules/changelog-rules.md` e `.github/PULL_REQUEST_TEMPLATE.md` estão consistentes.
