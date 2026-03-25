package com.gesturex.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.gesturex.R
import com.gesturex.data.db.GestureDatabase
import com.gesturex.ui.main.MainActivity
import com.gesturex.util.ActionDispatcher
import com.gesturex.util.GestureDetectorUtil
import kotlinx.coroutines.*

class GestureService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val detector = GestureDetectorUtil()
    private lateinit var dispatcher: ActionDispatcher
    private var gestosAtivos = listOf<com.gesturex.data.model.Gesture>()

    companion object {
        const val CHANNEL_ID = "gesturex_channel"
        const val NOTIF_ID = 1
        const val PREF_SENSIBILIDADE = "sensibilidade"

        fun start(ctx: Context) {
            val i = Intent(ctx, GestureService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ctx.startForegroundService(i)
            else ctx.startService(i)
        }

        fun stop(ctx: Context) = ctx.stopService(Intent(ctx, GestureService::class.java))
    }

    override fun onCreate() {
        super.onCreate()
        dispatcher = ActionDispatcher(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        criarCanal()
        startForeground(NOTIF_ID, buildNotif())
        val prefs = getSharedPreferences("gesturex_prefs", Context.MODE_PRIVATE)
        detector.setSensibilidade(prefs.getString(PREF_SENSIBILIDADE, "media") ?: "media")
        recarregarGestos()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        acelerometro?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        val gesto = detector.processar(event.values[0], event.values[1], event.values[2], gestosAtivos)
        gesto?.let { scope.launch(Dispatchers.Main) { dispatcher.executar(it) } }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    fun recarregarGestos() {
        scope.launch {
            gestosAtivos = GestureDatabase.getInstance(applicationContext).gestureDao().getAtivos()
        }
    }

    private fun criarCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
    }

    private fun buildNotif(): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_gesture)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }
}
