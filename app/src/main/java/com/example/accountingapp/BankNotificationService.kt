package com.example.accountingapp

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.accountingapp.Database.AppDatabase
import com.example.accountingapp.Database.TransactionEntity
import com.example.accountingapp.Database.TransactionRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BankNotificationService: NotificationListenerService() {
    private val repo by lazy {
        val db = AppDatabase.getDatabase(this)
        TransactionRepo(db.transactionDao())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.d("DEBUG", "onNotificationPosted")
        //if (!Helper.isBankNotification(sbn)) return
        val (title, text) = Helper.extractNotificationText(sbn)
        val amount = Helper.parseAmount(text)

        val newTransaction = TransactionEntity(
            fromUser = title,
            content = text,
            time = System.currentTimeMillis(),
            amount = amount.toDouble()
        )
        //Do phải được gọi bằng hàm suspend nhưng ở đây k có nên phải thông qua CoroutineScope
        CoroutineScope(Dispatchers.IO).launch{
            repo.insert(newTransaction)
            Log.d("DEBUG", "Inserted transaction time: ${System.currentTimeMillis()}")
        }
    }
}