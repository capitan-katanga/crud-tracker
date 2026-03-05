package com.expense.tracker.crudtracker.dto.transfer;

import com.expense.tracker.crudtracker.dto.TransactionDetailRequestDto;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonTypeName("TRANSFER")
@Schema(description = "Detail payload for transfer-type transactions.")
public record TransferRequestDto(
    @Schema(description = "Name of the sender.", example = "Juan Perez")
    @NotEmpty String senderName,
    @Schema(description = "CUIT of the sender.", example = "20-12345678-9")
    String senderCuit,
    @Schema(description = "Institution of the sender.", example = "Banco Santa Fe")
    String senderInstitution,
    @Schema(description = "Sender's account number.", example = "3660030123456")
    String senderAccount,
    @Schema(description = "Name of the receiver.", example = "Carla Gonzalez")
    @NotEmpty String receiverName,
    @Schema(description = "CUIT of the receiver.", example = "27-87654321-0")
    String receiverCuit,
    @Schema(description = "Institution of the receiver.", example = "Banco Galicia")
    String receiverInstitution,
    @Schema(description = "Receiver's account number.", example = "4401234001237")
    String receiverAccount,
    @Schema(description = "Internal operation number.", example = "000921332")
    String operationNumber,
    @Schema(description = "Reference code.", example = "ABC-2024-TF1")
    String referenceCode,
    @Schema(description = "Motivation or description of the transfer.", example = "Salary payment")
    String motive
) implements TransactionDetailRequestDto {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String senderName;
        private String senderCuit;
        private String senderInstitution;
        private String senderAccount;
        private String receiverName;
        private String receiverCuit;
        private String receiverInstitution;
        private String receiverAccount;
        private String operationNumber;
        private String referenceCode;
        private String motive;

        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder senderCuit(String senderCuit) {
            this.senderCuit = senderCuit;
            return this;
        }

        public Builder senderInstitution(String senderInstitution) {
            this.senderInstitution = senderInstitution;
            return this;
        }

        public Builder senderAccount(String senderAccount) {
            this.senderAccount = senderAccount;
            return this;
        }

        public Builder receiverName(String receiverName) {
            this.receiverName = receiverName;
            return this;
        }

        public Builder receiverCuit(String receiverCuit) {
            this.receiverCuit = receiverCuit;
            return this;
        }

        public Builder receiverInstitution(String receiverInstitution) {
            this.receiverInstitution = receiverInstitution;
            return this;
        }

        public Builder receiverAccount(String receiverAccount) {
            this.receiverAccount = receiverAccount;
            return this;
        }

        public Builder operationNumber(String operationNumber) {
            this.operationNumber = operationNumber;
            return this;
        }

        public Builder referenceCode(String referenceCode) {
            this.referenceCode = referenceCode;
            return this;
        }

        public Builder motive(String motive) {
            this.motive = motive;
            return this;
        }

        public TransferRequestDto build() {
            return new TransferRequestDto(this.senderName, this.senderCuit, this.senderInstitution,
                    this.senderAccount, this.receiverName, this.receiverCuit, this.receiverInstitution,
                    this.receiverAccount, this.operationNumber, this.referenceCode, this.motive);
        }
    }
}
