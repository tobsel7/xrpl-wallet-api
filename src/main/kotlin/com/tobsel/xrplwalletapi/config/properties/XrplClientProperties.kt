package com.tobsel.xrplwalletapi.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "xrpl")
data class XrplClientProperties(
    val url: String,
    val faucetUrl: String
)