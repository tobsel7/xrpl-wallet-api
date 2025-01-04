package com.tobsel.xrplwalletapi.controller.model

import com.tobsel.xrplwalletapi.service.model.Wallet
import java.util.UUID

data class WalletCreationDto(
    val name: String
)

data class WalletDto(
    val id: UUID,
    val name: String,
    val xrpDropsBalance: Long,
    val publicAddress: String
) {

    companion object {
        fun from(wallet: Wallet) = WalletDto(
            id = wallet.id,
            name = wallet.name,
            xrpDropsBalance = wallet.ledgerAccountInfo.balance().value().toLong(),
            publicAddress = wallet.publicKey.deriveAddress().value()
        )
    }
}
