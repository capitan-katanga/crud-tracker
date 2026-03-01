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

import java.util.UUID;

@Entity
@Table(name = "service_payment_details")
public class ServicePaymentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", unique = true, nullable = false)
    private Transaction transaction;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "customer_identifier", length = 100)
    private String customerIdentifier;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "payment_channel", length = 100)
    private String paymentChannel;

    @Column(name = "payment_processor", length = 100)
    private String paymentProcessor;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "transaction_number", length = 100)
    private String transactionNumber;

    @Column(name = "control_number", length = 50)
    private String controlNumber;

}
