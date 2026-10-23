package com.expense.tracker.crudtracker;

import com.expense.tracker.crudtracker.support.AbstractPostgresIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;

@SpringBootTest
class TransactionalSchemaIT extends AbstractPostgresIT {

    private static final String INSUFFICIENT_PRIVILEGE = "42501";

    private final JdbcTemplate jdbcTemplate;

    TransactionalSchemaIT(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @DisplayName("Flyway applies V1 successfully in the transactional schema")
    void flywayAppliesV1() {
        var history = adminJdbc().queryForList("SELECT version, success FROM transactional.flyway_schema_history");

        Assertions.assertAll(
                () -> assertThat(history, hasSize(1)),
                () -> assertThat(history.getFirst().get("version"), equalTo("1")),
                () -> assertThat(history.getFirst().get("success"), equalTo(true))
        );
    }

    @Test
    @DisplayName("Tables live only in the transactional schema and are owned by the transactional role")
    void tablesLiveInTransactionalSchema() {
        var admin = adminJdbc();
        var tables = admin.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'transactional'", String.class);
        var publicTables = admin.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'", String.class);
        var owners = admin.queryForList(
                "SELECT tableowner FROM pg_tables WHERE schemaname = 'transactional' AND tablename <> 'flyway_schema_history'",
                String.class);

        Assertions.assertAll(
                () -> assertThat(tables, containsInAnyOrder(
                        "transactions", "transfer_details", "service_payment_details", "flyway_schema_history")),
                () -> assertThat(publicTables, empty()),
                () -> assertThat(owners, hasSize(3)),
                () -> assertThat(owners, everyItem(equalTo(DB_ROLE)))
        );
    }

    @Test
    @DisplayName("The application connects as the transactional role with its own schema")
    void connectsAsTransactionalRole() {
        var session = jdbcTemplate.queryForMap("SELECT current_user AS usr, current_schema() AS sch");

        Assertions.assertAll(
                () -> assertThat(session.get("usr"), equalTo(DB_ROLE)),
                () -> assertThat(session.get("sch"), equalTo(DB_ROLE))
        );
    }

    @Test
    @DisplayName("The transactional role cannot create outside its schema nor read other schemas")
    void transactionalRoleIsIsolated() {
        Assertions.assertAll(
                () -> assertInsufficientPrivilege(() -> jdbcTemplate.execute("CREATE TABLE public.x (id int)")),
                () -> assertInsufficientPrivilege(() -> jdbcTemplate.execute("CREATE SCHEMA y")),
                () -> assertInsufficientPrivilege(() -> jdbcTemplate.queryForList("SELECT * FROM foreign_service.secret"))
        );
    }

    private static void assertInsufficientPrivilege(Executable statement) {
        var exception = Assertions.assertThrows(DataAccessException.class, statement);
        var cause = exception.getMostSpecificCause();

        assertThat(cause, instanceOf(SQLException.class));
        assertThat(((SQLException) cause).getSQLState(), equalTo(INSUFFICIENT_PRIVILEGE));
    }

}
