# Changelog

Todas as alterações notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado no [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) e este projeto segue o [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [Não Lançado] - Unreleased

### Adicionado
- **RBAC Dinâmico via Banco de Dados (`ms-usuarios`)**:
  - Nova entidade [`Permissao`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Permissao.java) com campos `id` (UUID), `nome` (único, formato uppercase), `descricao` e `categoria`.
  - Repositório [`PermissaoRepository`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/repository/PermissaoRepository.java) com consultas personalizadas: `findByNome`, `findByCategoria` e `findByIdIn`.
  - Serviço [`PermissaoService`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/PermissaoService.java) para listagem e consulta detalhada de permissões disponíveis.
  - Endpoints REST em [`PermissaoController`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/controller/PermissaoController.java):
    - `GET /api/v1/permissoes` (opcionalmente filtrável por `?categoria=...`) protegido por autoridade `PERMISSAO_LISTAR`.
  - DTOs [`PermissaoResponseDTO`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/dto/PermissaoResponseDTO.java) e mapeamento com [`PermissaoMapper`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/mapper/PermissaoMapper.java).
- **Migração de Banco de Dados Flyway V2**:
  - Script [`V2__criar_tabela_permissoes_e_refatorar_rbac.sql`](ms-usuarios/src/main/resources/db/migration/V2__criar_tabela_permissoes_e_refatorar_rbac.sql) criando tabelas `permissoes` e `cargo_permissoes` (join table), inserindo o catálogo completo de 31 permissões de sistema e migrando dados legados com idempotência.
- **Suporte a Java 25**:
  - Atualização dos arquivos `pom.xml` (raiz e submódulos `ms-patrimonio`, `ms-usuarios`, `ms-eureka`, `ms-gateway`) para `<java.version>25</java.version>`.
  - Atualização de wrappers do Maven (`mvnw`) com permissões executáveis.
  - Configuração isolada de testes com banco H2 em memória para `ms-patrimonio` e `ms-usuarios`.

### Modificado
- **Entidade Cargo e Usuário**:
  - [`Cargo`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Cargo.java) refatorado de `List<PermissaoEnum>` estático para `Set<Permissao>` dinâmico mapeado via `@ManyToMany`.
  - [`Usuario`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Usuario.java) implementando `getAuthorities()` retornando `SimpleGrantedAuthority` dinâmicas obtidas diretamente das permissões associadas ao cargo do usuário.
- **Gestão de Cargos (`CargoService` e `CargoController`)**:
  - `CargoRequestDTO` atualizado para receber `Set<UUID> permissoesIds`.
  - `CargoResponseDTO` atualizado para retornar `Set<PermissaoResponseDTO> permissoes`.
  - Validação estrita de existência de UUIDs de permissões no cadastro e edição de cargos.
- **Inicialização Automática (`InitialSetupConfig`)**:
  - Seed automático de todas as permissões no banco ao iniciar caso ainda não existam.
  - Associação automática de todas as permissões ao cargo padrão `"Administrador"` e atribuição ao usuário inicial `admin`.

### Corrigido
- **Encapsulamento**: Ajustada a visibilidade do repositório em [`UserDetailsServiceImp`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/UserDetailsServiceImp.java) para `private final`.
- **Limpeza de Imports**: Removidas dependências desnecessárias de anotações web em [`AuthService`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/AuthService.java).
- **Injeção de Dependência**: Padronizado o uso de `@RequiredArgsConstructor` no [`PermissaoController`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/controller/PermissaoController.java).
- **Tratamento de Autenticação JWT**: Adicionada verificação defensiva de tipo (`instanceof Usuario`) e tratamento nulo seguro no [`JwtTokenProvider`](ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/security/JwtTokenProvider.java).
- **Higiene de Testes**: Removidos blocos comentados de código morto em [`UsuarioServiceTest`](ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/UsuarioServiceTest.java).

---

## [0.2.0] - 2026-08-19

### Adicionado
- Integração via Spring Cloud OpenFeign entre `ms-patrimonio` e `ms-usuarios` para inclusão dos dados detalhados do usuário requerente nos empréstimos.
- Suporte a paginação e ordenação (`Pageable`, `Sort`) nos endpoints de listagem de usuários e patrimônios.
- Situação de empréstimo `"ATRASADA"` calculada com base na data de devolução prevista.
- Configuração de perfil de ambiente para habilitação dinâmica de CORS.

### Modificado
- Padronização do formato de tratamento global de exceções entre os microserviços com respostas RFC 7807 (`ProblemDetails`).
- Otimização de queries JPA eliminando problemas de consulta N+1 na recuperação de itens e empréstimos.

---

## [0.1.0] - 2026-06-05

### Adicionado
- Estrutura inicial do monorepo de microserviços:
  - `ms-eureka`: Servidor de descoberta de serviços (Netflix Eureka).
  - `ms-gateway`: API Gateway com Spring Cloud Gateway para roteamento unificado.
  - `ms-usuarios`: Microserviço de gestão de usuários, autenticação JWT e controle de acesso inicial.
  - `ms-patrimonio`: Microserviço para cadastro, movimentação e empréstimo de bens patrimoniais.
- Migrações iniciais Flyway V1 e integração com PostgreSQL.
