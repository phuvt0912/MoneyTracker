package com.example.accountingapp.Database

import kotlinx.coroutines.flow.Flow

class TransactionRepo(private val transactionDao: TransactionDAO) {

    fun getTransactionByDate(start: Long, end: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionByDate(start, end)
    }

    suspend fun insert(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }
}
