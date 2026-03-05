package com.expense.tracker.crudtracker.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Type of transaction. Determines detail polymorphism.")
public enum TransactionType {
    TRANSFER,
    SERVICE_PAYMENT,
    UNKNOWN;

    private static final Logger log = LoggerFactory.getLogger(TransactionType.class);

    public static TransactionType safeResolveType(String raw) {
        try {
            return TransactionType.valueOf(raw);
        } catch (IllegalArgumentException _) {
            log.warn("Failed to resolve TransactionType from raw value '{}'. Defaulting to UNKNOWN.", raw);
            return TransactionType.UNKNOWN;
        }
    }

}
