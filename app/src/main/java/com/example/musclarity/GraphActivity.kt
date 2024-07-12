package com.example.musclarity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import kotlin.math.pow
import kotlin.math.round

class GraphActivity : AppCompatActivity() {

    private var deviceName: String? = null
    private var deviceAddress: String? = null

    // Define LineChart and LineDataSet variables
    private lateinit var mpLineChart: LineChart
    private lateinit var lineDataSet: LineDataSet
    private var dataSet: MutableList<Float> = mutableListOf<Float>()
    private var medians_unique: MutableList<Double> = mutableListOf<Double>()
    private var fatigue_perc_unique: MutableList<Int> = mutableListOf<Int>()
    private var median_freq_DS: MutableList<Double> = mutableListOf<Double>()
    private var fatigue_perc_DS: MutableList<Int> = mutableListOf<Int>()
    private var fs_array = mutableListOf<Float>()
    private var fatigue_flag1: Boolean = false
    private var fatigue_flag2: Boolean = false
    private lateinit var lineData: LineData
    private var lastTimestamp: Long = System.currentTimeMillis()
    private var med_dataSet: MutableList<Double> = mutableListOf<Double>()
    private var med_lastTimestamp: Long = System.currentTimeMillis()

    private val WINDOW_SIZE_SECONDS = 30
    private val WINDOW_OVERLAP_PERCENTAGE = 0.5
    private val SAMPLE_RATE = 180.0
    private val LOW_CUTOFF = 10.0
    private val HIGH_CUTOFF = 120.0
    private val Calib_window=90
    var f_0 : Float = 0f

    // Fatigue Bar
    private lateinit var fatigueBar: GradientProgressBar
    private lateinit var calibActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var auth: FirebaseAuth

//    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
//        if (key == "flag") {
//            val isCalibrating = prefs.getBoolean("flag", false)
//            Log.d("Esta calibrando jose", "$isCalibrating")
//        }
//    }


