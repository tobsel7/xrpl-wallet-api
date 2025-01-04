package com.tobsel.xrplwalletapi.controller

import com.tobsel.xrplwalletapi.controller.model.LedgerPaymentDto
import com.tobsel.xrplwalletapi.controller.model.TransferInitiationDto
import com.tobsel.xrplwalletapi.service.TransactionService
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wallets/{walletId}")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createPayment(@PathVariable walletId: UUID, @RequestBody transferInitiation: TransferInitiationDto) {
        transactionService.createPayment(
            walletId,
            transferInitiation.destination,
            transferInitiation.amount.value,
            transferInitiation.amount.currency
        )
    }

    @GetMapping("/payments")
    fun getPayments(@PathVariable walletId: UUID): List<LedgerPaymentDto> {
        return transactionService.getPayments(walletId).map(LedgerPaymentDto::from)
    }
}