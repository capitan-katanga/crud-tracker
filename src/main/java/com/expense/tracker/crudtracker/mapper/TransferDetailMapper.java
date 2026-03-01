package com.expense.tracker.crudtracker.mapper;

import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import com.expense.tracker.crudtracker.dto.transfer.TransferRequestDto;
import com.expense.tracker.crudtracker.dto.transfer.TransferResponseDto;
import com.expense.tracker.crudtracker.entity.Transaction;
import com.expense.tracker.crudtracker.entity.TransferDetail;
import org.springframework.stereotype.Component;

@Component
public class TransferDetailMapper extends TransactionMapper {

    public TransferDetail toEntity(Transaction transaction, TransferRequestDto requestDetail) {
        return new TransferDetail(
                null,
                transaction,
                requestDetail.senderName(),
                requestDetail.senderCuit(),
                requestDetail.senderInstitution(),
                requestDetail.senderAccount(),
                requestDetail.receiverName(),
                requestDetail.receiverCuit(),
                requestDetail.receiverInstitution(),
                requestDetail.receiverAccount(),
                requestDetail.operationNumber(),
                requestDetail.referenceCode(),
                requestDetail.motive()
        );
    }

    public TransactionResponseDto toResponseDto(TransferDetail entity) {
        var transfer = TransferResponseDto.builder()
                .id(entity.getId())
                .senderName(entity.getSenderName())
                .senderCuit(entity.getSenderCuit())
                .senderInstitution(entity.getSenderInstitution())
                .senderAccount(entity.getSenderAccount())
                .receiverName(entity.getReceiverName())
                .receiverCuit(entity.getReceiverCuit())
                .receiverInstitution(entity.getReceiverInstitution())
                .receiverAccount(entity.getReceiverAccount())
                .operationNumber(entity.getOperationNumber())
                .referenceCode(entity.getReferenceCode())
                .motive(entity.getMotive())
                .build();
        return super.toResponseDto(entity.getTransaction(), transfer);
    }

}
