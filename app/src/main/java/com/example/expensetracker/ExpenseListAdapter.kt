package com.example.expensetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.database.Expense
import com.example.expensetracker.databinding.ListItemExpenseBinding

class ExpenseViewHolder (
    private val binding: ListItemExpenseBinding,
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(expense: Expense) {
        binding.expenseTitle.text = expense.title
        binding.expenseDate.text = expense.date.toString()
        binding.expenseCategory.text = expense.category
        binding.expenseAmount.text = expense.amount.toString()

        binding.root.setOnClickListener {
            onItemClick(expense)
        }
    }
}

class ExpenseListAdapter(
    private var expenses: List<Expense>,
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseViewHolder>() {


    fun getExpenseList(expenseNewList: List<Expense>){
        expenses = expenseNewList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemExpenseBinding.inflate(inflater, parent, false)
        return ExpenseViewHolder(binding) { expense ->
            onItemClick(expense)
        }
    }
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int {
        return expenses.size
    }
}

