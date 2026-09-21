# Todo Java Spring Boot Playground

REST API for managing tasks, built with Java and Spring Boot. This project is a learning playground for practicing backend development, persistence, database migrations, JPA relationships, and layered application design.

## Project goals

This project explores:

- REST API development with Spring Boot
- Separation between controllers, services, repositories, and entities
- Persistence with Spring Data JPA and Hibernate
- PostgreSQL database integration
- Versioned database migrations with Flyway
- DTOs for API input and output
- Entity relationships
- Docker-based local development
- Spring Boot context testing

## Technologies

| Technology | Purpose |
|---|---|
| Java 25 | Main programming language |
| Spring Boot 4.1.1 | Application framework |
| Spring Web MVC | REST endpoint development |
| Spring Data JPA | Persistence abstraction |
| Hibernate | ORM implementation |
| PostgreSQL 16 | Relational database |
| Flyway | Database schema versioning |
| Maven | Dependency and build management |
| Docker Compose | Local PostgreSQL environment |
| Lombok | Boilerplate reduction in entities |
| JUnit | Automated testing |

## Architecture

The application follows a simple layered architecture:

```text
controller
    ↓
service
    ↓
repository
    ↓
database
```

### Controllers

Controllers receive HTTP requests, validate input data, and delegate execution to services.

Examples:

- `TodoController`
- `FolderController`
- `CategoryController`

This follows the single-responsibility principle: controllers should not contain business rules or access the database directly.

### Services

Services contain business rules and coordinate operations involving DTOs, entities, and repositories.

Examples:

- `TodoService`
- `FolderService`
- `CategoryService`

This layer prevents rules such as relationship validation and duplicate checking from being scattered across controllers.

### Repositories

Repositories use Spring Data JPA to access the database without manually writing basic queries.

Examples:

- `TodoRepo`
- `FolderRepo`
- `CategoryRepo`

Repositories reduce repetitive code and provide abstractions such as `findById`, `findAll`, `save`, and name-based queries.

### Entities

Entities represent database tables:

- `TodoEntity` → `todos`
- `FolderEntity` → `folders`
- `CategoryEntity` → `categories`

The entities use JPA annotations such as:

- `@Entity`
- `@Table`
- `@Id`
- `@ManyToOne`
- `@OneToMany`
- `@JoinColumn`

## Data model

The application has three main entities:

```text
Folder 1 ──────── N Todo N ──────── 1 Category
```

### Todo

Represents a task.

Main fields:

- `id`
- `title`
- `description`
- `folder`
- `category`

### Folder

Groups tasks by context.

Examples:

- Personal
- Work
- Learning

A folder can contain multiple todos.

### Category

Classifies tasks by type.

Examples:

- Planning
- Development
- Reading
- Finance

A category can be used by multiple todos.

### Integrity rules

The database enforces the following rules:

- `id` is the primary key of every table
- `folders.name` is required and unique
- `categories.name` is required and unique
- `todos.title` is required
- Every todo must belong to a folder
- A todo category may be null
- Deleting a folder also deletes its related todos
- Deleting a category leaves related todos without a category

These rules are enforced by the database to protect data consistency even if another application accesses PostgreSQL directly.

## DTOs

The API does not expose JPA entities directly. Instead, it uses DTOs:

- `TodoRequestDTO`
- `TodoResponseDTO`
- `FolderRequestDTO`
- `FolderResponseDTO`
- `CategoryRequestDTO`
- `CategoryResponseDTO`

This helps to:

- Avoid coupling the API to the persistence model
- Control exactly which fields enter and leave the API
- Avoid exposing internal entity details
- Make future database or API contract changes easier

## Flyway migrations

The database schema is defined in:

```text
src/main/resources/db/migration/V1__init.sql
```

Flyway runs migrations automatically when the application starts.

The naming convention is:

```text
V<version>__<description>.sql
```

Examples:

```text
V1__init.sql
V2__add_due_date.sql
V3__add_completed_status.sql
```

