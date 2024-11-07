package com.example.my_wear_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class MainActivity : ComponentActivity() {
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
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database helper
        dbHelper = DBHelper(this)

        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initialize wake lock
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MyWearApp::AccelerometerWakeLock"
        )
        wakeLock.acquire()

        // Create a simple TextView to show the main message
        val mainTextView = TextView(this).apply {
            text = "Don't bite your nails."
            gravity = Gravity.CENTER
            textSize = 14f // Smaller font size
            setPadding(0, 0, 0, 20) // Add some margin below the text
        }

        // Create a smaller, circular button with a text symbol to access the detailed accelerometer data screen
        val detailButton = Button(this).apply {
            text = ">"
            setBackgroundResource(R.drawable.circular_button) // Set background to circular drawable
            setTextColor(android.graphics.Color.WHITE) // White text color
            setOnClickListener {
                showDetailScreen()
            }
            // Make the button small and adjust margin
            layoutParams = LinearLayout.LayoutParams(
                100, // Width
                100  // Height
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setMargins(0, 0, 0, 40) // Add margin at the bottom
            }
        }

        // Create a layout to hold the TextView and button
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(android.graphics.Color.BLACK) // Set background color to black
            addView(mainTextView)
            addView(detailButton)
        }

        setContentView(layout)
    }

    private fun showDetailScreen() {
        // Create a new layout for the detailed screen
        val detailLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(android.graphics.Color.BLACK) // Set background color to black
        }

        // Create a simple TextView to show accelerometer data
        textView = TextView(this).apply {
            text = "Waiting for movement..."
            gravity = Gravity.CENTER
        }

        // Create a button to go back to the main view with a text symbol
        val backButton = Button(this).apply {
            text = "<"
            setBackgroundResource(R.drawable.circular_button) // Set background to circular drawable
            setTextColor(android.graphics.Color.WHITE) // White text color
            setOnClickListener {
                showMainScreen()
            }
            // Make the button small and adjust margin
            layoutParams = LinearLayout.LayoutParams(
                100, // Width
                100  // Height
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setMargins(0, 0, 0, 40) // Add margin at the bottom
            }
        }

        // Create a layout to hold the TextView and button
        val detailLayoutContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(android.graphics.Color.BLACK) // Set background color to black
            addView(textView)
            addView(backButton)
        }

        setContentView(detailLayoutContainer)

        // Initialize sensor manager and accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            textView.text = "No accelerometer found!"
        }

        // Initialize vibrator
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator = vibratorManager.defaultVibrator

        // Register sensor listener
        accelerometer?.let {
            sensorManager.registerListener(object : SensorEventListener {
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
                                    showConfirmationScreen()
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
            }, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun showConfirmationScreen() {
        // Create a new layout for the confirmation screen
        val confirmationLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(android.graphics.Color.BLACK) // Set background color to black
        }

        // Create a TextView to ask the question
        val questionTextView = TextView(this).apply {
            text = "Did you bite your nails?"
            gravity = Gravity.CENTER
            textSize = 16f
            setPadding(0, 20, 0, 20) // Add some margin above and below the text
        }

        // Create a horizontal layout for the buttons
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        // Create buttons for user response
        yesButton = Button(this).apply {
            text = "✔"
            setBackgroundResource(R.drawable.circular_button) // Set background to circular drawable
            setTextColor(android.graphics.Color.WHITE) // White text color
            setOnClickListener {
                logResponse("Yes")
                showMainScreen()
            }
            // Make the button small and adjust margin
            layoutParams = LinearLayout.LayoutParams(
                100, // Width
                100  // Height
            ).apply {
                gravity = Gravity.CENTER
                setMargins(20, 0, 20, 0) // Add some margin between the buttons
            }
        }

        noButton = Button(this).apply {
            text = "✘"
            setBackgroundResource(R.drawable.circular_button) // Set background to circular drawable
            setTextColor(android.graphics.Color.WHITE) // White text color
            setOnClickListener {
                logResponse("No")
                showMainScreen()
            }
            // Make the button small and adjust margin
            layoutParams = LinearLayout.LayoutParams(
                100, // Width
                100  // Height
            ).apply {
                gravity = Gravity.CENTER
                setMargins(20, 0, 20, 0) // Add some margin between the buttons
            }
        }

        // Add buttons to the horizontal layout
        buttonLayout.apply {
            addView(yesButton)
            addView(noButton)
        }

        // Add views to the confirmation layout
        confirmationLayout.apply {
            addView(questionTextView)
            addView(buttonLayout)
        }

        setContentView(confirmationLayout)
    }

    private fun logResponse(response: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_TIMESTAMP, System.currentTimeMillis())
            put(DBHelper.COLUMN_RESPONSE, response)
        }
        db.insert(DBHelper.TABLE_NAME, null, values)
        queryDatabase() // Query the database and log the results
    }

    private fun queryDatabase() {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_ID, DBHelper.COLUMN_TIMESTAMP, DBHelper.COLUMN_RESPONSE),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIMESTAMP))
                val response = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_RESPONSE))
                Log.d("DBHelper", "ID: $id, Timestamp: $timestamp, Response: $response")
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun showMainScreen() {
        // Create a simple TextView to show the main message
        val mainTextView = TextView(this).apply {
            text = "Don't bite your nails."
            gravity = Gravity.CENTER
            textSize = 14f // Smaller font size
            setPadding(0, 0, 0, 20) // Add some margin below the text
        }

        // Create a smaller, circular button with a text symbol to access the detailed accelerometer data screen
        val detailButton = Button(this).apply {
            text = ">"
            setBackgroundResource(R.drawable.circular_button) // Set background to circular drawable
            setTextColor(android.graphics.Color.WHITE) // White text color
            setOnClickListener {
                showDetailScreen()
            }
            // Make the button small and adjust margin
            layoutParams = LinearLayout.LayoutParams(
                100, // Width
                100  // Height
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setMargins(0, 0, 0, 40) // Add margin at the bottom
            }
        }

        // Create a layout to hold the TextView and button
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(android.graphics.Color.BLACK) // Set background color to black
            addView(mainTextView)
            addView(detailButton)
        }

        setContentView(layout)
    }
}