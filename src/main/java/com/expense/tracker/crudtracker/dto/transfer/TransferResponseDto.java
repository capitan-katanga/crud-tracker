package com.expense.tracker.crudtracker.dto.transfer;

import com.expense.tracker.crudtracker.dto.TransactionDetailResponseDto;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("TRANSFER")
public record TransferResponseDto(UUID id,
                                  String senderName,
                                  String senderCuit,
                                  String senderInstitution,
                                  String senderAccount,
                                  String receiverName,
                                  String receiverCuit,
                                  String receiverInstitution,
                                  String receiverAccount,
                                  String operationNumber,
                                  String referenceCode,
                                  String motive) implements TransactionDetailResponseDto {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
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

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

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

        public TransferResponseDto build() {
            return new TransferResponseDto(this.id, this.senderName, this.senderCuit, this.senderInstitution,
                    this.senderAccount, this.receiverName, this.receiverCuit, this.receiverInstitution,
                    this.receiverAccount, this.operationNumber, this.referenceCode, this.motive);
        }
    }

}
