# Todo Java Spring Boot Playground

REST API for managing tasks, built with Java and Spring Boot. This project is a learning playground for practicing backend development, MongoDB persistence, document relationships, and layered application design.

## Project goals

This project explores:

- REST API development with Spring Boot
- Separation between controllers, services, repositories, and entities
- Persistence with Spring Data MongoDB
- MongoDB database integration
- MongoDB initialization scripts
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
| Spring Data MongoDB | Persistence abstraction |
| MongoDB 7 | Document database |
| Maven | Dependency and build management |
| Docker Compose | Local MongoDB environment |
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
MongoDB
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

Repositories use Spring Data MongoDB to access the database without manually writing basic queries.

Examples:

- `TodoRepo`
- `FolderRepo`
- `CategoryRepo`

Repositories reduce repetitive code and provide abstractions such as `findById`, `findAll`, `save`, and name-based queries.

### Entities

Entities represent MongoDB documents:

- `TodoEntity` → `todos` collection
- `FolderEntity` → `folders` collection
- `CategoryEntity` → `categories` collection

The entities use Spring Data MongoDB annotations such as:

- `@Document`
- `@Id`
- `@DBRef`

## Data model

The application has three main entities:

```text
Folder 1 ──────── N Todo N ──────── 1 Category
```

Folders and categories are stored as separate MongoDB documents. Todos reference
their related documents using MongoDB DBRefs.

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

### Document rules

The application applies the following rules:

- Every document has an identifier
- Folder and category names are required
- Todo titles are required
- Every todo must reference a folder
- A todo category may be null
- Relationship validation is handled in the service layer

## DTOs

The API does not expose persistence entities directly. Instead, it uses DTOs:

- `TodoCreateRequestDTO`
- `TodoUpdateRequestDTO`
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

## MongoDB initialization

The following file creates sample documents for manual testing:

```text
src/main/resources/mongo-init.js
```

The script runs automatically when the MongoDB container initializes its data
volume. It creates sample folders, categories, and todos using idempotent
`updateOne` operations with `upsert`.

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

## MongoDB configuration

The project includes a `compose.yaml` file with MongoDB 7:

```yaml
services:
  mongodb:
    image: mongo:7.0
    ports:
      - "27017:27017"
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

With MongoDB running, execute:

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

If port 8080 is already in use, configure another port in `application.yml`:

```yaml
server:
  port: 8081
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

The test suite verifies that the application context can start and exercises
the main controller validation and persistence flows:

```java
@SpringBootTest
class TodoApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

The tests require a running MongoDB instance. Start MongoDB with Docker Compose
before running the test suite:

```bash
docker compose up -d
./mvnw test
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
| `GET` | `/todos` | List todos using the default page (`page=0`, `size=10`) |
| `GET` | `/todos?page=0&size=20` | List todos with pagination |
| `GET` | `/todos/{id}` | Find a todo by ID |
| `POST` | `/todos` | Create a todo |
| `PUT` | `/todos/{id}` | Update a todo |
| `DELETE` | `/todos/{id}` | Delete a todo (`204 No Content`) |

Example:

```http
POST /todos
Content-Type: application/json
```

```json
{
  "title": "Implement todo API",
  "description": "Create endpoints for listing, creating, and updating todos.",
  "folderId": "UUID of an existing folder",
  "categoryId": "UUID of an existing category"
}
```

