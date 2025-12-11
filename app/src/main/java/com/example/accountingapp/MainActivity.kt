package com.example.accountingapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accountingapp.Database.AppDatabase
import com.example.accountingapp.Database.TransactionRepo
import com.example.accountingapp.Database.TransactionViewModel
import com.example.accountingapp.Database.TransactionViewModelFactory
import androidx.activity.viewModels
import com.example.accountingapp.Database.TransactionEntity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    var transactions: MutableList<TransactionItem> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter
    private lateinit var dateTextView: TextView
    //Tạo model view
    private val transactionviewmodel: TransactionViewModel by viewModels {
        //Tạo db, tạo DAO, tạo VMFactory
        val db: AppDatabase = AppDatabase.getDatabase(this)
        val repo = TransactionRepo(db.transactionDao())
        TransactionViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        dateTextView = findViewById<TextView>(R.id.date)
        recyclerView = findViewById(R.id.transactions_recyclerview)

        val datestr: String = dateTextView.text.toString()
        loadTransactions(transactions, datestr)

        adapter = Adapter(transactions)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        //Load data từ cơ sở dữ liệu

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun getStartAndEnd(dateStr: String): Pair<Long, Long> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDate = LocalDate.parse(dateStr, formatter)

        val zone = ZoneId.systemDefault()

        val start = localDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = localDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        return Pair(start, end)
    }

    private fun loadTransactions(transactions: MutableList<TransactionItem>, dateStr: String) {
        val (start, end) = getStartAndEnd(dateStr)
        //Load từ db vào transaction
        transactionviewmodel.getTransactionByDate(start, end).observe(this) { TransactionDatas ->
            transactions.clear()
            for (entity in TransactionDatas) {
                val newItem = TransactionItem(
                    entity.id,
                    entity.fromUser,
                    entity.content,
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entity.time),
                    entity.amount.toString()
                )
                transactions.add(newItem)
            }
            adapter.notifyDataSetChanged()
        }
    }
}