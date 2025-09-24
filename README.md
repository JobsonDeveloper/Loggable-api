# 📌 Loggable

API para autenticação de usuários, fornecendo a opção de criação de dois tipos de usuários (ADMIN e BASIC), onde o ADMIN em um sistema completo teria um nível de acesso maior e o restante dos usuários teriam um nível de acesso limitado.

Back-End para o projeto [Loggable-client](https://github.com/JobsonDeveloper/Loggable-client)

- Ao registrar um usuário, ele é cadastrado no banco, juntamente com sua senha encriptada.
  
- Ao solicitar o Login, a senha guardada no banco é descriptada e comparada com a senha enviada pelo cliente.
  
- Em caso de Login bem sucedido, é enviado para o cliente o seu ID juntamente com um token, para ser armazenado no navegador e possibilitar a realização de requisições específicas para usuários autenticados.

Esta API de autenticação e gestão de usuários utiliza Java 21 e Spring Boot, com segurança baseada em JWT e OAuth2, documentação via Swagger, persistência em PostgreSQL, conteinerização com Docker e testes unitários com JUnit.

# 🛠️ Tecnologias Utilizadas
- ☕ Java 21
- 🌱 Spring Boot
  - Spring Security
  - OAuth2
  - JWT
  - Spring Data JPA
  - Spring Web
- 🐘 PostgreSQL
- 🐳 Docker
- 📖 Swagger / OpenAPI
- 🧪 JUnit

# 🚀 Como Executar o Projeto
### ✅ Pré-requisitos
- Java 21+
- Maven 3.9+
- Docker

### ▶️ Rodando a aplicação localmente
#### Clone o repositório
```
git clone https://github.com/JobsonDeveloper/Loggable-api.git
```
```
cd Loggable-api
```

#### Suba o banco com o Docker
```
docker-compose up -d
```

Inicialize a aplicação através do editor de códio de sua preferência (Recomendo o IntelliJ :wink:)

# 🔑 Autenticação e Segurança
#### Rotas protegidas via Spring Security.
- POST → "/auth/login" → retorna token JWT

# 📖 Documentação da API
#### Após rodar a aplicação, acesse:
👉 http://localhost:8081/swagger-ui.html



