package com.expense.tracker.crudtracker.controller;

import com.expense.tracker.crudtracker.dto.TransactionRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import com.expense.tracker.crudtracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v{version}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(version = "1")
    public ResponseEntity<TransactionResponseDto> registerTransfer(
            @Valid @RequestBody TransactionRequestDto transactionRequestDto
    ) {
        var responseDto = transactionService.registerTransaction(transactionRequestDto);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{transactionId}")
                .buildAndExpand(responseDto.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(responseDto);
    }

    @GetMapping(value = "/{transactionId}", version = "1")
    public ResponseEntity<TransactionResponseDto> getTransactionById(@PathVariable UUID transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }

}
