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
    fun getBankName(packageName: String): String {
        return when (packageName) {
            "com.mbmobile" -> "MB Bank"
            "com.vietcombank.mobileapp" -> "Vietcombank"
            "com.techcombank.homebanking" -> "Techcombank"
            "com.vietinbank.ipay" -> "VietinBank"
            "com.bidv.smartbanking" -> "BIDV"
            "com.acb.acbbanking" -> "ACB"
            "com.tpbank.tpb" -> "TPBank"
            "com.sacombank.ewallet" -> "Sacombank"
            else -> "Unknown Bank"
        }
    }
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
        val clean = text.replace(" ", "").lowercase()

        // Regex bắt: GD:+5000VND hoặc GD:-100000 hoặc GD:+1,000,000
        val regex = Regex("""gd:([+-]?\d[\d.,]*)""")

        val match = regex.find(clean) ?: return 0L

        // Lấy phần sau "GD:"
        val rawAmount = match.groupValues[1]

        // Loại bỏ chữ và dấu
        val normalized = rawAmount
            .replace("[^0-9+-]".toRegex(), "")   // giữ lại chỉ số và dấu + -
            .replace("[.,]".toRegex(), "")        // bỏ dấu chấm/phẩy

        return normalized.toLongOrNull() ?: 0L
    }

    // =============================
    // 5. Parse thu/chi
    // =============================
    fun parseTransactionType(text: String): String {
        return if (text.contains("-")) "expense" else "income"
    }

    fun toggleNotificationListenerService(context: Context) {
        try {
            val componentName = ComponentName(context, BankNotificationService::class.java)
            val packageManager = context.packageManager

            // Tắt Service
            packageManager.setComponentEnabledSetting(
                componentName,
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                android.content.pm.PackageManager.DONT_KILL_APP
            )
            // Sau đó bật lại Service
            packageManager.setComponentEnabledSetting(
                componentName,
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                android.content.pm.PackageManager.DONT_KILL_APP
            )
            Log.d("DEBUG", "Notification Service toggled successfully.")
        } catch (e: Exception) {
            Log.e("DEBUG", "Error toggling service: ${e.message}")
        }
    }

    fun parseTransactionNote(text: String): String {
        val clean = text.replace("\n", " ")

        // 1. MB Bank: ND:XXXX
        val mbRegex = Regex("""ND:([^|]+)""", RegexOption.IGNORE_CASE)
        mbRegex.find(clean)?.let {
            return it.groupValues[1].trim()
        }

        // 2. VietcomBank: thường có "Noi dung: xxx"
        val vcbRegex = Regex("""(nội dung|noi dung)[: ]+(.+)""", RegexOption.IGNORE_CASE)
        vcbRegex.find(clean)?.let {
            return it.groupValues[2].trim()
        }

        // 3. Techcombank thường có "Mo ta: xxx"
        val tcbRegex = Regex("""(mô tả|mo ta)[: ]+(.+)""", RegexOption.IGNORE_CASE)
        tcbRegex.find(clean)?.let {
            return it.groupValues[2].trim()
        }

        // 4. VietinBank / BIDV có dạng "... ND ABCXYZ"
        val ndLooseRegex = Regex("""ND[ :]+([^|]+)""", RegexOption.IGNORE_CASE)
        ndLooseRegex.find(clean)?.let {
            return it.groupValues[1].trim()
        }

        // 5. fallback – không tìm thấy
        return ""
    }
}
