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
