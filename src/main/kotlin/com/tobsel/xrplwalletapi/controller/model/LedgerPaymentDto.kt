package com.tobsel.xrplwalletapi.controller.model

import com.tobsel.xrplwalletapi.service.model.LedgerPayment
import java.time.Instant

data class LedgerPaymentDto(
    val hash: String,
    val source: String,
    val destination: String,
    val amount: AmountDto,
    val execution: ExecutionDto?
) {

    data class ExecutionDto(
        val validated: Boolean,
        val ledgerIndex: Long,
        val dateTime: Instant?,
    )

    companion object {
        fun from(ledgerPayment: LedgerPayment) = LedgerPaymentDto(
            hash = ledgerPayment.hash.value(),
            source = ledgerPayment.source.value(),
            destination = ledgerPayment.destination.value(),
            amount = AmountDto.from(ledgerPayment.amount),
            execution = ExecutionDto(
                validated = ledgerPayment.execution.validated,
                ledgerIndex = ledgerPayment.execution.ledgerIndex.unsignedIntegerValue().toLong(),
                dateTime = ledgerPayment.execution.epochSeconds?.let { Instant.ofEpochSecond(it) }
            )
        )
    }
}

