CREATE TABLE transactions (
    id               uuid           NOT NULL,
    user_id          uuid           NOT NULL,
    type             varchar(50)    NOT NULL,
    amount           numeric(15, 2) NOT NULL,
    currency         varchar(3)     DEFAULT 'ARS' NOT NULL,
    description      varchar(500),
    transaction_date timestamp(6)   NOT NULL,
    created_at       timestamp(6)   NOT NULL,
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

CREATE INDEX idx_transactions_user_id ON transactions (user_id);

CREATE TABLE transfer_details (
    id                   uuid         NOT NULL,
    transaction_id       uuid         NOT NULL,
    sender_name          varchar(255) NOT NULL,
    sender_cuit          varchar(30),
    sender_institution   varchar(255),
    sender_account       varchar(30),
    receiver_name        varchar(255) NOT NULL,
    receiver_cuit        varchar(30),
    receiver_institution varchar(255),
    receiver_account     varchar(30),
    operation_number     varchar(100),
    reference_code       varchar(100),
    motive               varchar(255),
    CONSTRAINT pk_transfer_details PRIMARY KEY (id),
    CONSTRAINT uk_transfer_details_transaction_id UNIQUE (transaction_id),
    CONSTRAINT fk_transfer_details_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (id)
);

CREATE TABLE service_payment_details (
    id                  uuid         NOT NULL,
    transaction_id      uuid         NOT NULL,
    service_name        varchar(255),
    customer_identifier varchar(100),
    payment_method      varchar(100),
    payment_channel     varchar(100),
    payment_processor   varchar(100),
    bank_name           varchar(100),
    transaction_number  varchar(100),
    control_number      varchar(50),
    CONSTRAINT pk_service_payment_details PRIMARY KEY (id),
    CONSTRAINT uk_service_payment_details_transaction_id UNIQUE (transaction_id),
    CONSTRAINT fk_service_payment_details_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (id)
);
