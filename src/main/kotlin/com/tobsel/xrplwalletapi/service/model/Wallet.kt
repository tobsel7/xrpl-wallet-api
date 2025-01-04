package com.tobsel.xrplwalletapi.service.model

import com.tobsel.xrplwalletapi.repository.model.WalletEntity
import java.util.UUID
import org.xrpl.xrpl4j.codec.addresses.KeyType
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray
import org.xrpl.xrpl4j.crypto.keys.PrivateKey
import org.xrpl.xrpl4j.crypto.keys.PublicKey
import org.xrpl.xrpl4j.model.ledger.AccountRootObject

data class Wallet(
    val id: UUID,
    val name: String,
    val ledgerAccountInfo: AccountRootObject,
    val publicKey: PublicKey,
    val privateKey: PrivateKey
) {

    companion object {
        fun from(wallet: WalletEntity, ledgerAccountInfo: AccountRootObject): Wallet {
            return Wallet(
                id = wallet.id,
                name = wallet.name,
                ledgerAccountInfo = ledgerAccountInfo,
                publicKey = PublicKey.builder().from {
                    UnsignedByteArray.of(wallet.publicKey)
                }.build(),
                privateKey = PrivateKey.fromNaturalBytes(
                    UnsignedByteArray.of(wallet.privateKey),
                    KeyType.ED25519
                )
            )
        }
    }
}