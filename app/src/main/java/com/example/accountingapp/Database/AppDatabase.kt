package com.example.accountingapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "MoneyTracker" // Tên file cơ sở dữ liệu sẽ được tạo
                )
                    // .fallbackToDestructiveMigration() // Tùy chọn: Xóa và tạo lại DB nếu phiên bản thay đổi. Hữu ích khi phát triển.
                    .build()
                INSTANCE = instance
                // trả về instance
                instance
            }
        }
    }
}