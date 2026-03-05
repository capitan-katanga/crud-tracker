# AGENTS.md

## Project Overview

Spring Boot 4.0.2 expense tracker CRUD API. Java 25, PostgreSQL, Maven.
No Lombok, no MapStruct. Manual mappers, explicit getters/setters/constructors.

---

## Build, Test & Run Commands

All commands use system Maven (`mvn`).

```bash
# Compile (skip tests)
mvn compile

# Run all unit & integration tests
mvn test

# Run a single test class
mvn test -Dtest="TransactionServiceImplTest"

# Run a single test method
mvn test -Dtest="TransactionServiceImplTest#getTransactionNotFound"

# Build package (JAR in target/)
mvn package

# Run the app (requires PostgreSQL via Docker Compose)
mvn spring-boot:run

# Clean build (runs full test + JaCoCo coverage checks)
mvn clean verify
```

- No Checkstyle, SpotBugs o PMD configurado por defecto (no hay linter/formatter plugin).
- Code coverage: JaCoCo. Reporte en `target/site/jacoco/index.html`, 90% instrucciones/80% ramas mínimo (excluye config,
  controller, dto, entity, exception, mapper, repository, *Application).
- Test data: PostgreSQL por Docker Compose en puerto **5332**. Variables de entorno: `POSTGRES_DB_NAME` (`admin`),
  `POSTGRES_DB_PASSWORD` (`Password123`).
- Observabilidad: Grafana LGTM stack, OpenTelemetry exporta a localhost (`4317`, `4318`).
- Virtual threads habilitadas: `spring.threads.virtual.enabled: true` en config.

---

## Directory Structure

```
src/main/java/com/expense/tracker/crudtracker/
  config/          Observability config (OpenTelemetry, logback)
  controller/      REST API (@RestController)
  service/         Business logic interfaces
  service/impl/    Business logic implementations (@Service)
  repository/      Spring Data JPA (@Repository)
  entity/          Domain models (JPA entities), enums
  dto/             Request/response DTOs (Java records), marker interfaces
  dto/transfer/    Transfer-specific DTOs
  dto/service/     Service-payment-specific DTOs
  mapper/          Manual mapping (@Component or plain class)
  strategy/        Transaction type dispatch pattern
  exception/       Custom exceptions, exception handler (@RestControllerAdvice)

src/test/java/com/expense/tracker/crudtracker/
  service/impl/    Unit tests for services
  mock/            TestUtils, constants and factory methods
```

---

## Code Style Guidelines

### Formatting & Imports

- 4-space indentation, no tabs
- Opening brace after declaration on same line; closing brace on its own line
- One blank line between methods
- Order: project > third-party > Jakarta > Spring > Java. One import per line. **No wildcard imports** (except static in
  tests for Hamcrest/Mockito)
- No import grouping, reordering, or file headers enforced

### Types & Data Modeling

- **DTOs:** Use Java `record` types (never classes, never Lombok)
- **Entities:** Standard Java class (no Lombok!), explicit no-arg and all-arg constructors, getter/setter, manual
  `equals()/hashCode()`
- **Enums:** For fixed domain values only (`TransactionType`)
- **Marker interfaces:** For sealed polymorphism (`TransactionDetailRequestDto`, etc)
- **Test-only records:** Manual static Builder inner class in tests (not Lombok)

### Java Language Features

- Use local variable type inference (`var`) everywhere (services/tests)
- Use pattern matching `instanceof` (e.g., `if (!(o instanceof Transaction t))`)
- Logging: `private static final Logger log = LoggerFactory.getLogger(...)` (never Lombok)

### Naming

- **Packages:** Lowercase, singular (e.g., `entity`, `strategy`)
- **Classes:** PascalCase (`Transaction`, `TransferDetail`)
- **Services:** Interface: `TransactionService`; Impl: `TransactionServiceImpl`
- **DTOs:** Suffix with `Dto` (`TransactionRequestDto`)
- **Mapper/Repository:** Suffix with `Mapper`/`Repository`
- **Exception:** Suffix `Exception` (`TransactionNotFoundException`)
- **Strategy:** Suffix `Strategy` (`TransferDetailStrategy`)
- **Test:** Suffix `Test`, mirrors src class path; class is *package-private* (not `public`)
- **Test method:** camelCase, descriptive (`registersTransactionSuccessfully`)
- **Constants:** UPPER_SNAKE_CASE for static finals

### Validation

- `spring-boot-starter-validation` enabled
- Controller uses `@Validated`; DTO record components use `@NotNull`, `@NotEmpty`, or `@Valid`

### Dependency Injection

- Use **constructor injection** only (never `@Autowired`!)
- All injected fields are `private final`, set in constructor
- Annotate beans as `@Service`, `@Component`, `@Repository`, `@RestController`

### Error Handling

- Custom exceptions must extend `RuntimeException` with one-arg message constructor
- Central error handler: `@RestControllerAdvice` class (subclass of `ResponseEntityExceptionHandler`)
- Returns RFC 9457 `ProblemDetail` for errors (status, detail, title, type URI)
- `spring.mvc.problemdetails.enabled: true` must be enabled for Error API

### Patterns

- **Strategy pattern:**
    - Interface: `TransactionDetailStrategy` with `supports()`, `registerTransactionDetail()`, `getTransactionDetail()`
    - Register: `TransactionDetailStrategyRegister` auto-discovers strategy beans by constructor-injected `List`, wrap
      in unmodifiable map
    - To add transaction type: create `@Component` strategy, add its DTO in subtypes of request/response records
- **Manual mappers:** Subclassing: `TransferDetailMapper extends TransactionMapper` (use `super.toResponseDto` to map
  base fields)
- **Jackson polymorphism for DTOs:**
    - Use `@JsonTypeInfo` with `EXTERNAL_PROPERTY` in detail field
    - `@JsonSubTypes` lists DTOs mapped to transaction-type enum names
    - Concrete detail DTOs must use `@JsonTypeName("ENUM_VALUE")`
- **API versioning:** Use path-segment `/api/v{version}/...`, controller methods specify `version = "1"`

---

## Test Conventions

### Stack

- JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`) + Hamcrest
- Never load Spring context; pure mock-based access only

### Test Structure

- Use `@Mock` for dependencies, `@Spy` for mappers that need real mapping, `@InjectMocks` for SUT
- `@DisplayName` required on all test methods
- Use `@ParameterizedTest` + `@EnumSource`, use `mode = Exclude` for enums not needed
- Test classes are package-private (no `public`)

### Assertions & Verification

- Use `Assertions.assertAll()` to group multiple assertions
- Use Hamcrest: `assertThat(actual, equalTo(expected))`
- Catch exceptions: `Assertions.assertThrows`, assert details after
- Verify mocks: `verify(mock, times(n)).method(...)`; use `verifyNoInteractions` if relevant

### Test Data

- Use shared constants/factories from `src/test/java/.../mock/TestUtils.java`
- Constants: `public static final`, prefixed `TEST_`
- Factory methods: `public static`, named `create<Entity/Dto>(...)`
- TestUtils must have private constructor

---

## No Cursor .rules or Copilot instructions were detected in this repo. If you add them, summarize those rules here for agent discovery.
