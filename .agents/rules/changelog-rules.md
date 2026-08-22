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
