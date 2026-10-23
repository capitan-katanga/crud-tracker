package com.expense.tracker.crudtracker.controller;

import com.expense.tracker.crudtracker.entity.TransactionType;
import com.expense.tracker.crudtracker.support.AbstractPostgresIT;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.expense.tracker.crudtracker.mock.TestUtils.TEST_AMOUNT;
import static com.expense.tracker.crudtracker.mock.TestUtils.TEST_CURRENCY;
import static com.expense.tracker.crudtracker.mock.TestUtils.TEST_DATE;
import static com.expense.tracker.crudtracker.mock.TestUtils.TEST_DESCRIPTION;
import static com.expense.tracker.crudtracker.mock.TestUtils.TEST_RECEIVER_NAME;
import static com.expense.tracker.crudtracker.mock.TestUtils.TEST_SENDER_NAME;
import static com.expense.tracker.crudtracker.mock.TestUtils.TEST_USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionApiIT extends AbstractPostgresIT {

    private static final String TRANSACTIONS_PATH = "/api/v1/transactions/";

    private final MockMvc mockMvc;

    TransactionApiIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("Registers a transfer and reads it back from the transactional schema")
    void registersAndReadsTransfer() throws Exception {
        var body = """
                {"userId":"%s","type":"TRANSFER","amount":%s,"currency":"%s","description":"%s",
                 "transactionDate":"%s","detail":{"senderName":"%s","receiverName":"%s"}}
                """.formatted(TEST_USER_ID, TEST_AMOUNT, TEST_CURRENCY, TEST_DESCRIPTION, TEST_DATE,
                TEST_SENDER_NAME, TEST_RECEIVER_NAME);

        var created = mockMvc.perform(post(TRANSACTIONS_PATH).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        var id = UUID.fromString(JsonPath.read(created.getContentAsString(), "$.id"));

        mockMvc.perform(get(TRANSACTIONS_PATH + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.detail.senderName").value(TEST_SENDER_NAME))
                .andExpect(jsonPath("$.detail.receiverName").value(TEST_RECEIVER_NAME));

        var admin = adminJdbc();
        Assertions.assertAll(
                () -> assertThat(created.getHeader("Location"), endsWith(id.toString())),
                () -> assertThat(admin.queryForObject(
                        "SELECT count(*) FROM transactional.transactions WHERE id = ?", Integer.class, id), equalTo(1)),
                () -> assertThat(admin.queryForObject(
                        "SELECT sender_name FROM transactional.transfer_details WHERE transaction_id = ?", String.class, id),
                        equalTo(TEST_SENDER_NAME))
        );
    }

    @Test
    @DisplayName("Returns 404 ProblemDetail for an unknown transaction id")
    void returnsNotFoundProblemDetail() throws Exception {
        mockMvc.perform(get(TRANSACTIONS_PATH + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Transaction Not Found"));
    }

    @Test
    @DisplayName("Registers an UNKNOWN transaction without detail and without transfer_details row")
    void registersUnknownWithoutDetail() throws Exception {
        // detail null explícito: omitirlo da 400 por el EXTERNAL_PROPERTY de Jackson (comportamiento previo de la API)
        var body = """
                {"userId":"%s","type":"%s","amount":%s,"currency":"%s","transactionDate":"%s","detail":null}
                """.formatted(TEST_USER_ID, TransactionType.UNKNOWN, TEST_AMOUNT, TEST_CURRENCY, TEST_DATE);

        var created = mockMvc.perform(post(TRANSACTIONS_PATH).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse();
        var id = UUID.fromString(JsonPath.read(created.getContentAsString(), "$.id"));

        assertThat(adminJdbc().queryForObject(
                "SELECT count(*) FROM transactional.transfer_details WHERE transaction_id = ?", Integer.class, id),
                equalTo(0));
    }

}
