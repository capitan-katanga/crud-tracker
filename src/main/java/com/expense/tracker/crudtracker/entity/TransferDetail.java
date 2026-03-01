package com.expense.tracker.crudtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transfer_details")
public class TransferDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", unique = true, nullable = false)
    private Transaction transaction;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "sender_cuit", length = 30)
    private String senderCuit;

    @Column(name = "sender_institution")
    private String senderInstitution;

    @Column(name = "sender_account", length = 30)
    private String senderAccount;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_cuit", length = 30)
    private String receiverCuit;

    @Column(name = "receiver_institution")
    private String receiverInstitution;

    @Column(name = "receiver_account", length = 30)
    private String receiverAccount;

    @Column(name = "operation_number", length = 100)
    private String operationNumber;

    @Column(name = "reference_code", length = 100)
    private String referenceCode;

    private String motive;

    public TransferDetail() {
    }

    public TransferDetail(UUID id, Transaction transaction, String senderName, String senderCuit, String senderInstitution, String senderAccount, String receiverName, String receiverCuit, String receiverInstitution, String receiverAccount, String operationNumber, String referenceCode, String motive) {
        this.id = id;
        this.transaction = transaction;
        this.senderName = senderName;
        this.senderCuit = senderCuit;
        this.senderInstitution = senderInstitution;
        this.senderAccount = senderAccount;
        this.receiverName = receiverName;
        this.receiverCuit = receiverCuit;
        this.receiverInstitution = receiverInstitution;
        this.receiverAccount = receiverAccount;
        this.operationNumber = operationNumber;
        this.referenceCode = referenceCode;
        this.motive = motive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderCuit() {
        return senderCuit;
    }

    public void setSenderCuit(String senderCuit) {
        this.senderCuit = senderCuit;
    }

    public String getSenderInstitution() {
        return senderInstitution;
    }

    public void setSenderInstitution(String senderInstitution) {
        this.senderInstitution = senderInstitution;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverCuit() {
        return receiverCuit;
    }

    public void setReceiverCuit(String receiverCuit) {
        this.receiverCuit = receiverCuit;
    }

    public String getReceiverInstitution() {
        return receiverInstitution;
    }

    public void setReceiverInstitution(String receiverInstitution) {
        this.receiverInstitution = receiverInstitution;
    }

    public String getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public String getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getMotive() {
        return motive;
    }

    public void setMotive(String motive) {
        this.motive = motive;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransferDetail that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
