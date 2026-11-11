package com.expense.tracker.crudtracker.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

// configuración de contexto única para todos los *IT: un segundo contexto abre otro pool fijo y supera el CONNECTION LIMIT del rol
@SpringBootTest
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class AbstractPostgresIT {

    protected static final String DB_ROLE = "transactional";
    protected static final int POOL_SIZE = 10;

    // singleton: con @Container se pararía entre clases IT y rompería el contexto cacheado
    protected static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("pgvector/pgvector:pg17").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("ExpenseTracker")
            .withUrlParam("currentSchema", DB_ROLE)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("testcontainers/init-transactional.sql"),
                    "/docker-entrypoint-initdb.d/02-services.sql");

    static {
        POSTGRES.start();
    }

    // sin @ServiceConnection: usaría el superusuario y no probaría los permisos del rol.
    // Las mismas variables del .env, con más precedencia que el .env y las env vars: el test no depende de ellos
    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("TRANSACTIONAL_DB_URL", POSTGRES::getJdbcUrl);
        registry.add("TRANSACTIONAL_DB_USERNAME", () -> DB_ROLE);
        registry.add("TRANSACTIONAL_DB_PASSWORD", () -> "transactional-it");
        registry.add("TRANSACTIONAL_DB_POOL_SIZE", () -> POOL_SIZE);
        registry.add("DB_POOL_CONNECTION_TIMEOUT_MS", () -> 5_000);
        registry.add("DB_POOL_MAX_LIFETIME_MS", () -> 1_800_000);
        registry.add("DB_POOL_KEEPALIVE_TIME_MS", () -> 300_000);
        registry.add("DB_POOL_LEAK_DETECTION_MS", () -> 30_000);
        registry.add("spring.docker.compose.enabled", () -> "false");
    }

    protected static JdbcTemplate adminJdbc() {
        return new JdbcTemplate(new DriverManagerDataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword()));
    }

}
