package com.example.musclarity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class CalibActivity : AppCompatActivity() {
    lateinit var bandera: SharedPreferences

    companion object {
        var newActivityHandler: Handler? = null
    }

    var isCalibrating = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calibration_activity)

        // Ejemplo de inicio de la actividad de gráfico desde calibración
        val buttonToggle: Button = findViewById(R.id.button_toggle)
        var information_handler = GraphActivity.handler
        val instructionsTextView: TextView = findViewById(R.id.instructions_text)

        val step1 = getString(R.string.step1)
        val step2 = getString(R.string.step2)
        val step3 = getString(R.string.step3)
        val step4 = getString(R.string.step4)
        val step5 = getString(R.string.step5)
        val step6 = getString(R.string.step6)

        val instructions = "$step1\n$step2\n$step3\n$step4\n$step5\n$step6"
        instructionsTextView.text = instructions

        val infoPlayers = getSharedPreferences("MyPlayerPref", Context.MODE_PRIVATE)
        val playerName : String  = infoPlayers.getString("Player", "").toString()

        var documentId : String

        if (intent.getBooleanExtra("calibrationSuccess", false)) {
            Toast.makeText(this, "Calibration exitosa", Toast.LENGTH_SHORT).show()
        }
        buttonToggle.setOnClickListener {
            if (!isCalibrating) {
                isCalibrating = true

                // Mostrar un Toast para verificar que el botón se ha presionado y la preferencia se ha actualizado
                Toast.makeText(this, "Calibración iniciada: $isCalibrating", Toast.LENGTH_SHORT)
                    .show()

                val intent2 = Intent(this, GraphActivity::class.java)
                startActivity(intent2)

            }
        }
        val F0txt = intent.getStringExtra("F0")
        if (!F0txt.isNullOrBlank()) {
            val F0 = F0txt.toFloat()
            if (F0 != 0f) {
                val auth = FirebaseAuth.getInstance()
                Log.d("Frecuencia registrada", "$F0")
                val user = auth.currentUser
                if (user != null) {
                    val userId = user.uid
                    val email = user.email
                    Log.d("UserID", "El ID del usuario es: $userId")

                    val db = FirebaseFirestore.getInstance()
                    val collectionName = "Jugadores - $email"
                    val col = db.collection(collectionName)
                    val query = col
                        .whereEqualTo("Nombre", playerName)

                    query.get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                for (document in querySnapshot.documents) {
                                    documentId = document.id

                                    col.document(documentId)
                                        .update("F0", F0)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "Calibración exitosa!: $isCalibrating",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        .addOnFailureListener { e ->
                                            // Manejar errores
                                            Toast.makeText(
                                                this,
                                                "Error al actualizar F0: $isCalibrating",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    Log.d("PlayersActivity3 - DATA", "${data}")

                                    col.document(documentId)
                                        .update(data as Map<String, Any>)
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Actualización exitosa",Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener{ exception ->
                                            Toast.makeText(this,"Error en la actualización de datos: " + exception,Toast.LENGTH_SHORT).show()
                                        }
                                }
                    docRef.update("F0", F0)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Calibración exitosa!: $isCalibrating",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        .addOnFailureListener { e ->
                            // Manejar errores
                            Toast.makeText(
                                this,
                                "Error al actualizar F0: $isCalibrating",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                } else {
                    Log.e("Error", "Error processing data")


                }



            }
            else{
                Toast.makeText(
                    this,
                    "F0 registrada es 0, volver a calibrar: $isCalibrating",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}

