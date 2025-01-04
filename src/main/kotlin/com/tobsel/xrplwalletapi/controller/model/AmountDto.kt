package com.tobsel.xrplwalletapi.controller.model

import com.tobsel.xrplwalletapi.config.Currency
import org.xrpl.xrpl4j.model.transactions.CurrencyAmount

data class AmountDto(
    val value: String,
    val currency: String
) {

    companion object {
        fun from(currencyAmount: CurrencyAmount): AmountDto = currencyAmount.map(
            { AmountDto(it.toXrp().toPlainString(), Currency.XRP) },
            { AmountDto(it.value(), it.currency()) }
        )
    }
}