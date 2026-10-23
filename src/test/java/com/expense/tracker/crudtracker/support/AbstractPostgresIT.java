package com.expense.tracker.crudtracker.support;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class AbstractPostgresIT {

    protected static final String DB_ROLE = "transactional";

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

    // sin @ServiceConnection: usaría el superusuario y no probaría los permisos del rol
    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", () -> DB_ROLE);
        registry.add("spring.datasource.password", () -> "transactional-it");
        registry.add("spring.docker.compose.enabled", () -> "false");
    }

    protected static JdbcTemplate adminJdbc() {
        return new JdbcTemplate(new DriverManagerDataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword()));
    }

}