Versioned migrations allow the schema to evolve predictably across environments.

### Future migration improvements

- Create a new migration for every schema change
- Avoid editing migrations that have already run in shared environments
- Add indexes for frequent queries
- Add due dates, status, and completion timestamps
- Name database constraints explicitly
- Automate migration validation in CI

## Initial data

The following file contains data for manual testing:

```text
src/main/resources/db/data.sql
```

It creates initial folders, categories, and todos. The inserts use `ON CONFLICT DO NOTHING`, allowing the script to run repeatedly without duplicating records with the same IDs.

Initialization is configured in:

```properties
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:db/data.sql
```

## Prerequisites

Install:

- Java 25
- Docker and Docker Compose
- Maven, if you do not use the Maven Wrapper

Check the installed versions:

```bash
java -version
docker --version
docker compose version
```

## PostgreSQL configuration

The project includes a `compose.yaml` file with PostgreSQL 16:

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: mydatabase
      POSTGRES_PASSWORD: secret
      POSTGRES_USER: myuser
    ports:
      - "5432:5432"
```

Start the database:

```bash
docker compose up -d
```

Check the container:

```bash
docker compose ps
```

Stop the database:

```bash
docker compose down
```

To also remove the local database data:

```bash
docker compose down -v
```

> The `docker compose down -v` command removes the database volume. Use it only when you want to recreate the local environment from scratch.

## Running the application

With PostgreSQL running, execute:

```bash
./mvnw spring-boot:run
```

If Maven is installed globally:

```bash
mvn spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

If port 8080 is already in use, configure another port in `application.properties`:

```properties
server.port=8081
```

## Running tests

Run the tests with:

```bash
./mvnw test
```

Or:

```bash
mvn test
```

The current test verifies that the application context can start:

```java
@SpringBootTest
class TodoApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

## Building the application

Create the application package:

```bash
./mvnw package
```

Create the package without running tests:

```bash
./mvnw package -DskipTests
```

The artifact is created at:

```text
target/todo-0.0.1-SNAPSHOT.jar
```

Run the JAR with:

```bash
java -jar target/todo-0.0.1-SNAPSHOT.jar
```

## Available endpoints

### Todos

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/todos` | List all todos |
| `GET` | `/todos/{id}` | Find a todo by ID |
| `GET` | `/todos/search/{title}` | Find a todo by title |
| `POST` | `/todos` | Create a todo |
| `PUT` | `/todos/{id}` | Update a todo |
| `DELETE` | `/todos/{id}` | Delete a todo |

Example:

```http
POST /todos
Content-Type: application/json
```

```json
{
  "title": "Implement todo API",
  "description": "Create endpoints for listing, creating, and updating todos.",
  "folderName": "Work",
  "categoryName": "Development"
}
```

### Folders

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/folders` | List all folders |
| `GET` | `/folders/{id}` | Find a folder by ID |
| `POST` | `/folders` | Create a folder |
| `PATCH` | `/folders/{id}` | Update a folder |
| `DELETE` | `/folders/{id}` | Delete a folder |

Example:

```http
POST /folders
Content-Type: application/json
```

```json
{
  "name": "Studies"
}
```

### Categories

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/categories` | List all categories |
| `GET` | `/categories/{id}` | Find a category by ID |
| `POST` | `/categories` | Create a category |
| `PATCH` | `/categories/{id}` | Update a category |
| `DELETE` | `/categories/{id}` | Delete a category |

Example:

```http
POST /categories
Content-Type: application/json
```

```json
{
  "name": "Urgent"
}
```

## Creating a todo

The folder and category provided when creating a todo must already exist.

Typical flow:

```text
1. Create or find a folder
2. Create or find a category
3. Send their names in POST /todos
4. The service resolves the relationships in the database
5. The todo is persisted with the corresponding foreign keys
```

This approach keeps relationship resolution in the service layer and prevents clients from needing to send internal entity IDs.

## Applied principles

### Separation of responsibilities

Each layer has a specific role:

