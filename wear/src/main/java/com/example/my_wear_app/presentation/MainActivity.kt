package com.example.my_wear_app

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout
import android.view.Gravity
import android.os.PowerManager
import android.view.WindowManager
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var textView: TextView
    private lateinit var wakeLock: PowerManager.WakeLock // Fixes the "punch bug"
    private lateinit var vibrator: Vibrator
    private var startTime: Long = 0
    private var isTracking = false
    private val THRESHOLD_X = 7f
    private val DURATION_THRESHOLD = 3000 // 3 seconds in milliseconds
    private lateinit var yesButton: Button
    private lateinit var noButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Set background color to black
        window.decorView.setBackgroundColor(android.graphics.Color.BLACK)

        // Initialize wake lock
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MyWearApp::AccelerometerWakeLock"
        )
        wakeLock.acquire()

        // Create a simple TextView to show accelerometer data
        textView = TextView(this).apply {
            text = "Waiting for movement..."
            gravity = Gravity.CENTER
            setTextColor(android.graphics.Color.WHITE) // Set text color to white
        }

        // Create buttons for user response
        yesButton = Button(this).apply {
            text = "✔"
            visibility = Button.GONE
            setOnClickListener {
                textView.text = "Nail bite detected!"
                // Handle nail bite detection
                this.visibility = Button.GONE
                noButton.visibility = Button.GONE
            }
        }

        noButton = Button(this).apply {
            text = "✘"
            visibility = Button.GONE
            setOnClickListener {
                textView.text = "False alarm!"
                // Handle false alarm
                this.visibility = Button.GONE
                yesButton.visibility = Button.GONE
            }
        }

        // Create a horizontal layout for the buttons
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            addView(yesButton)
            addView(noButton)
        }

        // Create a circular layout to hold the buttons
        val circularLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = resources.getDrawable(R.drawable.circular_background, null)
            addView(buttonLayout)
        }

        // Create a layout to hold the TextView and circular button layout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(textView)
            addView(circularLayout)
        }

        setContentView(layout)

        // Initialize sensor manager and accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            textView.text = "No accelerometer found!"
        }

        // Initialize vibrator
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator = vibratorManager.defaultVibrator
    }

    override fun onResume() {
        super.onResume()
        if (!wakeLock.isHeld) {
            wakeLock.acquire()
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            textView.text = "X: $x\nY: $y\nZ: $z"
            Log.d("WearApp", "X: $x, Y: $y, Z: $z")

            if (x > THRESHOLD_X) {
                if (!isTracking) {
                    startTime = System.currentTimeMillis()
                    isTracking = true
                } else {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    if (elapsedTime >= DURATION_THRESHOLD) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                        isTracking = false // Reset tracking after vibration
                        textView.text = "Did you bite your nail?"
                        yesButton.visibility = Button.VISIBLE
                        noButton.visibility = Button.VISIBLE
                    }
                }
            } else {
                isTracking = false // Reset tracking when X goes below threshold
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this example
    }
}