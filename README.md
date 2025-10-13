# 📌 Loggable

API para autenticação de usuários, fornecendo os recursos de cadastro, login, logout e exclusão de perfil.
Validando o usuário via Token por meio da utilização do JWT + OAuth2, garantindo que as operações sejam realizadas somente por quem tem permissão para tal.

Back-End para o projeto [Loggable-client](https://github.com/JobsonDeveloper/Loggable-client)

- Ao registrar um usuário, ele é cadastrado no banco, juntamente com sua senha encriptada.
  
- Ao solicitar o Login, a senha guardada no banco é descriptada e comparada com a senha enviada pelo cliente.
  
- Em caso de Login bem sucedido, é enviado para o cliente o seu ID juntamente com um token, para ser armazenado no navegador e possibilitar a realização de requisições específicas para usuários autenticados.

Esta API de autenticação e gestão de usuários utiliza Java 21 e Spring Boot, com segurança baseada em JWT e OAuth2, documentação via Swagger, persistência em PostgreSQL, conteinerização com Docker, testes unitários e de integração com JUnit e testes de cobertura com JaCoCo.

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
- 🧪 Testes
  - JUnit (Unitários e Integração)
  - Testcontainers (Banco real para testes E2E)
  - Spring Boot Test (E2E)
- 🔍 JaCoCo

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

## Visualização

<img width="921" height="808" alt="image" src="https://github.com/user-attachments/assets/088431d8-0635-47d2-b10a-33cd8b8a88ba" />

# Realizando o teste de cobertura
#### No terminal da IDE, execute o comando para gerar a documentação html:
```
mvn clean test
```
#### Depois, execute o comando para abrir a documentação no navegador:
```
start target\site\jacoco\index.html
```

## Visualização

<img width="1919" height="433" alt="image" src="https://github.com/user-attachments/assets/d3842b0d-b7d3-4cd9-9421-ec6f8370c4ed" />

# Testes unitários e de integração

<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/47ee1071-09ca-474f-8072-ebae868c8022" />

# Teste E2E

![2025-10-13 15-35-55](https://github.com/user-attachments/assets/a460d4c9-e6c1-4070-842a-d786eb4399ea)
