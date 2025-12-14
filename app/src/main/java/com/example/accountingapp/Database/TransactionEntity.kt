package com.example.accountingapp.Database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fromUser: String,
    val content: String,
    val time: Long,
    val amount: Double
)