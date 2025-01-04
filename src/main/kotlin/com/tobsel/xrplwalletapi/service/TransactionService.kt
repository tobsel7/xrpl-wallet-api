package com.tobsel.xrplwalletapi.service

import com.tobsel.xrplwalletapi.service.model.LedgerPayment
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xrpl.xrpl4j.client.XrplClient
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService
import org.xrpl.xrpl4j.model.client.accounts.AccountTransactionsTransactionResult
import org.xrpl.xrpl4j.model.client.fees.FeeUtils
import org.xrpl.xrpl4j.model.transactions.Address
import org.xrpl.xrpl4j.model.transactions.Payment
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount

private val log = LoggerFactory.getLogger(TransactionService::class.java)

@Service
class TransactionService(
    private val xrplClient: XrplClient,
    private val walletService: WalletService
) {

    private val signatureService = BcSignatureService()

    fun createPayment(walletId: UUID, destination: String, amount: String, currency: String) {
        val wallet = walletService.getWallet(walletId)

        val payment = Payment.builder()
            .account(wallet.publicKey.deriveAddress())
            .fee(FeeUtils.computeNetworkFees(xrplClient.fee()).recommendedFee())
            .sequence(wallet.ledgerAccountInfo.sequence())
            .destination(Address.builder().value(destination).build())
            .amount(determineAmount(amount, currency))
            .signingPublicKey(wallet.publicKey)
            .build()

        val signedTransaction: SingleSignedTransaction<Payment> = signatureService.sign(wallet.privateKey, payment)
        val transactionResult = xrplClient.submit(signedTransaction)

        if (transactionResult.accepted()) {
            log.info("XRP transaction was accepted by ledger. Transaction hash: ${transactionResult.transactionResult().hash()}")
        } else {
            throw RuntimeException("XRP transaction was not accepted by ledger. Error: ${transactionResult.engineResultMessage()}")
        }
    }

    private fun determineAmount(amount: String, currency: String) =
        if (currency == "XRP") {
            XrpCurrencyAmount.ofDrops((amount.toDouble() * 1000000).toLong())
        } else {
            throw IllegalArgumentException("Currency $currency is not supported")
        }

    fun getPayments(walletId: UUID): List<LedgerPayment> {
        val wallet = walletService.getWallet(walletId)
        val ledgerPayments = xrplClient.accountTransactions(wallet.publicKey.deriveAddress()).transactions()
            .filterIsInstance<AccountTransactionsTransactionResult<Payment>>()

        return ledgerPayments.map(LedgerPayment::from)
    }
}