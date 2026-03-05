package com.expense.tracker.crudtracker.dto;

import com.expense.tracker.crudtracker.dto.transfer.TransferRequestDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Payload for a transaction registration request. Transaction 'type' determines the detail polymorphic payload.")
public record TransactionRequestDto(
        @Schema(description = "UUID of the user initiating the transaction.", example = "12c40443-acc4-440d-8091-05a1dc3b011a")
        @NotNull UUID userId,

        @Schema(description = "Transaction type (TRANSFER, SERVICE_PAYMENT, etc). Controls polymorphism on 'detail'.", example = "TRANSFER")
        String type,

        @Schema(description = "Amount of the transaction.", example = "1000.00")
        @NotNull BigDecimal amount,

        @Schema(description = "Currency code (ISO 4217).", example = "ARS")
        String currency,

        @Schema(description = "Description of the transaction.", example = "Wire transfer to Carla Gonzalez")
        String description,

        @Schema(description = "Date and time of the transaction (ISO format).", example = "2024-06-01T12:44:00")
        @NotNull LocalDateTime transactionDate,

        @Valid
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type"
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = TransferRequestDto.class, name = "TRANSFER")
                // add new type here.
        })
        @Schema(description = "Detail payload. Type-specific, resolved by the 'type' field.")
        TransactionDetailRequestDto detail
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID userId;
        private String type;
        private BigDecimal amount;
        private String currency;
        private String description;
        private LocalDateTime transactionDate;
        private TransactionDetailRequestDto detail;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder transactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder detail(TransactionDetailRequestDto transferDetail) {
            this.detail = transferDetail;
            return this;
        }

        public TransactionRequestDto build() {
            return new TransactionRequestDto(this.userId, this.type, this.amount, this.currency,
                    this.description, this.transactionDate, this.detail);
        }
    }
}
