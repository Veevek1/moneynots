package com.example.moneynots.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneynots.data.ExpenseDatabase
import com.example.moneynots.data.ExpenseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ExpenseDatabase.getDatabase(application).expenseDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val expenses: StateFlow<List<ExpenseEntity>> = dao.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredExpenses: StateFlow<List<ExpenseEntity>> = combine(expenses, _searchQuery) { list, query ->
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            list
        } else {
            list.filter { expense ->
                expense.category.contains(normalizedQuery, ignoreCase = true) ||
                    expense.note.contains(normalizedQuery, ignoreCase = true) ||
                    expense.amount.toString().contains(normalizedQuery, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMonthTotal: StateFlow<Double> = expenses.map { expenseList ->
        expenseList.filter { isInCurrentMonth(it.createdAt) }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categoryTotals: StateFlow<List<Pair<String, Double>>> = expenses.map { expenseList ->
        expenseList.filter { isInCurrentMonth(it.createdAt) }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addExpense(amount: Double, category: String, note: String) {
        if (amount <= 0.0) return

        val expense = ExpenseEntity(
            amount = amount,
            category = category,
            note = note.trim(),
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            dao.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            dao.deleteExpense(expense)
        }
    }

    private fun isInCurrentMonth(timestamp: Long): Boolean {
        val current = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        return current.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            current.get(Calendar.MONTH) == target.get(Calendar.MONTH)
    }
}
