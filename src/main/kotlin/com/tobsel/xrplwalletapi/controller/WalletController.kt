package com.tobsel.xrplwalletapi.controller

import com.tobsel.xrplwalletapi.controller.model.TransferInitiationDto
import com.tobsel.xrplwalletapi.controller.model.WalletDto
import com.tobsel.xrplwalletapi.controller.model.WalletCreationDto
import com.tobsel.xrplwalletapi.service.WalletService
import java.util.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wallets")
class WalletController(private val walletService: WalletService) {

    @PostMapping
    fun createWallet(@RequestBody walletCreation: WalletCreationDto): WalletDto {
        val wallet = walletService.createWallet(walletCreation.name)
        return WalletDto.from(wallet)
    }

    @GetMapping
    fun getWallets(@RequestParam name: String?): List<WalletDto> {
        return walletService.getWallets(name).map(WalletDto::from)
    }

    @GetMapping("/{walletId}")
    fun getWallet(@PathVariable walletId: UUID): WalletDto {
        return WalletDto.from(walletService.getWallet(walletId))
    }

    @PostMapping("/{walletId}/transfers")
    fun sendXrp(@PathVariable walletId: UUID, @RequestBody transferInitiation: TransferInitiationDto) {
        walletService.transferXrp(
            walletId,
            transferInitiation.destination,
            transferInitiation.xrpDropsAmount
        )
    }
}