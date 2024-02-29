package com.example.expensetracker.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface ExpenseDao {
    @Insert
    suspend fun addExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun findAllExpenses(): List<Expense>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()


    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    suspend fun findCategoryExpenses(category: String): List<Expense>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun findExpenseById(id: Long): Expense?

}