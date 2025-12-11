package com.example.accountingapp.Database

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
class TransactionViewModel(private val repo: TransactionRepo): ViewModel() {

    fun getTransactionByDate(start: Long, end: Long): LiveData<List<TransactionEntity>> {
        return repo.getTransactionByDate(start, end).asLiveData()
    }

    fun insert(transaction: TransactionEntity) = viewModelScope.launch {
        repo.insert(transaction)
    }
}

class TransactionViewModelFactory(private val repository: TransactionRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
