package com.example.accountingapp.Database

import kotlinx.coroutines.flow.Flow

class TransactionRepo(private val transactionDao: TransactionDAO) {

    fun getTransactionByDate(timestamp: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionByDate(timestamp)
    }

    suspend fun insert(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }
}