    // SHARED PREFERENCES
    private val PREF_NAME = "MyPref"
    private val KEY_VARIABLE = "deviceName"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        enableEdgeToEdge()


        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)



        // UI Initialization
        val buttonConnect = findViewById<Button>(R.id.buttonConnect)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE
        val textViewInfo = findViewById<TextView>(R.id.textViewInfo)
        val buttonToggle = findViewById<Button>(R.id.buttonToggle)
        buttonToggle.isEnabled = false
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setBackgroundColor(resources.getColor(R.color.bg_2))
        val backButton: ImageView = findViewById(R.id.back_button)
        val exportButton: ImageView = findViewById(R.id.button_export)
        var mpLineChart: LineChart = findViewById(R.id.line_chart)
        val logoColor = ContextCompat.getColor(this, R.color.logoColor)
        val logoColorTransparente = ContextCompat.getColor(this, R.color.logoColor_transparente)
        val instructionsButton = findViewById<TextView>(R.id.instructions_button)

        // Initialize LineChart
        mpLineChart = findViewById(R.id.line_chart)
        lineDataSet = LineDataSet(ArrayList(), "Real-Time Data")
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawValues(false)
        lineData = LineData(lineDataSet)
        mpLineChart.data = lineData
        mpLineChart.invalidate()
        var n: Int = 0
        var x: Float = 0f
        val fs: Float = 250f
        var i: Int = 0
        var t: Float = 0f
        var fatigue: Int = 100

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val infoPlayers = getSharedPreferences("MyPlayerPref", Context.MODE_PRIVATE)
        val playerName: String = infoPlayers.getString("Player", "").toString()

        if (user != null) {
            var documentId: String
            val email = user.email
            val db = FirebaseFirestore.getInstance()
            val collectionName = "Jugadores - $email"
            val col = db.collection(collectionName)
            val query = col
                .whereEqualTo("Nombre", playerName)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    f_0 = if (!querySnapshot.isEmpty) {
                        querySnapshot.documents.firstOrNull()?.getLong("F0")?.toFloat() ?: 0f
                    } else {
                        0f
                    }

                    /* if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            f_0 = document.getLong("F0")?.toFloat()!!
                        }
                    }
                    else {
                        f_0 = 0f
                    }
                     */
                }
        }

        var counter=1f

        val fatigueTxt: TextView = findViewById(R.id.fatigue_txt)
        fatigueBar = findViewById(R.id.fatigue_bar)


        // Example usage: set the percentage to 75%
        fatigueBar.setPercentage(fatigue)
        fatigueTxt.setText("Energy: $fatigue%")

        // Move to squad activity on click
        instructionsButton.setOnClickListener {
            val intent1 = Intent(this, InstructionsActivity::class.java)
            startActivity(intent1)
        }

        // Move to squad activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, SquadActivity::class.java)
            startActivity(intent1)
        }

        val bandera = getSharedPreferences("MyPlayerPref", Context.MODE_PRIVATE)
        val isCalib: Boolean = bandera.getBoolean("flag", false)

        if (isCalib || f_0 == 0f) {
            fatigueBar.visibility = View.INVISIBLE
            fatigueTxt.visibility = View.INVISIBLE
        }

        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = intent.getStringExtra("deviceName")

        if (deviceName != null) {
            // Get the device address to make BT Connection
            deviceAddress = intent.getStringExtra("deviceAddress")
            // Show progree and connection status
            toolbar.subtitle = "Connecting to $deviceName..."
            progressBar.visibility = View.VISIBLE
            buttonConnect.isEnabled = false

            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            createConnectThread = CreateConnectThread(bluetoothAdapter, deviceAddress)
            createConnectThread!!.start()
        }

        val signalProcessingUtils = SignalProcessingUtils()

        handler = object : Handler(Looper.getMainLooper()) {


            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    CONNECTING_STATUS -> handleConnectingStatus(msg)
                    MESSAGE_READ -> handleMessageRead(msg)

                }
            }

            private fun handleConnectingStatus(msg: Message) {
                when (msg.arg1) {
                    1 -> {
                        val editor = sharedPreferences.edit()
                        editor.putString("deviceName", deviceName)
                        editor.putString("deviceAddress",deviceAddress)
                        editor.apply()
                    }

                    -1 -> {
                        toolbar.subtitle = "Device fails to connect"
                        progressBar.visibility = View.GONE
                        buttonConnect.isEnabled = true
                        buttonToggle.setBackgroundColor(logoColorTransparente)
                    }
                }
            }

            private fun handleMessageRead(msg: Message) {

                val arduinoMsg = msg.obj.toString() // Leer mensaje desde
                handleDataProcessing(arduinoMsg)


                val deviceName = sharedPreferences.getString("deviceName", "")
                if (!deviceName.isNullOrBlank()) {
                    toolbar.subtitle = "Connected to $deviceName"
                    progressBar.visibility = View.GONE
                    buttonConnect.isEnabled = true
                    buttonToggle.isEnabled = true
                    buttonToggle.setBackgroundColor(logoColor)
                }

                if (buttonToggle.text == "Turn On") {
                    handleTurnOnState()

                    mpLineChart.visibility = View.INVISIBLE
                } else {
                    //handleDataProcessing(arduinoMsg)
                    mpLineChart.visibility = View.VISIBLE
                }
            }

            fun handleTurnOnState() {
                mpLineChart.axisLeft.apply {
                    axisMinimum = 0f // Límite mínimo
                    axisMaximum = 1023f // Límite máximo
                }
                mpLineChart.axisRight.isEnabled = false // Desactiva el eje derecho si no lo necesitas

                imageView.setBackgroundColor(resources.getColor(R.color.transparente))
                //textViewInfo.text = ""
                lineDataSet.clear()
                lineData.notifyDataChanged()
                mpLineChart.notifyDataSetChanged()
                mpLineChart.invalidate()
            }

            fun handleDataProcessing(arduinoMsg: String) {
                imageView.setBackgroundColor(resources.getColor(R.color.logoColor))
                val timePassed: Float = ((System.currentTimeMillis() - lastTimestamp).toFloat())
                x += timePassed
                lastTimestamp = System.currentTimeMillis()
                if (n % 100 == 0) {
                    val Fs = x / 100
                    textViewInfo.text = "Arduino Message : $arduinoMsg, x : $Fs"
                    Log.d("Valores", "Arduino Message : $arduinoMsg, x : $Fs")
                    x = 0f
                }

                val value = arduinoMsg.toFloatOrNull()

                if (value != null) {
                    val entry = Entry(n.toFloat() / fs, value)
                    lineDataSet.addEntry(entry)
                    lastTimestamp = System.currentTimeMillis()
                    dataSet.add(value)
                    if (n > 0) {
                        fs_array.add(timePassed)
                    }
                    n += 1
                    if (medians_unique.size > 0) {
                        median_freq_DS.add(medians_unique.last())
                    } else {
                        median_freq_DS.add(0.toDouble())
                    }
                    if (fatigue_perc_unique.size > 0) {
                        fatigue_perc_DS.add(fatigue_perc_unique.last())
                    } else {
                        fatigue_perc_DS.add(100)
                    }

                    med_dataSet.add(value.toDouble())

                    processMedianAndFatigue("MyPlayerPref",Calib_window)
                }

                if (lineDataSet.entryCount > 10 * fs) {
                    lineDataSet.removeFirst()
                }

                if (n % 25 == 0) {
                    lineData.notifyDataChanged()
                    mpLineChart.notifyDataSetChanged()
                    mpLineChart.moveViewToX(lineData.entryCount.toFloat())
                }
            }

            private fun processMedianAndFatigue(CalibKey: String, calibWindow: Int) {
                val deltaTimeSeconds = (System.currentTimeMillis() - med_lastTimestamp) / 1000.0
                var ventana = WINDOW_SIZE_SECONDS

                val bandera = getSharedPreferences(CalibKey, Context.MODE_PRIVATE)
                val isCalib: Boolean = bandera.getBoolean("flag", false)
                // Log.d("Esta calibrando jose", "${isCalib}")

                if (isCalib || f_0 == 0f) {
                    val prev : Boolean = true
                    Log.d("Se actualiza la ventana","true")
                    ventana = calibWindow
                    fatigueBar.visibility = View.INVISIBLE
                    fatigueTxt.visibility = View.INVISIBLE
                }

                if (deltaTimeSeconds >= ventana) {
                    try {
                        // Preparar los datos originales
                        val originalData = med_dataSet.toDoubleArray()
                        val originalLength = originalData.size
                        val desiredLength = signalProcessingUtils.nextPowerOfTwo(originalLength)
                        val paddedData = DoubleArray(desiredLength)

                        // Copiar los datos originales y rellenar con ceros
                        System.arraycopy(originalData, 0, paddedData, 0, originalLength)
                        for (l in originalLength until desiredLength) {
                            paddedData[l] = 0.0
                        }

                        // Procesar los datos para obtener las medianas
                        val medians = signalProcessingUtils.process(
                            paddedData,
                            1,
                            WINDOW_OVERLAP_PERCENTAGE,
                            SAMPLE_RATE,
                            LOW_CUTOFF,
                            HIGH_CUTOFF
                        )
                        medians_unique.add(medians[0])


                        if (!isCalib || f_0!=0f) {
                            try {
                                // Calcular el porcentaje de fatiga
                                val fatiguePercentage = signalProcessingUtils.percFatigue(
                                    medians.average(),
                                    maxFrec = f_0
                                )
                                Log.d("Fatigue Percentage", fatiguePercentage.toString())

                                if (counter != 0f) {
                                    runOnUiThread {
                                        fatigueBar.setPercentage(fatiguePercentage.toInt())
                                        fatigueTxt.text = "Energy: ${fatiguePercentage.toInt()}%"
                                        if (fatiguePercentage.toInt() < 50) {
                                            showAlertDialog2()
                                            fatigue_flag2 = true
                                        } else if (fatiguePercentage.toInt() < 75) {
                                            showAlertDialog()
                                            fatigue_flag1 = true
                                        }
                                    }
                                    fatigue_perc_unique.add(fatiguePercentage.toInt())
                                }
                                /*else {
                                    f_0 = medians[0].toInt()
                                    if (f_0 == 0) {
                                        counter -= 1
                                    }

                                    Log.d("F0", f_0.toString())
                                    fatigue_perc_unique.add(100)
                                }*/
                                counter += 1
                                Log.d("counter", "Counter: ${counter}")

                                // Actualizar los datos y el tiempo de marca
                                val halfIndex = med_dataSet.size / 2
                                med_dataSet =
                                    med_dataSet.subList(halfIndex, med_dataSet.size).toMutableList()
                                med_lastTimestamp =
                                    (System.currentTimeMillis() - round(deltaTimeSeconds / 2)).toLong()
                            } catch (e: Exception) {
                                Log.e("Error", "Error processing data: ${e.message}", e)
                            }
                        } else {

                            val editor_f = bandera.edit()
                            editor_f.putBoolean("flag", false)
                            editor_f.apply()

                            val intent3 = Intent(this@GraphActivity, CalibActivity::class.java)
                            intent3.putExtra("F0", medians[0].toString())
                            startActivity(intent3)

                        }

                    } catch (e: Exception) {
                        Log.e("Error", "Error during initial processing: ${e.message}", e)
                    }


                }
            }
        }



            // Select Bluetooth Device
        buttonConnect.setOnClickListener {
            val intent = Intent(
                this@GraphActivity,
                SelectDeviceActivity::class.java
            )
            startActivity(intent)
        }

        // Button to ON/OFF LED on Arduino Board
        buttonToggle.setOnClickListener {
            val btnState = buttonToggle.text.toString().lowercase(Locale.getDefault())
            when (btnState) {
                "turn on" -> buttonToggle.text = "Turn Off"
                "turn off" -> buttonToggle.text = "Turn On"
            }
        }

        // Button to export data
        exportButton.setOnClickListener {
            checkAndRequestPermissions()
            if (isExternalStorageWritable()) {
                i = saveDataToCSV(i)
            }
        }
    }
