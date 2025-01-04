package com.tobsel.xrplwalletapi.service

import com.tobsel.xrplwalletapi.repository.WalletRepository
import com.tobsel.xrplwalletapi.repository.model.WalletEntity
import com.tobsel.xrplwalletapi.service.model.Wallet
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xrpl.xrpl4j.client.XrplClient
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray
import org.xrpl.xrpl4j.crypto.keys.PublicKey
import org.xrpl.xrpl4j.crypto.keys.Seed

import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult
import org.xrpl.xrpl4j.model.transactions.Address

private val log = LoggerFactory.getLogger(WalletService::class.java)

@Service
class WalletService(
    private val walletRepository: WalletRepository,
    private val xrplClient: XrplClient,
    private val faucetService: FaucetService
) {

    fun createWallet(name: String): Wallet {
        val randomKeyPair = Seed.ed25519Seed().deriveKeyPair()
        faucetService.fundWallet(randomKeyPair.publicKey().deriveAddress())
        val accountInfo = getAccountInfo(randomKeyPair.publicKey().deriveAddress())

        return Wallet(
            id = UUID.randomUUID(),
            name = name,
            ledgerAccountInfo = accountInfo.accountData(),
            publicKey = randomKeyPair.publicKey(),
            privateKey = randomKeyPair.privateKey()
        ).also { generatedWallet ->
            walletRepository.save(WalletEntity.from(generatedWallet))
            log.info("Creating wallet with name $name. Generated address: ${generatedWallet.publicKey.deriveAddress()}")
        }
    }

    fun getWallets(name: String?): List<Wallet> {
        val savedWallets = if (name == null) walletRepository.findAll() else walletRepository.findAllByNameLike(name)

        return savedWallets.map {
            Wallet.from(
                it,
                getAccountInfo(PublicKey.builder().from {
                    UnsignedByteArray.of(it.publicKey)
                }.build()
                    .deriveAddress())
                    .accountData()
            )
        }.also { log.info("Returning ${it.size} wallets. Search query was $name") }
    }

    fun getWallet(walletId: UUID): Wallet {
        val savedWallet = walletRepository.findById(walletId)
            .orElseThrow { IllegalArgumentException("Wallet with id $walletId not found") }
        val accountInfo = getAccountInfo(PublicKey.builder().from {
            UnsignedByteArray.of(savedWallet.publicKey)
        }.build().deriveAddress())

        return Wallet.from(savedWallet, accountInfo.accountData())
    }

    private fun getAccountInfo(address: Address): AccountInfoResult {
        return xrplClient.accountInfo(
            AccountInfoRequestParams.of(address)
        )
    }
}