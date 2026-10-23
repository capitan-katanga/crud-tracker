package com.expense.tracker.crudtracker;

import com.expense.tracker.crudtracker.support.AbstractPostgresIT;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

// verifica que el application.yaml mapea las variables de pool que fija AbstractPostgresIT
class HikariPoolIT extends AbstractPostgresIT {

    private static final String POOL_NAME = "transactional";
    private static final int CONNECTION_LIMIT = 12;

    private final DataSource dataSource;
    private final MeterRegistry meterRegistry;

    HikariPoolIT(DataSource dataSource, MeterRegistry meterRegistry) {
        this.dataSource = dataSource;
        this.meterRegistry = meterRegistry;
    }

    @Test
    @DisplayName("The DataSource is a fixed Hikari pool named transactional with the configured timeouts")
    void dataSourceIsFixedTransactionalPool() {
        assertThat(dataSource, instanceOf(HikariDataSource.class));
        var hikari = (HikariDataSource) dataSource;

        Assertions.assertAll(
                () -> assertThat(hikari.getPoolName(), equalTo(POOL_NAME)),
                () -> assertThat(hikari.getMaximumPoolSize(), equalTo(POOL_SIZE)),
                () -> assertThat(hikari.getMinimumIdle(), equalTo(POOL_SIZE)),
                () -> assertThat(hikari.getConnectionTimeout(), equalTo(5_000L)),
                () -> assertThat(hikari.getMaxLifetime(), equalTo(1_800_000L)),
                () -> assertThat(hikari.getKeepaliveTime(), equalTo(300_000L)),
                () -> assertThat(hikari.getLeakDetectionThreshold(), equalTo(30_000L)),
                () -> assertThat(hikari.isAutoCommit(), equalTo(true))
        );
    }

    @Test
    @DisplayName("The hikaricp.connections.max gauge is tagged with the transactional pool and equals its size")
    void publishesMaxConnectionsGauge() {
        var gauge = meterRegistry.find("hikaricp.connections.max").tag("pool", POOL_NAME).gauge();

        assertThat(gauge, notNullValue());
        assertThat(gauge.value(), equalTo((double) POOL_SIZE));
    }

    @Test
    @DisplayName("The transactional role has a CONNECTION LIMIT of pool size + 2")
    void roleHasConnectionLimit() {
        var limit = adminJdbc().queryForObject(
                "SELECT rolconnlimit FROM pg_roles WHERE rolname = ?", Integer.class, DB_ROLE);

        assertThat(limit, equalTo(CONNECTION_LIMIT));
    }

    @Test
    @DisplayName("The pool fills up to exactly its size in connections of the transactional role")
    void poolFillsToItsSize() {
        var admin = adminJdbc();

        // Hikari llena el pool en background; un segundo contexto haría pasar de este número o no llenarlo
        await().atMost(Duration.ofSeconds(10)).until(() -> admin.queryForObject(
                "SELECT count(*) FROM pg_stat_activity WHERE usename = ? AND datname = 'ExpenseTracker'",
                Integer.class, DB_ROLE), equalTo(POOL_SIZE));
    }

}
