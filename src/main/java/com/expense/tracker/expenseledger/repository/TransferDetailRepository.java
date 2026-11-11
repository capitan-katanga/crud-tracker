package com.expense.tracker.expenseledger.repository;

import com.expense.tracker.expenseledger.entity.Transaction;
import com.expense.tracker.expenseledger.entity.TransferDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferDetailRepository extends JpaRepository<TransferDetail, UUID> {
    Optional<TransferDetail> findByTransaction(Transaction transaction);
}
