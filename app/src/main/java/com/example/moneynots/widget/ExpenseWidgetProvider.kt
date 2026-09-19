package com.example.moneynots.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.moneynots.MainActivity
import com.example.moneynots.R
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class ExpenseWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.expense_widget)
            val amount = 0.0
            views.setTextViewText(R.id.widget_total, formatInr(amount))

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun formatInr(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        formatter.currency = Currency.getInstance("INR")
        return formatter.format(amount)
    }
}
