package com.tobsel.xrplwalletapi.repository

import com.tobsel.xrplwalletapi.repository.model.WalletEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : JpaRepository<WalletEntity, UUID> {

    @Query("SELECT w FROM WalletEntity w WHERE w.name LIKE %:name%")
    fun findAllByNameLike(name: String): List<WalletEntity>
}