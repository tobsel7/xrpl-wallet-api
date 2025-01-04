package com.tobsel.xrplwalletapi.controller.model

import com.tobsel.xrplwalletapi.service.model.Wallet
import java.util.UUID

data class WalletCreationDto(
    val name: String
)

data class WalletDto(
    val id: UUID,
    val name: String,
    val publicAddress: String,
    val balances: List<AmountDto>
) {

    companion object {
        fun from(wallet: Wallet) = WalletDto(
            id = wallet.id,
            name = wallet.name,
            publicAddress = wallet.publicKey.deriveAddress().value(),
            balances = listOf(
                AmountDto.from(wallet.ledgerAccountInfo.balance())
            )
        )
    }
}
