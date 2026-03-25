package com.gesturex.ui.record

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.gesturex.databinding.ActivityRecordGestureBinding
import com.gesturex.ui.edit.EditGestureActivity
import com.gesturex.util.GestureRecorder
import com.google.gson.Gson

class RecordGestureActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var b: ActivityRecordGestureBinding
    private lateinit var sm: SensorManager
    private var acelerometro: Sensor? = null
    private var giroscopio: Sensor? = null
    private val recorder = GestureRecorder()
    private var gravando = false

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityRecordGestureBinding.inflate(layoutInflater)
        setContentView(b.root)
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        giroscopio = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (acelerometro == null) {
            b.textStatus.text = "Acelerômetro não disponível"; b.btnGravar.isEnabled = false
        }

        b.btnVoltar.setOnClickListener { finish() }
        b.btnGravar.setOnClickListener { iniciarGravacao() }
    }

    override fun onResume() {
        super.onResume()
        acelerometro?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        giroscopio?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    override fun onPause() { super.onPause(); sm.unregisterListener(this) }

    override fun onSensorChanged(e: SensorEvent?) {
        e ?: return
        when (e.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = e.values[0]; val y = e.values[1]; val z = e.values[2]
                b.textEixoX.text = "X: ${"%.1f".format(x)}"
                b.textEixoY.text = "Y: ${"%.1f".format(y)}"
                b.textEixoZ.text = "Z: ${"%.1f".format(z)}"
                b.progressX.progress = norm(x)
                b.progressY.progress = norm(y)
                b.progressZ.progress = norm(z)
                if (gravando) recorder.adicionar(x, y, z)
            }
            Sensor.TYPE_GYROSCOPE -> {
                if (gravando) recorder.adicionarGyro(e.values[0], e.values[1], e.values[2])
            }
        }
    }

    override fun onAccuracyChanged(s: Sensor?, a: Int) {}

    private fun norm(v: Float) = ((v + 20f) / 40f * 100f).toInt().coerceIn(0, 100)

    private fun iniciarGravacao() {
        gravando = true; recorder.iniciar()
        b.btnGravar.isEnabled = false
        object : CountDownTimer(2500, 100) {
            override fun onTick(ms: Long) { b.textStatus.text = "Gravando… ${"%.1f".format(ms/1000.0)}s" }
            override fun onFinish() {
                gravando = false
                val dados = recorder.finalizar()
                val tipo = recorder.detectarTipo(dados)
                val json = Gson().toJson(dados)
                startActivity(Intent(this@RecordGestureActivity, EditGestureActivity::class.java)
                    .putExtra("tipo_gesto", tipo)
                    .putExtra("dados_sensor", json))
                finish()
            }
        }.start()
    }
}
