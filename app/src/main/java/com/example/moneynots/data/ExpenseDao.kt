package com.example.moneynots.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE createdAt >= :startOfMonth AND createdAt <= :endOfMonth")
    fun getMonthlyTotal(startOfMonth: Long, endOfMonth: Long): Flow<Double>
}
