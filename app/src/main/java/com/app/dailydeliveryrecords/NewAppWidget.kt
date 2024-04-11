package com.app.dailydeliveryrecords

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews


/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    companion object{
        const val ACTION_TEXT_CHANGED = "com.app.dailydeliveryrecords.ACTION_TEXT_CHANGED"
        var s = ""
    }

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TEXT_CHANGED) {
            // handle intent here
            context?.let {
                s = intent.getStringExtra("NewString") ?: ""
                val ids: IntArray = AppWidgetManager.getInstance(it)
                    .getAppWidgetIds(ComponentName(it, NewAppWidget::class.java))
                onUpdate(it,AppWidgetManager.getInstance(it),ids)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.app_full_name) + " Widget"
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    views.setTextViewText(R.id.appwidget_text, NewAppWidget.s.ifEmpty { widgetText })

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}