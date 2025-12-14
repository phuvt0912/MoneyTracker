package com.example.accountingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(
    private val transactions:  MutableList<TransactionItem>
): RecyclerView.Adapter<Adapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userTextView: TextView = view.findViewById(R.id.userTextView)
        val contentTextView: TextView = view.findViewById(R.id.contentTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val amountTextView: TextView = view.findViewById(R.id.amountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.userTextView.text = transaction.fromUser
        holder.contentTextView.text = transaction.content
        holder.timeTextView.text = transaction.time
        holder.amountTextView.text = transaction.amount
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}