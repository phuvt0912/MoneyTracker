package com.example.accountingapp

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.accountingapp.Database.AppDatabase
import com.example.accountingapp.Database.TransactionEntity
import com.example.accountingapp.Database.TransactionRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent


private const val NOTIFICATION_ID = 101
private const val CHANNEL_ID = "BankTrackingChannel"
private const val CHANNEL_NAME = "Theo dõi giao dịch ngân hàng"

private val handledKeys = mutableSetOf<String>()

class BankNotificationService: NotificationListenerService() {

    // 1. Quản lý CoroutineScope
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob) // Dùng Dispatchers.IO cho Database

    // ... (Phần repo giữ nguyên)
    private val repo by lazy {
        val db = AppDatabase.getDatabase(this)
        TransactionRepo(db.transactionDao())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        // 2. Kiểm tra Null và Bank App
        sbn?.let { notification ->
            if (!Helper.isBankNotification(notification)){
                Log.d("DEBUG", "Not a bank")
                return
            }

            // 3. Thực hiện Parsing và Database Insert
            val (title, text) = Helper.extractNotificationText(sbn)
            val amount = Helper.parseAmount(text)
            val content = Helper.parseTransactionNote(text)

            if (amount <= 0 || content.isBlank()) return
            if (handledKeys.contains(sbn.key)) return
            handledKeys.add(sbn.key)

            if (handledKeys.size > 100) handledKeys.clear()

            val bank = Helper.getBankName(sbn.packageName.toString())
            val newTransaction = TransactionEntity(
                fromUser = bank,
                // Gán nội dung thông báo đầy đủ vào đây để dễ debug
                content = content,
                time = System.currentTimeMillis(),
                amount = amount.toDouble()
            )

            serviceScope.launch{
                try {
                    repo.insert(newTransaction)
                    Log.d("DEBUG", "Inserted transaction successfully")
                } catch (e: Exception) {
                    Log.e("DEBUG", "Error inserting transaction: ${e.message}")
                }
            }
        }
    }

    // 4. Hủy Scope khi Service bị hủy
    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
    override fun onCreate() {
        super.onCreate()
        // 1. Tạo Kênh thông báo (Bắt buộc từ Android O trở lên)
        createNotificationChannel()

        // 2. Xây dựng Thông báo cho Foreground
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Money Tracker đang hoạt động")
            .setContentText("Đang theo dõi thông báo ngân hàng để ghi lại giao dịch.")
            // Yêu cầu một icon nhỏ (small icon)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Thay thế bằng icon thực tế của bạn
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // 3. Khởi chạy Foreground Service
        startForeground(NOTIFICATION_ID, notification)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Rất quan trọng: Yêu cầu hệ thống cố gắng khởi động lại Service
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}