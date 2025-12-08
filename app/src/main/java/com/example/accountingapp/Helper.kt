package com.example.accountingapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.util.Log

object Helper {

    // =============================
    // 1. Kiểm tra quyền Noti Listener
    // =============================
    fun isNotificationServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val cn = ComponentName(context, serviceClass) //Tên class của service lăng nghe thông báo
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners") //Chuỗi chứa các service
        return flat != null && flat.contains(cn.flattenToString()) //Kiểm tra chuỗi có chứa tên service không
    }

    fun openNotificationAccessSettings(context: Context) {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        context.startActivity(intent)
    }


    // =============================
    // Kiểm tra thông báo có phải từ ngân hàng
    // =============================
    private val supportedBanks = setOf(
        "com.mbmobile", //MB Bank
        "com.vietcombank.mobileapp", // VietcomBank
        "com.techcombank.homebanking", //Techcombank
        "com.vietinbank.ipay", //VietinBank
        "com.bidv.smartbanking", //BIDV
        "com.acb.acbbanking", //ACB
        "com.tpbank.tpb", //TPBank
        "com.sacombank.ewallet" //Sacombank
    )

    fun isBankNotification(sbn: StatusBarNotification?): Boolean {
        //statusbarNotification đại diện cho 1 thông báo, nên nó sẽ chứa đầy đủ các thông tin của 1 thông báo, chẳng hạn như tên package
        val pkg = sbn?.packageName ?: return false
        return supportedBanks.contains(pkg)
    }


    // =============================
    // 3. Lấy title + text của thông báo
    // =============================
    fun extractNotificationText(sbn: StatusBarNotification?): Pair<String, String> {
        // Các dữ liệu text sẽ lưu trong 1 bundle và phải lấy thông qua .notification.extras
        //Lấy thông tin từ thông báo thông qua .extras (là 1 Bundle chứa các dự liệu text của thông báo) trả về dạng string to string
        val extras = sbn?.notification?.extras ?: return "" to ""
        val title = extras.getString("android.title") ?: ""
        val text = extras.getString("android.text") ?: ""
        return title to text
    }


    // =============================
    // 4. Parse số tiền
    // =============================
    fun parseAmount(text: String): Long {
        val regexAmount = Regex("""[+-]?\d{1,3}(?:,\d{3})*""")
        val match = regexAmount.find(text)
        val amountStr = match?.value ?: "0"
        return amountStr.replace(",", "").toLong()
    }

    // =============================
    // 5. Parse thu/chi
    // =============================
    fun parseTransactionType(text: String): String {
        return if (text.contains("-")) "expense" else "income"
    }
}