- Controller: HTTP concerns
- Service: business rules
- Repository: persistence
- Entity: persisted model
- DTO: API contract

### Encapsulation

Entities represent the internal model, while DTOs control the data exposed externally.

### Referential integrity

Foreign keys ensure that relationships between `todos`, `folders`, and `categories` remain valid.

### Immutable response DTOs

Response DTOs are defined as `record`, which is appropriate for simple data-transfer objects.

### Incremental evolution

Flyway allows database changes to be added through new migrations while preserving the schema history.

### Dependency injection

Services receive their repositories through constructors. This makes dependencies explicit and facilitates unit testing.

## Completed improvements

### Request DTO validation — Issue #1

Request DTOs now validate required and bounded fields with Bean Validation:

- `TodoRequestDTO`
  - `title`: required, up to 255 characters
  - `description`: up to 2,000 characters
  - `categoryName`: required, up to 255 characters
  - `folderName`: required, up to 255 characters
- `FolderRequestDTO`
  - `name`: required, up to 255 characters
- `CategoryRequestDTO`
  - `name`: required, up to 255 characters

Controllers use `@Valid` and `@RequestBody`, so invalid request payloads are rejected before reaching the service layer. This completes GitHub issue #1.

### Global exception handling — Issue #2

The API now uses typed application exceptions and a `@RestControllerAdvice` to return consistent error responses:

- `404 Not Found` for missing resources
- `409 Conflict` for duplicate folders or categories
- `422 Unprocessable Entity` for invalid relationship operations
- `400 Bad Request` for invalid request bodies and validation failures
- `500 Internal Server Error` for unexpected failures without exposing stack traces

Error responses use the following structure:

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Category not found",
  "path": "/categories/..."
}
```

Validation failures also include a `validationErrors` object with field-level messages. This completes GitHub issue #2. Further refinement of domain exception semantics remains possible as the service layer evolves.

## Future improvements

### Correct string comparison

Business rules should use `.equals()` or `Objects.equals()` instead of `==`.

Example:

```java
if (Objects.equals(category.getName(), dto.categoryName())) {
    // ...
}
```

The `==` operator compares object references, not necessarily string contents.

### Relationship updates

When updating a todo, the service should assign the folder and category found in the database instead of directly changing the names of related entities.

This prevents renaming a category shared by multiple todos.

### Pagination and sorting

Add pagination to list endpoints:

```text
GET /todos?page=0&size=20&sort=title,asc
```

This prevents loading all records into memory as the database grows.

### Testing

Expand coverage with:

- Unit tests for services
- Controller tests
- PostgreSQL integration tests
- Validation tests
- Duplicate-resource scenarios
- Relationship deletion tests
- Migration tests

### API documentation

Add OpenAPI/Swagger to provide:

- Endpoint visualization
- Browser-based API testing
- Payload documentation
- Response and error documentation

### Observability

Add:

- Structured logging
- Spring Boot Actuator
- Health checks
- Metrics
- Request tracing

### Security

Future security improvements may include:

- Spring Security
- Authentication
- Authorization
- User ownership of todos
- Protection against accessing another user's data

### Environment-based configuration

Move credentials and URLs to environment variables:

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

The credentials in `compose.yaml` are intended only for local development.

### CI/CD

Create a pipeline that:

1. Compiles the project
2. Runs the tests
3. Validates migrations
4. Creates the application artifact
5. Builds a Docker image
6. Publishes or deploys the application

## Project structure

```text
.
├── compose.yaml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── src
│   ├── main
│   │   ├── java/dev/souto/todo
│   │   │   ├── controller
│   │   │   ├── domain
│   │   │   ├── dto
│   │   │   ├── repository
│   │   │   ├── service
│   │   │   └── TodoApplication.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── db
│   │           ├── data.sql
│   │           └── migration
│   │               └── V1__init.sql
│   └── test
│       └── java/dev/souto/todo
└── README.md
```

## License

This project is available under the license defined in [`LICENSE`](LICENSE).
