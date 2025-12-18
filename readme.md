# API Social MeLi 🚀

## 📋 Descrição do Projeto

**SocialMeli** é uma API REST desenvolvida como parte do Bootcamp do Mercado Livre, que visa criar uma experiência inovadora conectando compradores e vendedores. A plataforma permite que compradores sigam seus vendedores favoritos e acompanhem todas as novidades e promoções publicadas por eles.

## 🎯 Objetivos

Este projeto foi desenvolvido para aplicar os conceitos aprendidos durante o Bootcamp, implementando uma API REST completa com as seguintes funcionalidades principais:

- Sistema de seguidores (follow/unfollow)
- Publicação de produtos e promoções
- Feed de novidades dos vendedores seguidos
- Ordenação e filtragem de resultados

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21** (Eclipse Temurin)
- **Spring Boot 4.0.0**
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **Spring Cloud** (2025.1.0) - Microserviços
- **Resilience4j** - Circuit breaker e resiliência

### Banco de Dados
- **MySQL** - Banco de dados principal
- **H2** - Banco de dados em memória para testes

### Ferramentas
- **Maven** - Gerenciamento de dependências
- **Docker & Docker Compose** - Containerização
- **Lombok** - Redução de código boilerplate
- **Testcontainers** - Testes de integração com containers

### Testes
- **JUnit Jupiter** - Framework de testes
- **Spring Boot Test** - Testes de integração
- **Testcontainers** - Testes com MySQL

## ⚙️ Funcionalidades

### 👥 Gestão de Seguidores

- **US-0001**: Seguir um vendedor - Permite que um usuário siga um vendedor específico
- **US-0007**: Deixar de seguir - Permite que um usuário pare de seguir um vendedor
- **US-0002**: Contar seguidores - Retorna o número total de seguidores de um vendedor
- **US-0003**: Listar seguidores - Obtém a lista de todos os usuários que seguem um vendedor (Quem me segue?)
- **US-0004**: Listar seguindo - Obtém a lista de todos os vendedores que um usuário segue (Quem estou seguindo?)

### 📦 Gestão de Publicações

- **US-0005**: Criar publicação - Registra uma nova publicação de produto
- **US-0006**: Feed de novidades - Lista publicações das últimas duas semanas dos vendedores seguidos
- **Publicação promocional**: Registra um produto em promoção exclusiva para seguidores
- **Contar promoções**: Retorna a quantidade de produtos em promoção de um vendedor

### 🔍 Ordenação e Filtragem

- **US-0008**: Classificação alfabética - Ascendente e Descendente
- **US-0009**: Classificação por data - Crescente e Decrescente

## 📊 Estrutura de Dados

### Parâmetros e Tipos

| Campo | Tipo | Tamanho | Descrição |
|-------|------|---------|-----------|
| user_id | Integer | - | Identificador do usuário |
| user_id_to_follow | Integer | - | Identificador do usuário a ser seguido |
| user_name | String | 15 | Nome do usuário |
| followers_count | Integer | - | Número de seguidores |
| id_post | Integer | - | Identificador da publicação |
| date | LocalDate | - | Data de publicação (formato: dd-MM-yyyy) |
| product_id | Integer | - | Identificador do produto |
| product_name | String | 40 | Nome do produto |
| type | String | 15 | Tipo do produto |
| brand | String | 25 | Marca do produto |
| color | String | 15 | Cor do produto |
| notes | String | 80 | Observações sobre o produto |
| category | Integer | - | Categoria do produto |
| price | Double | - | Preço do produto |
| has_promo | Boolean | - | Indica se está em promoção |
| discount | Double | - | Percentual de desconto |

### Categorias de Produtos

- **100**: Cadeiras
- **58**: Teclados
- *(Adicione outras categorias conforme necessário)*

## 🐳 Docker & Containers

### Estrutura do Projeto

O projeto utiliza uma arquitetura multi-stage Docker com:
- **Build Stage**: Maven 3.9.7 com Eclipse Temurin para compilação
- **Runtime Stage**: Eclipse Temurin 21 JRE Alpine (imagem otimizada)

### Serviços

- **MySQL**: Banco de dados na porta 3306
- **API Runtime**: Aplicação Spring Boot na porta 5173 (mapeada para 8080 interno)

## 🚀 Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 21 (se executar localmente)
- Maven 3.9+ (se executar localmente)

### Executar com Docker Compose (Recomendado)

```bash
# Clone o repositório
git clone [url-do-repositorio]

# Entre no diretório do projeto
cd api-social-meli

# Inicie os containers
docker-compose up -d

# Acompanhe os logs
docker-compose logs -f runtime
```

A aplicação estará disponível em: `http://localhost:5173`

### Executar Localmente

```bash
# Clone o repositório
git clone git@github.com:Jvieyrah/API-Social-MELI.git

# Entre no diretório do projeto
cd api-social-meli

# Compile o projeto
mvn clean install

# Execute a aplicação
mvn spring-boot:run
```

### Parar os Containers

```bash
# Parar os containers
docker-compose down

# Parar e remover volumes (limpar banco de dados)
docker-compose down -v
```

