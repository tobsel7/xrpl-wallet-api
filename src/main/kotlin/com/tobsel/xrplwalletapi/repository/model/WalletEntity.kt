package com.tobsel.xrplwalletapi.repository.model

import com.tobsel.xrplwalletapi.service.model.Wallet
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "wallet")
data class WalletEntity(
    @Id val id: UUID,
    val name: String,
    val publicKey: ByteArray,
    val privateKey: ByteArray
) {
    companion object {
        fun from(wallet: Wallet): WalletEntity {
            return WalletEntity(
                id = wallet.id,
                name = wallet.name,
                publicKey = wallet.publicKey.value().toByteArray(),
                privateKey = wallet.privateKey.naturalBytes().toByteArray()
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WalletEntity) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (!publicKey.contentEquals(other.publicKey)) return false
        if (!privateKey.contentEquals(other.privateKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + publicKey.contentHashCode()
        result = 31 * result + privateKey.contentHashCode()
        return result
    }
}
