
# gPatri

O gPatri é um sistema back-end robusto e modular, desenvolvido para o Núcleo de Estudos aplicados a Redes de computadores e Sistemas distribuídos (NumbERS), com o objetivo de centralizar e otimizar o gerenciamento de seus participantes e patrimônios. A aplicação é composta por dois microsserviços principais: ms-usuarios e ms-patrimonio, que se comunicam para fornecer um sistema completo de gestão.

---

## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/thiago-f-santos/gPatri.git
```
Navegue ate o diretorio do projeto

```bash
  cd gPatri
```

### 1. Configure o banco de dados: 

Inicie uma instância do PostgreSQL na porta 5432

E crie os bancos de dados, um banco para ms-usuarios e um para ms-patrimonio

```bash
  CREATE DATABASE ms-usuarios;
```

```bash
  CREATE DATABASE ms-patrimonios;
```
Para evitar a modificação manual dos arquivos application.yml, você pode definir as seguintes variáveis de ambiente. Elas são essenciais para a conexão com o banco de dados e para a comunicação entre os microsserviços.

    | Variável           | Descrição                                            | Valor Padrão                               |
    | ------------------ | ---------------------------------------------------- | ------------------------------------------ |
    | DB_URL             | URL de conexão com o banco de dados.                 | jdbc:postgresql://localhost:5432/gpatri_db |
    | DB_USER            | Nome de usuário do PostgreSQL.                       | postgres                                   |
    | DB_PASSWORD        | Senha do usuário do PostgreSQL.                      | postgres                                   |
    | EUREKA_SERVER      | URL do servidor Eureka.                              | http://localhost:8761/eureka/              |
    | JWT_SECRET         | Chave secreta para a assinatura de tokens JWT.       | String aleatória de 256 caracteres         |
    | JWT_EXPIRATION_MS  | Tempo de expiração do token JWT em milissegundos.    | 86400000                                   |
    | CORS_ALLOWED       | Habilita ou desabilita requisições CORS.             | true                                       |
    | ALLOWED_ORIGINS    | URLs permitidas para requisições CORS.               | http://localhost:8080                      |

Você pode definir as variáveis de ambiente de forma permanente através da interface gráfica do sistema.

---

### 2. Rodar os microsserviços

Para iniciar os microsserviços, você deve rodá-los na seguinte ordem para garantir que a comunicação e a descoberta de serviços funcionem corretamente.

ms-eureka -> ms-usuarios -> ms-patrimonio -> ms-gateway

Acesse o módulo ms-eureka: 
```bash
  cd ms-eureka
```
Inicie a aplicação com o Maven:

```bash
  mvn spring-boot:run
```
Agora inicie os outros microsserviços:

Acesse o ms-usuarios: 
```bash
  cd ms-usuarios
```
Inicie a aplicação com o Maven:

```bash
  mvn spring-boot:run
```

Acesse o ms-patrimonio: 
```bash
  cd ms-patrimonio
```
Inicie a aplicação com o Maven:

```bash
  mvn spring-boot:run
```

Acesse o ms-gateway: 
```bash
  cd ms-gateway
```
Inicie a aplicação com o Maven:

```bash
  mvn spring-boot:run
```
Segue o link do repositorio do front-end da aplicação:
```bash
  https://github.com/thiago-f-santos/gpatri-web-app.git
```

---

### 3. Documentação da API

A API foi completamente documentada utilizando **Swagger**.

Você pode visualizar e testar a documentação interativa da API acessando:

http://localhost:8080/swagger-ui.html

> **Importante:** Os microserviços somente aceitarão requests através do Swagger caso acesso CORS estiver habilitado para o domínio do gateway.

> **Importante:** O usuário administrador padrão é admin@admin.com e a senha é admin123
