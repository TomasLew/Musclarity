package com.example.musclarity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CalibActivity : AppCompatActivity() {
    var isCalibrating = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calibration_activity)

        val buttonToggle: Button = findViewById(R.id.button_toggle)
        val backButton: ImageView = findViewById(R.id.button_back)
        val instructionsTextView: TextView = findViewById(R.id.instructions_text)

        val colorLogoColor = ContextCompat.getColor(this, R.color.logoColor)

        val step1 = getColorizedSpannable(getString(R.string.paso1)+ ' ' + getString(R.string.step1), getString(R.string.paso1), colorLogoColor)
        val step2 = getColorizedSpannable(getString(R.string.paso2)+ ' ' + getString(R.string.step2), getString(R.string.paso2), colorLogoColor)
        val step3 = getColorizedSpannable(getString(R.string.paso3)+ ' ' + getString(R.string.step3), getString(R.string.paso3), colorLogoColor)
        val step4 = getColorizedSpannable(getString(R.string.paso4)+ ' ' + getString(R.string.step4), getString(R.string.paso4), colorLogoColor)
        val step5 = getColorizedSpannable(getString(R.string.paso5)+ ' ' + getString(R.string.step5), getString(R.string.paso5), colorLogoColor)
        val step6 = getColorizedSpannable(getString(R.string.paso6)+ ' ' + getString(R.string.step6), getString(R.string.paso6), colorLogoColor)

        val instructions = SpannableStringBuilder()
        instructions.append(step1).append("\n\n")
        instructions.append(step2).append("\n\n")
        instructions.append(step3).append("\n\n")
        instructions.append(step4).append("\n\n")
        instructions.append(step5).append("\n\n")
        instructions.append(step6)

        instructionsTextView.text = instructions


        val infoPlayers = getSharedPreferences("MyPlayerPref", Context.MODE_PRIVATE)
        val playerName: String = infoPlayers.getString("Player", "").toString()
        val playerPosition: String = infoPlayers.getString("Position", "").toString()
        val playerUrl: String = infoPlayers.getString("url", "").toString()
        val f0: String = infoPlayers.getString("F0", "").toString()
        val squadPosition : String = infoPlayers.getString("squad_position", "").toString()


        var documentId: String

        if (intent.getBooleanExtra("calibrationSuccess", false)) {
            Toast.makeText(this, "Successful calibration!", Toast.LENGTH_SHORT).show()
        }
        buttonToggle.setOnClickListener {
            if (!isCalibrating) {
                isCalibrating = true

                // Mostrar un Toast para verificar que el botón se ha presionado y la preferencia se ha actualizado
                Toast.makeText(this, "Calibration started: $isCalibrating", Toast.LENGTH_SHORT)
                    .show()

                val intent2 = Intent(this, GraphActivity::class.java)
                startActivity(intent2)

            }
        }
        val F0txt = intent.getStringExtra("F0")
        if (!F0txt.isNullOrBlank()) {
            val F0 = F0txt.toFloat()
            Log.d("Frecuencia registrada", "$F0")
            if (F0 != 0f) {
                val auth = FirebaseAuth.getInstance()
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
                                                "Successful calibration!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val intent_players = Intent(this, PlayersActivity3::class.java)
                                            intent_players.putExtra("player_name", playerName)
                                            intent_players.putExtra("player_position", playerPosition)
                                            intent_players.putExtra("url", playerUrl)
                                            intent_players.putExtra("F0", F0)
                                            intent_players.putExtra("squad_position", squadPosition)
                                            startActivity(intent_players)
                                        }

                                        .addOnFailureListener { e ->
                                            // Manejar errores
                                            Toast.makeText(
                                                this,
                                                "Data update error: ${e}. Please try again.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }

                            } else {
                                Log.e("Error", "Error processing data. Please try again.")


                            }

                        }
                }

            } else {
                Toast.makeText(
                    this,
                    "Calibration error. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Move to squad activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, PlayersActivity::class.java)
            startActivity(intent1)
        }
        fun getColorizedSpannable(text: String, color: Int): SpannableString {
            val spannableString = SpannableString(text)
            spannableString.setSpan(ForegroundColorSpan(color), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableString
        }
    }
    private fun getColorizedSpannable(fullText: String, partToColor: String, color: Int): SpannableString {
        val spannableString = SpannableString(fullText)
        val start = fullText.indexOf(partToColor)
        val end = start + partToColor.length

        if (start >= 0) {
            spannableString.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannableString
    }

}



