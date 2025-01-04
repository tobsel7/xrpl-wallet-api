package com.tobsel.xrplwalletapi.service

import com.tobsel.xrplwalletapi.repository.WalletRepository
import com.tobsel.xrplwalletapi.repository.model.WalletEntity
import com.tobsel.xrplwalletapi.service.model.Wallet
import java.util.*
import org.springframework.stereotype.Service
import org.xrpl.xrpl4j.client.XrplClient
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray
import org.xrpl.xrpl4j.crypto.keys.PublicKey
import org.xrpl.xrpl4j.crypto.keys.Seed
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult
import org.xrpl.xrpl4j.model.client.fees.FeeUtils
import org.xrpl.xrpl4j.model.transactions.Address
import org.xrpl.xrpl4j.model.transactions.Payment
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount


@Service
class WalletService(
    private val walletRepository: WalletRepository,
    private val xrplClient: XrplClient,
    private val faucetService: FaucetService
) {

    private val signatureService = BcSignatureService()

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
        }
    }

    fun getWallet(walletId: UUID): Wallet {
        val savedWallet = walletRepository.findById(walletId)
            .orElseThrow { IllegalArgumentException("Wallet with id $walletId not found") }
        val accountInfo = getAccountInfo(PublicKey.builder().from {
            UnsignedByteArray.of(savedWallet.publicKey)
        }.build().deriveAddress())

        return Wallet.from(savedWallet, accountInfo.accountData())
    }

    fun transferXrp(walletId: UUID, destination: String, xrpDropsAmount: Long) {
        val wallet = getWallet(walletId)

        val payment = Payment.builder()
            .account(wallet.publicKey.deriveAddress())
            .fee(FeeUtils.computeNetworkFees(xrplClient.fee()).recommendedFee())
            .sequence(wallet.ledgerAccountInfo.sequence())
            .destination(Address.builder().value(destination).build())
            .amount(XrpCurrencyAmount.ofDrops(xrpDropsAmount))
            .signingPublicKey(wallet.publicKey)
            .build()

        val signedTransaction: SingleSignedTransaction<Payment> = signatureService.sign(wallet.privateKey, payment)
        xrplClient.submit(signedTransaction)
    }

    private fun getAccountInfo(address: Address): AccountInfoResult {
        return xrplClient.accountInfo(
            AccountInfoRequestParams.of(address)
        )
    }


}