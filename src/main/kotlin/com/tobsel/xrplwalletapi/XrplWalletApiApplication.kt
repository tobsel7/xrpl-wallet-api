package com.tobsel.xrplwalletapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class XrplWalletApiApplication

fun main(args: Array<String>) {
    runApplication<XrplWalletApiApplication>(*args)
}
