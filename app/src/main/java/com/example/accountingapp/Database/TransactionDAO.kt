package com.example.accountingapp.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE time BETWEEN :start AND :end ORDER BY time DESC")
    fun getTransactionByDate(start: Long, end: Long): Flow<List<TransactionEntity>>
}