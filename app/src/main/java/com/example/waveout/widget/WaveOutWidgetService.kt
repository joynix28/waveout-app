package com.example.waveout.widget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.waveout.R
import com.example.waveout.audio.AudioEngine
import com.example.waveout.audio.CleaningMode
import com.example.waveout.audio.VibrationEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WaveOutWidgetService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var cleanJob: Job? = null
    private lateinit var audioEngine: AudioEngine
    private lateinit var vibrationEngine: VibrationEngine
    
    override fun onCreate() {
        super.onCreate()
        audioEngine = AudioEngine.getInstance(applicationContext)
        vibrationEngine = VibrationEngine.getInstance(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            WaveOutWidget.ACTION_WATER_EJECT -> {
                startCleaning(CleaningMode.WATER_EJECT, 30)
            }
            WaveOutWidget.ACTION_DUST_CLEAN -> {
                startCleaning(CleaningMode.DUST_CLEAN, 20)
            }
            WaveOutWidget.ACTION_STOP -> {
                stopCleaning()
            }
        }
        return START_NOT_STICKY
    }

    private fun startCleaning(mode: CleaningMode, durationSeconds: Int) {
        startForegroundNotification(mode.displayName)
        updateWidgets(true)
        
        vibrationEngine.vibratePattern(mode)
        when (mode) {
            CleaningMode.WATER_EJECT -> audioEngine.startTone(165f)
            CleaningMode.DUST_CLEAN -> audioEngine.startSweep(300f, 800f, durationSeconds * 1000L)
            else -> audioEngine.startTone(165f)
        }

        cleanJob?.cancel()
        cleanJob = serviceScope.launch {
            delay(durationSeconds * 1000L)
            stopCleaning()
        }
    }

    private fun stopCleaning() {
        cleanJob?.cancel()
        audioEngine.stopTone()
        vibrationEngine.stop()
        
        updateWidgets(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun updateWidgets(isPlaying: Boolean) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, WaveOutWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        for (appWidgetId in appWidgetIds) {
            WaveOutWidget.updateWidget(this, appWidgetManager, appWidgetId, isPlaying)
        }
    }

    private fun startForegroundNotification(mode: String) {
        val channelId = "waveout_widget_channel"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "WaveOut Cleaning",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, WaveOutWidgetService::class.java).apply {
            action = WaveOutWidget.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 3, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationLayout = RemoteViews(packageName, R.layout.waveout_notification)
        notificationLayout.setTextViewText(R.id.tv_notif_title, "WaveOut — $mode")
        notificationLayout.setTextViewText(R.id.tv_notif_status, "Nettoyage en cours...")
        notificationLayout.setOnClickPendingIntent(R.id.btn_notif_stop, stopPendingIntent)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setCustomContentView(notificationLayout)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cleanJob?.cancel()
        audioEngine.stopTone()
        vibrationEngine.stop()
    }
}