## 🧪 Testes

### Executar Testes

```bash
# Executar todos os testes
mvn test

# Executar testes com relatório de cobertura
mvn clean test jacoco:report

# Executar apenas testes unitários
mvn test -Dtest=*UnitTest

# Executar apenas testes de integração
mvn test -Dtest=*IntegrationTest
```

### Estratégia de Testes

#### Testes Unitários
- Validação de todas as funcionalidades individuais
- Garantia do funcionamento correto de cada método
- Mock de dependências externas

#### Testes de Integração
- Pelo menos um teste de integração por User Story
- Uso de Testcontainers para MySQL
- Testes end-to-end dos endpoints
- **Meta de cobertura**: ≥ 80%

## 📚 Documentação da API

A API está completamente documentada utilizando **Swagger/OpenAPI**.

### Acessar a Documentação

Após iniciar a aplicação, acesse:

```
http://localhost:5173/swagger-ui.html
```

A documentação permite:
- Visualização de todos os endpoints disponíveis
- Teste interativo das funcionalidades
- Descrição detalhada de parâmetros e respostas
- Exemplos de requisições e respostas

## 🔒 Segurança

O projeto implementa Spring Security para:
- Autenticação de usuários
- Autorização de endpoints
- Proteção contra ataques comuns (CSRF, XSS)
- Gestão de sessões

## 🏗️ Arquitetura

### Padrões Utilizados

- **REST API** - Arquitetura RESTful
- **Layered Architecture** - Separação em camadas (Controller, Service, Repository)
- **DTO Pattern** - Data Transfer Objects
- **Circuit Breaker** - Resilience4j para resiliência
- **Repository Pattern** - Abstração de acesso a dados

### Estrutura de Pastas

```
src/
├── main/
│   ├── java/
│   │   └── com/meli/social/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       ├── dto/
│   │       ├── exception/
│   │       └── config/
│   └── resources/
│       ├── application.properties
│       └── db/migration/
└── test/
    ├── java/
    │   └── com/meli/social/
    │       ├── unit/
    │       └── integration/
    └── resources/
```

## 🌐 Variáveis de Ambiente

### Docker Compose

```yaml
SPRING_DATA_MYSQL_URI: jdbc:mysql://myuser:secret@mysql:3306/meli_social
MYSQL_DATABASE: meli_social
MYSQL_USER: myuser
MYSQL_PASSWORD: secret
MYSQL_ROOT_PASSWORD: verysecret
```

### Configuração Local

Crie um arquivo `application-local.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/meli_social
spring.datasource.username=myuser
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=update
```

## 📈 Monitoramento e Resiliência

- **Circuit Breaker**: Resilience4j configurado para proteção de falhas
- **Health Checks**: Endpoints de saúde da aplicação
- **Restart Policy**: `unless-stopped` para alta disponibilidade

## 🎁 Funcionalidades Bônus (Opcionais)

- ✅ Testes de integração com Testcontainers
- ✅ Docker multi-stage build otimizado
- ✅ Circuit breaker para resiliência
- ✅ Spring Security para autenticação
- 🎯 Cobertura de testes ≥ 80%
- 🎯 Métricas e observabilidade

## 📝 Requisitos Técnicos

- ✅ Endpoints seguem padrões REST
- ✅ Validação de dados de entrada
- ✅ Tratamento adequado de erros
- ✅ Código limpo e bem documentado
- ✅ Padrões de qualidade MercadoLibre
- ✅ Documentação Swagger
- ✅ Testes unitários e de integração

## 🛠️ Build e Deploy

### Build da Imagem Docker

```bash
# Build manual
docker build -t api-social-meli:latest .

# Build com docker-compose
docker-compose build
```

### Gerenciamento de Containers

```bash
# Ver logs
docker-compose logs -f runtime

# Reiniciar aplicação
docker-compose restart runtime

# Verificar status
docker-compose ps

# Acessar container
docker-compose exec runtime sh
```

## 🤝 Contribuindo

### Convenções de Código

- Utilizar Lombok para reduzir boilerplate
- Seguir princípios SOLID
- Manter cobertura de testes acima de 80%
- Documentar endpoints no Swagger
- Commitar com mensagens descritivas

### Fluxo de Desenvolvimento

1. Criar branch feature/US-XXXX
2. Desenvolver funcionalidade
3. Escrever testes unitários e de integração
4. Verificar cobertura de código
5. Criar Pull Request
6. Code Review
7. Merge para main

## 📄 Licença

Este projeto foi desenvolvido como parte do Bootcamp MercadoLibre.

## 👥 Autor

**João Filho** - Desenvolvedor Bootcamp MeLi

---

**Desenvolvido com ❤️ durante o Bootcamp MeLi**

### 📞 Suporte

Para dúvidas ou problemas:
1. Verifique a documentação Swagger
2. Consulte os logs: `docker-compose logs -f`
3. Abra uma issue no repositório

### 🔄 Status do Projeto

🚧 Em desenvolvimento - Bootcamp MeLi Sprint