package com.tobsel.xrplwalletapi.controller.model

data class TransferInitiationDto(
    val destination: String,
    val amount: AmountDto
)
