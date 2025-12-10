package com.example.accountingapp

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class BankNotificationService: NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (!Helper.isBankNotification(sbn)) return

        //Xử lý khi nhận được thông báo
    }
}