### Folders

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/folders` | List folders using the default page (`page=0`, `size=10`) |
| `GET` | `/folders?page=0&size=20` | List folders with pagination |
| `GET` | `/folders/{id}` | Find a folder by ID |
| `POST` | `/folders` | Create a folder |
| `PATCH` | `/folders/{id}` | Update a folder |
| `DELETE` | `/folders/{id}` | Delete a folder (`204 No Content`) |

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
| `GET` | `/categories` | List categories using the default page (`page=0`, `size=10`) |
| `GET` | `/categories?page=0&size=20` | List categories with pagination |
| `GET` | `/categories/{id}` | Find a category by ID |
| `POST` | `/categories` | Create a category |
| `PATCH` | `/categories/{id}` | Update a category |
| `DELETE` | `/categories/{id}` | Delete a category (`204 No Content`) |

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

### Paginated responses

List endpoints return a paginated response with this structure:

```json
{
  "items": [
    {
      "title": "Implement todo API",
      "description": "Create endpoints for listing, creating, and updating todos.",
      "category": {
        "name": "Development"
      },
      "folder": {
        "name": "Work"
      }
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 6,
  "hasNext": false
}
```

The `page` parameter is zero-based. The `size` parameter must be positive.
When omitted, list endpoints use `page=0` and `size=10`.

## Creating a todo

The folder and category IDs provided when creating a todo must reference
existing records.

Typical flow:

```text
1. Create or find a folder
2. Create or find a category
3. Send their IDs in POST /todos
4. The service loads the relationships from MongoDB
5. The todo is persisted with references to the related documents
```

Relationship resolution remains in the service layer, while the API explicitly
uses the IDs of existing folders and categories. The referenced folder and
category must already exist.

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

### Relationship validation

The service layer validates folder and category references before persisting a
todo, since MongoDB does not enforce relational foreign keys.

### Immutable response DTOs

Response DTOs are defined as `record`, which is appropriate for simple data-transfer objects.

### Document-oriented persistence

MongoDB documents and DBRefs allow the persistence model to evolve without
relational schema migrations.

### Dependency injection

Services receive their repositories through constructors. This makes dependencies explicit and facilitates unit testing.

## Completed improvements

### Request DTO validation — Issue #1

Request DTOs now validate required and bounded fields with Bean Validation:

- `TodoCreateRequestDTO`
  - `title`: required, up to 255 characters
  - `description`: up to 2,000 characters
  - `categoryId`: required UUID
  - `folderId`: required UUID
- `TodoUpdateRequestDTO`
  - `title`: required, up to 255 characters
  - `description`: up to 2,000 characters
  - `categoryId`: required UUID
  - `folderId`: required UUID
- `FolderRequestDTO`
  - `name`: required, up to 255 characters
- `CategoryRequestDTO`
  - `name`: required, up to 255 characters

Controllers use `@Valid` and `@RequestBody`, so invalid request payloads are rejected before reaching the service layer. This completes GitHub issue #1.

### Global exception handling — Issue #2

The API now provides centralized exception handling through
`@RestControllerAdvice`.

The current handlers cover:

- `400 Bad Request` for DTO validation failures
- `400 Bad Request` for business-rule violations
- A consistent `ErrorResponse` containing:
  - `timestamp`
  - `status`
  - `error`
  - `messages`

Business-rule failures are represented by `BusinessRulesException`.
Additional mappings for not-found resources, malformed JSON, and unexpected
server errors can be added as the error model evolves.

### Pagination — Current implementation

List endpoints for todos, folders, and categories support zero-based
pagination through the `page` and `size` query parameters.

Responses use the shared `PageResponse` structure with:

- `items`: records in the current page
- `page`: zero-based page number
- `size`: requested page size
- `totalElements`: total number of records
- `hasNext`: whether another page is available

## Future improvements

### Sorting

Sorting is not implemented yet. The following request is planned for a future
improvement:

```text
GET /todos?page=0&size=20&sort=title,asc
```

Sorting should be added to the paginated endpoints with validated fields and
directions.

### Testing

The current integration tests cover:

- folder and category validation errors
- folder and category creation
- todo creation with existing relationships
- todo validation when relationship IDs are missing
- todo creation with non-existing relationships
- folder and category deletion with `204 No Content`

The following coverage is still planned:

- pagination response tests
- Unit tests for services
- controller tests for update operations
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

Move the MongoDB URI to an environment variable:

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}
```

The default MongoDB URI is intended only for local development.

### CI/CD

Create a pipeline that:

1. Compiles the project
2. Runs the tests
3. Validates MongoDB initialization
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
│   │   │   ├── entity
│   │   │   ├── dto
│   │   │   ├── repository
│   │   │   ├── service
│   │   │   └── TodoApplication.java
│   │   └── resources
│       │   ├── application.yml
│       │   └── mongo-init.js
│   └── test
│       └── java/dev/souto/todo
└── README.md
```

## License

This project is available under the license defined in [`LICENSE`](LICENSE).
