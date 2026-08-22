# Changelog

Todas as alterações notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado no [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) e este projeto segue o [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [Não Lançado]

---

## [0.7.0] - 2026-08-22

### Adicionado
- **docker**: adicionar `docker-compose.yml`, `docker/init.sql` e `.env.example` para ambiente de desenvolvimento local com PostgreSQL multi-banco by @thiago-f-santos
- **ci**: enriquecer automaticamente itens do [Não Lançado] com autoria by `@User` by @thiago-f-santos

### Modificado
- **git**: adicionar `.env` e variações locais ao `.gitignore` by @thiago-f-santos

### Corrigido
- **ms-usuarios**: adicionar chamada explícita de `flush()` em `CargoService.deletarCargo` e `UsuarioService.deletarUsuario` para forçar execução imediata de restrições de integridade SQL dentro do bloco `try/catch` da transação (#22) by @thiago-f-santos
- **ms-usuarios**: adicionar manipulador para `DataIntegrityViolationException` no `GlobalExceptionHandler` retornando HTTP 409 Conflict com mensagem amigável (#22) by @thiago-f-santos

### Outros
- Merge pull request #28 from thiago-f-santos/fix/22-flush-integridade-referencial-409 by @thiago-f-santos
- Merge pull request #27 from thiago-f-santos/fix/ci-skip-and-contributor-mentions by @thiago-f-santos
- **ci**: validar mencao crua a usuario sem crases e protecao de anotacoes by @thiago-f-santos
---

## [0.6.1] - 2026-08-22

### Corrigido
- **ci**: descartar commits contendo `[skip ci]`, `[ci skip]` ou `chore(release):` no cálculo de versão do `release.py`, impedindo bumps e releases automáticas indevidas.
- **ci**: restringir resolução de handles de autoria (`get_author_handle`) exclusivamente a emails noreply oficiais do GitHub e mapeamentos de desenvolvedores conhecidos, evitando atribuição de `@` a nomes genéricos ou bots.
---

## [0.6.0] - 2026-08-22

### Adicionado
- **ci**: adicionar sufixo inline by `@User` por commit e remover secao separada de contribuidores by `@Thiago`

### Corrigido
- **ci**: filtrar estritamente contas de bot e github-actions da lista de contribuidores by `@Thiago`
---

## [0.5.0] - 2026-08-22

### Adicionado
- **ci**: adicionar secao de contribuidores com @ nas release notes

### 👥 Contribuidores
- `@Thiago`
---

## [0.4.1] - 2026-08-22

### Corrigido
- **ci**: sanitizar mencoes com @ em notas de release para evitar mencao indevida a usuarios do github
---

## [0.4.0] - 2026-08-22

### Adicionado
- **ci**: adicionar script python de release e calculo de semver com testes
- **ms-usuarios**: atualizar InitialSetupConfig e testes para modelo dinamico de permissoes
- **ms-usuarios**: adicionar PermissaoService, PermissaoController e testes
- **ms-usuarios**: adicionar repositorios, dtos e mappers para Permissao e Cargo
- **ms-usuarios**: adicionar migracao flyway V2 para permissoes e rbac dinamico
- implementando comunicacao entre microservicos com feign para retorno de dados do usuario aninhado nos emprestimos.
- implementando sorting no endpoint de busca de usuarios
- implementando sorting
- alterando logica de alteração de cargos para aceitar mesmo cargo
- ativando serialização de pages
- retornando usuarios de forma paginada
- paginação em retorno de listas implementada
- adicionando permissão para acesso ao painel de administrador
- handling da exceção de quantidade inválida
- adicionando lista de itens patrimonio ao patrimonio, implementação de novos métodos de busca
- implementando busca por usuário
- implementando gateway
- conectando microsserviços como cliente do eureka
- implementando eureka server
- implementando filtro na requisicao findAll
- implementando requisições com filtros para busca de item patrimonio
- adicionando exceções a serem capturadas
- implementando SituacaoEmprestimo
- modificando jwtSecret com finalidade de testes
- handling de IllegalStateException
- adicionando id do usuario avaliador no corpo da resposta
- adicionando mappings necessários
- recuperando id do usuario em requisições através do token jwt
- adicionando id usuario avaliador do emprestimo
- implementando atribuição de cargo, usuario não escolhe seu cargo ao se registrar mais
- implementando atribuição de cargo, usuario não escolhe seu cargo ao se registrar mais.
- adicionando autorizações para os endpoints
- implementando camada de segurança inicial
- adicionando permissões faltantes
- trocando endpoints autorizados
- modificando lenght do sobrenome
- renomeando endpoint
- implementando serviço de autenticação
- criando migration inicial
- trocando banco de dados por postgresql e adicionando migrations
- adicionando nome do modulo e versao
- fazendo handle das exceções
- mensagem de erro padrão
- mapeando de forma completa campos entre domain e dtos
- definindo valor padrão de aprovado como false
- atualizando para utilização do PatrimonioMapper
- adicionando campo aprovado ao dto
- deixando de carregar itens de emprestimo vindos da dto de criação para a domain
- adicionando validacoes
- iniciando lista vazia ao criar emprestimo
- criando endpoints para controle de itens de emprestimo
- implementando service contendo logica de negocio dos itens de emprestimo
- implementação inicial de item emprestimo
- implementação inicial de emprestimo
- criando exceções personalizadas
- enum para string
- adicionando jsonignore na categoria domain
- criando classes para realização do login.
- configurando autenticação com token jwt.
- criando e configurando as permissões e cargos.
- implementando enum para guardar condição do produto
- permitindo atualização de novos campos e removendo funções inutilizadas pelo sistema
- adicionando condicao, descricao e quantidade
- adicionando condicao diretamente ao item de patrimonio, adicionando quantidade ao domain
- permitindo atualização do tipo de controle
- adicionando tipo de controle ao patrimonio
- criando enum para definir tipos de controle de estoque
- criação da migration inicial do ms-patrimonios
- Implementação inicial do microserviço de usuários (#3)
- adicionando anotação @valid nas requisições de DTOs
- criação da controller para ItemPatrimonio, definição dos endpoits
- criação da controller para Patrimonio, definição dos endpoits
- criação da controller para Condição, definição dos endpoits
- criação da controller para Categorias, definição dos endpoits
- implementação das regras de negócio para a domain ItemPatrimonio
- implementação das regras de negócio para a domain Patrimonio
- implementação das regras de negócio para a domain Condição
- implementação das regras de negócio para a domain Categoria
- implementação do mapper de Patrimonio
- implementação do mapper de ItemPatrimonio
- implementação do mapper de Condição
- implementação do mapper de categoria
- criação dos repositories iniciais
- criação das DTOs de ItemPatrimonio
- criação das DTOs de Patrimonio
- criação das DTOs de Condição
- criação das DTOs de Categoria
- criação das entidades de domínio
- arquitetura inicial do microservico
- estrutura inicial

### Modificado
- **ms-usuarios**: aplicar melhorias e correcoes pontuais identificadas no code review
- **ms-usuarios**: refatorar CargoService e CargoController para gerenciamento dinamico de permissoes
- **ms-usuarios**: atualizar entidades Cargo e Usuario para modelo dinâmico com Permissao
- removendo braces desnecessarios em if's statements
- alterando logica da query para melhoria de performance
- padronizando handling de exceções entre os microserviços
- resolvendo problema N+1 e adicionando situação de empréstimo 'atrasada'
- melhorando visualização
- passando id do usuario como parametro das funções
- utilizando novas autoridades
- renomeando autoridades
- substituindo logger por slf4j log
- renomeando ApiExceptionHandler para GlobalExceptionHandler, movendo handlers para pacote exception.handler
- corrigindo retorno de código http errado.
- corrigindo retorno de código http errado.
- removendo passwordEncoder não utilizado e atualizando método depreciado de desabilitação de csrf
- utilizando Usuario ao invés de UserDetails como principal
- removendo linhas desnecessarias fazendo função ser mais curta
- adicionando espaço
- utilizando AuthService
- movendo enum para dentro de domain
- trocando application.properties por application.properties
- movendo funções
- permissoes sao definidas ao criar o cargo.
- mudando ordem de variaveis
- melhorando visualização
- trocando patrimonio de domain puro para dto
- melhorando visualização de função
- ignorando o id explicitamente
- trocando annotation
- usando PatrimonioRepository ao inves da service
- adicionando a verificação de autoridade para executar a requesição.
- mudando para receber o nome da cargo ao criar um novo usuario.
- movendo arquivos do pacote br.edu.ifg.numbers para br.edu.ifg.numbers.gpatri.
- removendo delete em cascada
- movendo arquivos do pacote br.edu.ifg.numbers para br.edu.ifg.numbers.gpatri
- removendo comentario desnecessario
- padronizando posição de anotações

### Corrigido
- verificando data antes de atribuir status ao aprovar emprestimo
- removendo chave em excesso na variavel CORS_ENABLED
- removendo código redundante e adicionando anotação faltante
- consertando merge
- corrigindo nome da aplicação de testes

### Outros
- Merge pull request #25 from thiago-f-santos/ci-cd-release
- adicionar padroes de cache do python ao .gitignore
- adicionar diretrizes de changelog para agentes e template de PR
- adicionar workflow github actions para release e changelog automatico
- adicionar .worktrees e .superpowers ao .gitignore
- adicionar plano de implementacao de ci-cd release e changelog
- adicionar especificacao tecnica de ci-cd release e changelog
- Merge pull request #21 from thiago-f-santos/permissionamento-dinamico-e-atualizacao-java
- atualizar versao da aplicacao para 0.3.0
- adicionar CHANGELOG.md no padrao Keep a Changelog
- atualizar configuracao e testes para compatibilidade com Java 25
- adicionar plano de implementacao do RBAC dinamico
- adicionar design doc do RBAC dinâmico
- novos mvn wrappers
- alterando comando de criação de database
- Merge pull request #20 from thiago-f-santos/feat/paginacao-usuarios
- Merge remote-tracking branch 'origin/main'
- atualizando readme com nova chave
- Merge remote-tracking branch 'origin/main'
- adicionando diagramas uml feitos em levantamento
- Add admin user credentials to README
- atualizando documentação
- Include front-end repository link in README
- Add setup instructions for gPatri backend system
- Merge pull request #19 from thiago-f-santos/thiago/feat/integracao-frontend
- configurando variavel de ambiente para subir aplicação com cors ativado ou desativado sem alteração de código
- criando cargo de usuário comum no primeiro inicio, renomeando antigo arquivo Admin.java
- adicionando variaveis de ambiente para facilitar futuro deploy em prod
- Merge pull request #18 from thiago-f-santos/thiago/feat/integracao-frontend
- remoção de dependencias inutilizadas
- removendo carregamento de allowedOrigins (nao utilizado no momento)
- adicionando permissão cors para o front-end
- dependencia ao spring security adicionada ao gateway
- Merge pull request #17 from thiago-f-santos/thiago/feat/eureka-e-gateway
- remoção de imports não utilizados
- Testes unitários de ms-usuarios e ms-patrimonios (#16)
- Merge pull request #15 from thiago-f-santos/thiago/feat/situacao-emprestimo
- Documentando ms-usuarios com Swagger.
- otimizando imports
- Merge pull request #13 from thiago-f-santos/thiago/refactor/melhor-utilizacao-jwt
- Merge branch 'main' into thiago/refactor/melhor-utilizacao-jwt
- removendo idUsuario
- removendo exceção não lançada
- Merge pull request #12 from thiago-f-santos/thiago/feat/conectando-msusuario-mspatrimonio
- configurando swagger para usar bearer token
- adicionando dependencia para spring boot security e auth0.jwt
- implementando configurações de segurança
- adicionando flyway
- Merge pull request #11 from thiago-f-santos/thiago/feat/implementacao-emprestimos
- Merge pull request #10 from thiago-f-santos/thiago/fix/consertando-merge
- Merge pull request #9 from thiago-f-santos/autenticacao-e-controle-de-acesso
- Merge branch 'main' into autenticacao-e-controle-de-acesso
- removendo anotação de transactional em selects
- refactor
- Merge pull request #8 from thiago-f-santos/thiago/feat/implementacao-emprestimos
- removendo imports não utilizados
- documentando endpoints da ItemEmprestimoController
- documentando endpoints da EmprestimoController
- removendo funções relacionadas a itens de emprestimo
- removendo imports não utilizados
- atualizando banco de dados
- adicionando criação automatica do usuario ADMIN na inicialização do programa.
- removendo condição como dominio separado - implementado diretamente no item
- removendo querys não utilizadas
- trocando componentModel de string por enum
- Merge pull request #6 from thiago-f-santos/thiago/chore/config-dependencias-msusuarios
- configurando dependencias herdadas
- adicionando ms-usuarios como modulo
- Merge pull request #5 from thiago-f-santos/thiago/feat/criacao-do-bd-e-migrations
- Merge pull request #4 from thiago-f-santos/thiago/chore/configurando-dependencias-herdadas
- adicionando dependencia ao flyway-core e flyway-postgresql
- removendo h2, habilitando flyway e configurando conexao com banco de dados psql
- configurando parent como o gPatri e modificando dependencias
- configurando dependencias no pom.xml pai
- Merge pull request #2 from thiago-f-santos/thiago/docs/doc-endpoints-mspatrimonio
- Merge remote-tracking branch 'origin/thiago/docs/doc-endpoints-mspatrimonio' into thiago/docs/doc-endpoints-mspatrimonio
- configurando springdoc para abrir no endpoint '/docs.html'
- configurando springdoc para abrir no endpoint '/docs.html'
- documentação ItemPatrimonioController
- documentação PatrimonioController
- documentação CondicaoController
- removendo import não utilizado
- documentando CategoriaController
- adicionando dependencia para o springdoc-openapi
- Merge pull request #1 from thiago-f-santos/thiago/feat/mvp-gestao-patrimonios
- otimização dos imports removendo imports não utilizados
- configuracao com banco h2 em memoria
---

## [0.3.0] - 2026-08-22

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
