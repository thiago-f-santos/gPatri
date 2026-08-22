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
