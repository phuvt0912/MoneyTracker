package com.example.accountingapp.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE time = :timestamp ORDER BY time DESC")
    fun getTransactionByDate(timestamp: Long): Flow<List<TransactionEntity>>
}