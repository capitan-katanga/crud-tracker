# AGENTS.md

## Project Overview

Spring Boot 4.0.2 expense tracker CRUD API. Java 25, PostgreSQL, Maven.
No Lombok, no MapStruct. Manual mappers and explicit getters/setters/constructors.

## Build & Run Commands

All commands use system Maven (`mvn`).

```bash
# Compile (skip tests)
mvn compile

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest="TransactionServiceImplTest"

# Run a single test method
mvn test -Dtest="TransactionServiceImplTest#getTransactionNotFound"

# Package (produces JAR in target/)
mvn package

# Run the application (requires PostgreSQL via Docker Compose)
mvn spring-boot:run

# Clean build
mvn clean verify
```

There is no configured linter or formatter. No Checkstyle, SpotBugs, or PMD plugins.

## Infrastructure

- PostgreSQL runs via Docker Compose (`compose.yaml`), exposed on host port **5332** (not 5432)
- Spring Boot auto-starts DB (`spring.docker.compose.lifecycle-management: start-only`)
- Datasource credentials from env vars: `POSTGRES_DB_NAME` / `POSTGRES_DB_PASSWORD` (local dev: `admin`/`Password123`)
- Observability: Grafana LGTM stack (Grafana, Loki, Tempo, Mimir) via compose, OpenTelemetry traces/metrics/logs exported to localhost (`4317`, `4318`)
- Virtual threads are enabled (`spring.threads.virtual.enabled: true`)

## Project Structure

```
src/main/java/com/expense/tracker/crudtracker/
  config/          OpenTelemetry/logback config
  controller/      REST controllers (@RestController)
  service/         Service interfaces
  service/impl/    Service implementations (@Service)
  repository/      Spring Data JPA repositories (@Repository)
  entity/          JPA entities (classes), enums
  dto/             Request/response DTOs (records), marker interfaces
  dto/transfer/    Transfer-specific DTOs
  dto/service/     Service-payment-specific DTOs
  mapper/          Manual mapping classes (@Component or plain)
  strategy/        Strategy pattern for transaction-type dispatch
  exception/       Custom exceptions, @RestControllerAdvice handler

src/test/java/com/expense/tracker/crudtracker/
  service/impl/    Service layer unit tests
  mock/            TestUtils (shared constants, factories)
```

## Code Style Guidelines

### Formatting & Imports
- 4-space indentation, no tabs
- Opening brace on same line as declaration; closing brace on its own line
- One blank line between methods
- Project, third-party, Jakarta, Spring, Java imports—in that order. Each import on its own line. **No wildcard imports** in production code
- Static wildcard imports allowed in test files for Hamcrest, Mockito
- No import grouping or file headers required by convention

### Types & Data Modeling
- **DTOs:** Java `record` types (not classes, not Lombok)
- **Entities:** Plain classes with explicit no-arg constructor, all-args constructor, getters, setters, `equals()`, `hashCode()`
- **Enums:** For fixed domain values (e.g., `TransactionType`)
- **Marker interfaces:** Used for sealed polymorphism (`TransactionDetailRequestDto`, `TransactionDetailResponseDto`, `TransactionDetail`)
- **Records used in tests:** Manual static `Builder` inner class with fluent setters and `build()` method (no Lombok `@Builder`)

### Java Language Features
- Local variable type inference (`var`) used throughout service implementations and tests
- Pattern matching `instanceof` (e.g., `if (!(o instanceof Transaction that))`)
- Logging via SLF4J: `private static final Logger log = LoggerFactory.getLogger(...)` (no Lombok `@Slf4j`)

### Naming Conventions
- **Packages:** Lowercase, singular (`entity`, `repository`, `strategy`)
- **Classes:** PascalCase; domain entities (`Transaction`, `TransferDetail`)
- **Service interfaces:** `TransactionService`, implementations: `TransactionServiceImpl`
- **DTOs:** Suffix with `Dto` (`TransactionRequestDto`, `TransactionResponseDto`). Subtypes in respective sub-packages
- **Mapper/Repository:** Suffix with `Mapper`/`Repository` (`TransactionMapper`, `TransferDetailRepository`)
- **Exceptions:** Descriptive + `Exception` suffix (`TransactionNotFoundException`)
- **Strategy classes:** Suffix with `Strategy` (`TransferDetailStrategy`)
- **Test classes:** Suffix with `Test`, mirror source class path; test classes are package-private (no `public`)
- **Test methods:** camelCase, descriptive (e.g., `registersTransactionSuccessfully`)
- **Constants:** `UPPER_SNAKE_CASE` for `static final` fields

### Validation
- **Validation enabled** with `spring-boot-starter-validation`
- Controllers use `@Validated` for request validation
- DTO record components annotated `@NotNull`, `@NotEmpty`, or `@Valid` as appropriate

### Dependency Injection
- Constructor injection only (no `@Autowired` anywhere)
- Dependencies are `private final`, assigned in constructor
- Spring beans: `@Service`, `@Component`, `@Repository`, `@RestController`

### Error Handling
- Custom exceptions extend `RuntimeException` with a single `String message` constructor
- `@RestControllerAdvice` (`CustomExceptionHandler`) extends `ResponseEntityExceptionHandler`; returns RFC 9457 `ProblemDetail`
- Each `@ExceptionHandler` constructs `ProblemDetail` (status, detail, title, `type` URI)
- Spring MVC problem details enabled (`spring.mvc.problemdetails.enabled: true`)

### Patterns
- **Strategy pattern:**
  - Interface: `TransactionDetailStrategy` with `supports()`, `registerTransactionDetail()`, `getTransactionDetail()`
  - Register: `TransactionDetailStrategyRegister` auto-discovers all strategy beans via constructor-injected `List<>`, builds unmodifiable map
  - Add new transaction type: create `@Component` implementing strategy, add DTO to `@JsonSubTypes` in request/response records
- **Manual mappers:** Inheritance-based: `TransferDetailMapper extends TransactionMapper` (sub-mappers add detail handling via `super.toResponseDto()`)
- **Jackson polymorphism for DTOs:**
  - `@JsonTypeInfo` with `EXTERNAL_PROPERTY` on `detail` field
  - `@JsonSubTypes` lists DTOs mapped to enum names
  - Concrete detail DTOs use `@JsonTypeName("ENUM_VALUE")`
- **API versioning:** Path-segment structure `/api/v{version}/...`, controller methods specify `version = "1"` on mappings

## Testing Conventions

### Stack
- JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`) + Hamcrest
- No Spring context loaded in tests, pure mock-based

### Structure
- `@Mock` for dependencies, `@Spy` for mappers needing real calls, `@InjectMocks` for class under test
- `@DisplayName` on every test method
- Parameterized tests: `@ParameterizedTest` + `@EnumSource(enumClass)`, exclude values with `mode = EnumSource.Mode.EXCLUDE`
- Test classes are package-private (no `public` modifier)

### Assertions and Verification
- Group assertions: `Assertions.assertAll()`
- Use Hamcrest: `assertThat(actual, equalTo(expected))`
- Exception testing: `Assertions.assertThrows(ExceptionClass.class, () -> ...)`, then `assertThat(..., equalTo(...))`
- Verify mock interactions: `verify(mock, times(n)).method(...)`, `verifyNoInteractions(mock)`

### Test Data
- Shared constants/factories: `src/test/java/.../mock/TestUtils.java`
- Constants: `public static final`, prefixed `TEST_`
- Factory methods: `public static`, named `create<Entity/Dto>(...)`
- TestUtils has a private constructor
