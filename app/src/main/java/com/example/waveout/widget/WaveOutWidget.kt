package com.example.waveout.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.waveout.R

class WaveOutWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, false)
        }
    }

    companion object {
        const val ACTION_WATER_EJECT = "io.github.joynix28.waveout.WIDGET_WATER_EJECT"
        const val ACTION_DUST_CLEAN = "io.github.joynix28.waveout.WIDGET_DUST_CLEAN"
        const val ACTION_STOP = "io.github.joynix28.waveout.WIDGET_STOP"
        
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            isPlaying: Boolean
        ) {
            val views = RemoteViews(context.packageName, R.layout.waveout_widget_layout)

            val waterEjectIntent = Intent(context, WaveOutWidgetService::class.java).apply {
                action = ACTION_WATER_EJECT
            }
            val waterEjectPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    context, 0, waterEjectIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getService(
                    context, 0, waterEjectIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
            views.setOnClickPendingIntent(R.id.btn_water_eject, waterEjectPendingIntent)

            val dustCleanIntent = Intent(context, WaveOutWidgetService::class.java).apply {
                action = ACTION_DUST_CLEAN
            }
            val dustCleanPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    context, 1, dustCleanIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getService(
                    context, 1, dustCleanIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
            views.setOnClickPendingIntent(R.id.btn_dust_clean, dustCleanPendingIntent)

            val stopIntent = Intent(context, WaveOutWidgetService::class.java).apply {
                action = ACTION_STOP
            }
            val stopPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    context, 2, stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getService(
                    context, 2, stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
            views.setOnClickPendingIntent(R.id.btn_stop, stopPendingIntent)

            if (isPlaying) {
                views.setTextViewText(R.id.tv_status, "Nettoyage en cours...")
            } else {
                views.setTextViewText(R.id.tv_status, "Touchez pour nettoyer le HP")
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
