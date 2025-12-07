package com.example.accountingapp

import android.R

class TransactionItem(
    var id: Int,
    var fromUser: String,
    var content: String,
    var time: String,
    var transactionType: Boolean, //true là thu, false là chi
    var amount: String
)