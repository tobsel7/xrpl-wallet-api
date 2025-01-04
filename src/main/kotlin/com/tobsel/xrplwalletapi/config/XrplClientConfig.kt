package com.tobsel.xrplwalletapi.config

import com.tobsel.xrplwalletapi.config.properties.XrplClientProperties
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.xrpl.xrpl4j.client.XrplClient
import org.xrpl.xrpl4j.client.faucet.FaucetClient

@Configuration
class XrplClientConfig(xrplClientProperties: XrplClientProperties) {

    private val xrplUrl = xrplClientProperties.url.toHttpUrl()
    private val faucetUrl = xrplClientProperties.faucetUrl.toHttpUrl()

    @Bean
    fun xrplClient(xrplClientProperties: XrplClientProperties): XrplClient {
        return XrplClient(xrplUrl)
    }

    @Bean
    fun xrplFaucetClient(xrplClientProperties: XrplClientProperties): FaucetClient? {
        return FaucetClient.construct(faucetUrl)
    }
}