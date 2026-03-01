# AGENTS.md

## Project Overview

Spring Boot 4.0.2 expense tracker CRUD API. Java 25, PostgreSQL, Maven.
No Lombok, no MapStruct. Manual mappers and explicit getters/setters/constructors.

## Build & Run Commands

All commands use the Maven wrapper (`./mvnw`). Do NOT use a system-installed `mvn`.

```bash
# Compile (skip tests)
./mvnw compile

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest="TransactionServiceImplTest"

# Run a single test method
./mvnw test -Dtest="TransactionServiceImplTest#getTransactionNotFound"

# Package (produces JAR in target/)
./mvnw package

# Run the application (requires PostgreSQL via Docker Compose)
./mvnw spring-boot:run

# Clean build
./mvnw clean verify
```

There is no linter or formatter configured. No Checkstyle, SpotBugs, or PMD plugins.

## Infrastructure

PostgreSQL runs via Docker Compose (`compose.yaml`). Spring Boot auto-starts it
(`spring.docker.compose.lifecycle-management: start-only`).
The DB is exposed on host port **5332** (not the default 5432).

```bash
# Start DB manually if needed
docker compose up -d
```

Datasource credentials come from environment variables:
`POSTGRES_DB_NAME` and `POSTGRES_DB_PASSWORD`. The compose file sets
`admin` / `Password123` for local dev.

## Project Structure

```
src/main/java/com/expense/tracker/crudtracker/
  controller/       REST controllers (@RestController)
  service/           Service interfaces
  service/impl/      Service implementations (@Service)
  repository/        Spring Data JPA repositories (@Repository)
  entity/            JPA entities (classes) and enums
  dto/               Request/response DTOs (records), marker interfaces
  dto/transfer/      Transfer-specific DTOs
  dto/service/       Service-payment-specific DTOs
  mapper/            Manual mapping classes (@Component or plain)
  strategy/          Strategy pattern for transaction-type dispatch
  exception/         Custom exceptions and @RestControllerAdvice handler

src/test/java/com/expense/tracker/crudtracker/
  service/impl/      Unit tests for service layer
  mock/              TestUtils with shared constants and factory methods
```

## Code Style

### Formatting

- **4-space indentation**, no tabs.
- Opening brace on the **same line** as the declaration.
- One blank line between methods.
- One import per line. **No wildcard imports** in production code.
  Static wildcard imports are acceptable in tests (Hamcrest matchers, Mockito stubs).
- Imports are ordered: project classes, then `jakarta.*`, then `org.*`, then `java.*`.

### Types & Data Modeling

- **DTOs are Java `record` types.** Not classes. Not Lombok `@Data`.
- **Entities are plain classes** with explicit no-arg constructor, all-args constructor,
  getters, setters, `equals()`, and `hashCode()`.
- **Enums** for fixed domain values (e.g., `TransactionType`).
- **Marker interfaces** for polymorphic DTOs (`TransactionDetailRequestDto`,
  `TransactionDetailResponseDto`) and entities (`TransactionDetail`).
- Records that need test construction provide a **manual static `Builder`** pattern
  (static inner `Builder` class with fluent setters and a `build()` method).
  Do NOT use Lombok `@Builder`.

### Naming Conventions

- **Packages:** all lowercase, singular (e.g., `entity`, `repository`, `strategy`).
- **Classes:** PascalCase. Entities match the domain noun (`Transaction`, `TransferDetail`).
- **Service interfaces:** `TransactionService`. Implementations: `TransactionServiceImpl`.
- **DTOs:** suffix with `Dto` -- `TransactionRequestDto`, `TransactionResponseDto`.
  Sub-type DTOs go in sub-packages (`dto/transfer/`, `dto/service/`).
