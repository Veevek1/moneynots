package com.example.moneynots.ui

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatInr(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    formatter.currency = Currency.getInstance("INR")
    return formatter.format(amount)
}

fun formatShortDate(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(java.util.Date(timestamp))
}

fun formatFullDate(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(java.util.Date(timestamp))
}
