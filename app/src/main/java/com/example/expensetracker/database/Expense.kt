package com.example.expensetracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var title: String,
    val amount: Double,
    val category: String,
    val date: Date
)
