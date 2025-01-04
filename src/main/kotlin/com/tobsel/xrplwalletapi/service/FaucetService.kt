package com.tobsel.xrplwalletapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xrpl.xrpl4j.client.faucet.FaucetClient
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest
import org.xrpl.xrpl4j.model.transactions.Address

private val log = LoggerFactory.getLogger(FaucetService::class.java)

@Service
class FaucetService(private val faucetClient: FaucetClient) {

    fun fundWallet(address: Address) {
        val walletFundRequest = FundAccountRequest.of(address)
        log.info("Funding wallet with address: $address")
        faucetClient.fundAccount(walletFundRequest)
    }
}