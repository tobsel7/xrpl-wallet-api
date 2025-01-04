package com.tobsel.xrplwalletapi.service.model

import com.tobsel.xrplwalletapi.config.properties.CalculationConstant.RIPPLE_EPOCH_SECONDS
import org.xrpl.xrpl4j.model.client.accounts.AccountTransactionsTransactionResult
import org.xrpl.xrpl4j.model.client.common.LedgerIndex
import org.xrpl.xrpl4j.model.transactions.Address
import org.xrpl.xrpl4j.model.transactions.CurrencyAmount
import org.xrpl.xrpl4j.model.transactions.Hash256
import org.xrpl.xrpl4j.model.transactions.Payment
import kotlin.jvm.optionals.getOrNull

data class LedgerPayment(
    val hash: Hash256,
    val source: Address,
    val destination: Address,
    val amount: CurrencyAmount,
    val execution: Execution
) {

    data class Execution(
        val validated: Boolean,
        val ledgerIndex: LedgerIndex,
        val epochSeconds: Long?,
    )

    companion object {
        fun from(accountTransactionResult: AccountTransactionsTransactionResult<Payment>) =
            with(accountTransactionResult) {
                LedgerPayment(
                    hash = resultTransaction().hash(),
                    source = resultTransaction().transaction().account(),
                    destination = resultTransaction().transaction().destination(),
                    amount = resultTransaction().transaction().amount(),
                    execution = Execution(
                        validated = validated(),
                        ledgerIndex = resultTransaction().ledgerIndex(),
                        epochSeconds = resultTransaction().closeDate()
                            .map { it.toLong() + RIPPLE_EPOCH_SECONDS }
                            .getOrNull()
                    )
                )
            }
    }
}