//    override fun onDestroy() {
//        super.onDestroy()
//        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
//    }


    private fun lanzarCalibActivity() {
        val intent = Intent(this, CalibActivity::class.java)
        calibActivityResultLauncher.launch(intent)
    }
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun roundToDecimalPlaces(value: Float, decimalPlaces: Int): Float {
        val factor = 10.0.pow(decimalPlaces).toFloat()
        return round(value * factor) / factor
    }

    private fun saveDataToCSV(i: Int): Int {

        val fileName = "output_$i.csv"
        val file = File(getExternalFilesDir(null), fileName)

        var csvData = ""
        if (fatigue_flag2) {
            csvData += "Signal (Fs = ${roundToDecimalPlaces(1000/(fs_array.sum()/fs_array.size),2)}Hz),Median Frequencies,Energy Percentage,Diagnosis: Fatigue\n"//dataSet.joinToString("\n")
        }
        else if (fatigue_flag1) {
            csvData += "Signal (Fs = ${roundToDecimalPlaces(1000/(fs_array.sum()/fs_array.size),2)}Hz),Median Frequencies,Energy Percentage,Diagnosis: Risk of Fatigue\n"//dataSet.joinToString("\n")

        } else {
            csvData += "Signal (Fs = ${roundToDecimalPlaces(1000/(fs_array.sum()/fs_array.size),2)}Hz),Median Frequencies,Energy Percentage,Diagnosis: NO Fatigue\n"//dataSet.joinToString("\n")

        }

        for (l in 0 until dataSet.size) {
            if (median_freq_DS[l] == 0.toDouble()) {
                csvData += "${dataSet[l]},-,${fatigue_perc_DS[l]}\n"
            } else {
                csvData += "${dataSet[l]},${median_freq_DS[l]},${fatigue_perc_DS[l]}\n"
            }
        }

        try {
            FileOutputStream(file).use { fos ->
                fos.write(csvData.toByteArray())
                Log.d("GraphActivity", "Data saved to ${file.absolutePath}")
                Log.d("GraphActivity", "DataSet contents: $dataSet")
            }
        } catch (e: IOException) {
            Log.e("GraphActivity", "Error saving data to CSV", e)
        }
        dataSet = mutableListOf<Float>()
        median_freq_DS = mutableListOf<Double>()
        fatigue_perc_DS = mutableListOf<Int>()
        fs_array = mutableListOf<Float>()
        fatigue_flag1 = false
        fatigue_flag2 = false

        val k: Int = i+1

        return k
    }



    // Other existing methods...

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    @SuppressLint("MissingPermission")
    class CreateConnectThread(bluetoothAdapter: BluetoothAdapter, address: String?) : Thread() {
        init {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            val bluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
            var tmp: BluetoothSocket? = null
            val uuid = bluetoothDevice.uuids[0].uuid

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Socket's create() method failed", e)
            }
            mmSocket = tmp
        }

        @SuppressLint("MissingPermission")
        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter.cancelDiscovery()
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket!!.connect()
                Log.e("Status", "Device connected")
                handler!!.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget()
            } catch (connectException: IOException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket!!.close()
                    Log.e("Status", "Cannot connect to device")
                    handler!!.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget()
                } catch (closeException: IOException) {
                    Log.e(ContentValues.TAG, "Could not close the client socket", closeException)
                }
                return
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = ConnectedThread(mmSocket)
            connectedThread!!.run()
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Could not close the client socket", e)
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    class ConnectedThread(private val mmSocket: BluetoothSocket?) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = mmSocket!!.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {
            }

            mmInStream = tmpIn
            mmOutStream = tmpOut
        }

        override fun run() {
            val buffer = ByteArray(1024) // buffer store for the stream
            var bytes = 0 // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = mmInStream!!.read().toByte()
                    var readMessage: String
                    if (buffer[bytes] == '\n'.code.toByte()) {
                        readMessage = String(buffer, 0, bytes)
                        //Log.e("Arduino Message", readMessage)
                        handler!!.obtainMessage(MESSAGE_READ, readMessage).sendToTarget()
                        bytes = 0
                    } else {
                        bytes++
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        fun write(input: String?) {
            val bytes = input!!.toByteArray() //converts entered String into bytes
            try {
                mmOutStream!!.write(bytes)
            } catch (e: IOException) {
                Log.e("Send Error", "Unable to send message", e)
            }
        }

        /* Call this from the main activity to shutdown the connection */
        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (e: IOException) {
            }
        }
    }
    private fun roundToDecimals(num: Float, decimals: Int): Float {
        val factor = 10.0.pow(decimals)
        return (round(num.toDouble() * factor) / factor).toFloat()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (createConnectThread != null) {
            createConnectThread!!.cancel()
        }
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    companion object {
        var handler: Handler? = null
        var mmSocket: BluetoothSocket? = null
        var connectedThread: ConnectedThread? = null
        var createConnectThread: CreateConnectThread? = null

        private const val CONNECTING_STATUS = 1
        private const val MESSAGE_READ = 2

    }

    private fun showAlertDialog() {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.fatigue_alert_layout, null)

        // Create the AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()


        // Retrieve the positive button and apply the custom style
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.logoColor))
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.myBackgroundColor))

        dialog.window?.setBackgroundDrawableResource(R.color.myBackgroundColor)
    }

    private fun showAlertDialog2() {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.fatigue_alert_layout2, null)

        // Create the AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()


        // Retrieve the positive button and apply the custom style
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.logoColor))
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.myBackgroundColor))

        dialog.window?.setBackgroundDrawableResource(R.color.myBackgroundColor)
    }
}