- **Mappers:** suffix with `Mapper` -- `TransactionMapper`, `TransferDetailMapper`.
- **Repositories:** suffix with `Repository`.
- **Exceptions:** descriptive name + `Exception` suffix (`TransactionNotFoundException`).
- **Strategy classes:** suffix with `Strategy` (`TransferDetailStrategy`).
- **Test classes:** mirror the source class path. Suffix with `Test`.
- **Test methods:** `camelCase`, descriptive verb phrases (e.g.,
  `registersTransactionSuccessfully`, `getTransactionNotFound`).
- **Constants:** `UPPER_SNAKE_CASE` for `static final` fields.

### Dependency Injection

- **Constructor injection only.** No `@Autowired` annotation anywhere.
- Dependencies are `private final` fields assigned in the constructor.
- Spring beans: `@Service`, `@Component`, `@Repository`, `@RestController`.

### Error Handling

- Custom exceptions extend `RuntimeException` with a single `String message` constructor.
- `@RestControllerAdvice` class (`CustomExceptionHandler`) extends
  `ResponseEntityExceptionHandler` and returns `ProblemDetail` (RFC 9457).
- Each `@ExceptionHandler` method creates a `ProblemDetail` with status, detail, title,
  and a `type` URI.
- Spring's built-in problem details are enabled via
  `spring.mvc.problemdetails.enabled: true`.

### Patterns

- **Strategy pattern** for transaction-type-specific logic:
  - `TransactionDetailStrategy` interface with `supports()`, `registerTransactionDetail()`,
    and `getTransactionDetail()`.
  - `TransactionDetailStrategyRegister` auto-discovers all strategy beans via
    constructor-injected `List<TransactionDetailStrategy>` and builds an unmodifiable map.
  - To add a new transaction type: create a new `@Component` implementing
    `TransactionDetailStrategy`, add its DTO to `@JsonSubTypes` in
    `TransactionRequestDto` and `TransactionResponseDto`.
- **Mapper inheritance:** `TransferDetailMapper extends TransactionMapper`.
  The base mapper handles `Transaction` <-> `TransactionResponseDto`.
  Sub-mappers handle detail entities and compose the full response via `super.toResponseDto()`.

### Jackson Polymorphism

- `@JsonTypeInfo` with `EXTERNAL_PROPERTY` on the `detail` field of request/response DTOs.
- `@JsonSubTypes` lists concrete record types mapped to enum names.
- Each concrete detail DTO is annotated with `@JsonTypeName("ENUM_VALUE")`.

### API Versioning

- Path-segment versioning: `/api/v{version}/...`
- Controller methods specify `version = "1"` on `@GetMapping` / `@PostMapping`.
- Configured via `spring.mvc.api-version.use.path-segment: 1`.

## Testing Conventions

### Stack

JUnit 5 + Mockito (via `@ExtendWith(MockitoExtension.class)`) + Hamcrest matchers.
No Spring context loading for unit tests -- pure mock-based tests.

### Structure

- `@Mock` for dependencies, `@Spy` for mappers that need real method calls,
  `@InjectMocks` for the class under test.
- `@DisplayName` annotation on every test method for readable output.
- `@ParameterizedTest` with `@EnumSource` to iterate over enum values.
  Use `mode = EnumSource.Mode.EXCLUDE` to skip specific values.

### Assertions

- Group related assertions with `Assertions.assertAll()`.
- Use **Hamcrest** `assertThat(actual, equalTo(expected))` inside `assertAll` lambdas.
  Do NOT use JUnit `assertEquals`.
- For exception tests: `Assertions.assertThrows(ExceptionClass.class, () -> ...)`,
  then assert message with Hamcrest.
- Verify mock interactions with `verify(mock, times(n)).method(...)` and
  `verifyNoInteractions(mock)`.

### Test Data

- Shared constants and factory methods live in `src/test/.../mock/TestUtils.java`.
- Constants are `public static final` with `TEST_` prefix.
- Factory methods are `public static`, named `create<Entity/Dto>(...)`.
- `TestUtils` has a private constructor to prevent instantiation.
