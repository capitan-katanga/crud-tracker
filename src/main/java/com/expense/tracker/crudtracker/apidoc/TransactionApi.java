package com.expense.tracker.crudtracker.apidoc;

import com.expense.tracker.crudtracker.dto.TransactionRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Transactions", description = "Register and retrieve financial transactions. Extensible by transaction type.")
public interface TransactionApi {

    @Operation(
            summary = "Register a new transaction",
            description = "Creates a transaction (operation) and persists it. Response contains transaction details, strategy-based polymorphic detail, and Location header with new transaction URI."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully.", content = @Content(schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    ResponseEntity<TransactionResponseDto> registerTransfer(
            @Parameter(description = "Main transaction payload. The 'type' field determines which transaction detail is expected.", required = true)
            @jakarta.validation.Valid TransactionRequestDto transactionRequestDto
    );

    @Operation(
            summary = "Get a transaction by its ID",
            description = "Fetches a transaction and its polymorphic detail for the given transaction ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found.", content = @Content(schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found.")
    })
    ResponseEntity<TransactionResponseDto> getTransactionById(
            @Parameter(description = "UUID of the transaction to fetch.", required = true)
            UUID transactionId
    );
}
