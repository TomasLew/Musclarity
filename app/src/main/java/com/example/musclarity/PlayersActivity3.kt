package com.example.musclarity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.io.IOException

class PlayersActivity3 : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players3)

        lateinit var auth: FirebaseAuth
        val backButton: ImageView = findViewById(R.id.back_button)
        val spinner: Spinner = findViewById(R.id.position_spinner)
        val textName: TextView = findViewById(R.id.player_name)
        val EditButton: Button = findViewById(R.id.edit_button)
        val DeleteButton: Button = findViewById(R.id.delete_button)

        // Obtener datos del Intent
        val playerName = intent.getStringExtra("player_name")
        val playerPosition = intent.getStringExtra("player_position")

        Log.d("PlayersActivity3", "Player Name: $playerName")
        Log.d("PlayersActivity3", "Player Position: $playerPosition")

        // Configurar la vista con los datos recibidos
        if (playerName != null && playerPosition != null) {
            textName.text = playerName
            val spinnerItems = resources.getStringArray(R.array.spinner_position_items)

            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_position_item,
                spinnerItems
            )
            adapter.setDropDownViewResource(R.layout.spinner_position_item)
            spinner.adapter = adapter

            Log.d("PlayersActivity3", "Spinner adapter set")

            val pos = adapter.getPosition(playerPosition)
            if (pos >= 0) {
                spinner.setSelection(pos)
            }
            else {
                Log.d("PlayersActivity3", "Position not found in adapter")
            }
        } else {
            textName.text = "Nombre no disponible"
            Log.d("PlayersActivity3", "Player name or position is null")
        }


        // Move to previous activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, PlayersActivity::class.java)
            startActivity(intent1)
        }

        EditButton.setOnClickListener{
            auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            val user = auth.currentUser
            if (user != null) {
                val email = user.email
                val newplayerName = textName.text.toString()
                val newplayerPosition = spinner.selectedItem.toString()
                val f0 = 0
                var documentId : String

                if (playerPosition != "Select Position" && email != null){
                    val collectionName = "Jugadores - $email"
                    val col = db.collection(collectionName)
                    val query = col
                        .whereEqualTo("Nombre", playerName)
                        .whereEqualTo("Posición", playerPosition)

                    query.get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                for (document in querySnapshot.documents) {
                                    documentId = document.id
                                    val data = hashMapOf(
                                        "Nombre" to newplayerName,
                                        "Posición" to newplayerPosition,
                                        "F0" to f0
                                    )
                                    col.document(documentId)
                                        .update(data as Map<String, Any>)
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Actualización exitosa",Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener{ exception ->
                                            Toast.makeText(this,"Error en la actualización de datos: " + exception,Toast.LENGTH_SHORT).show()
                                        }
                                }

                            } else {
                                Log.d("Firestore", "No se encontraron documentos con los datos provistos")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error accediendo a los documentos: ", exception)
                        }

                }
                val intent_back = Intent(this, PlayersActivity::class.java)
                startActivity(intent_back)

            }
        }

        /*// Set the hint text color to gray
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    (parent.getChildAt(0) as? TextView)?.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey_hint))
                    flagPosition = false
                    updateButton(flagName, flagPosition, flagImg, addPlayerButton)
                } else {
                    (parent.getChildAt(0) as? TextView)?.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    flagPosition = true
                    updateButton(flagName, flagPosition, flagImg, addPlayerButton)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }

        // Text change listener for editText1
        textName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable the button if both EditText fields are not empty
                flagName = !s.isNullOrBlank()
                updateButton(flagName, flagPosition, flagImg, addPlayerButton)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Add player on click
        addPlayerButton.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            val user = auth.currentUser
            if (user != null) {
                val email = user.email
                val playerName = textName.text.toString()
                val playerPosition = spinner.selectedItem.toString()
                val f0 = 0

                if (playerPosition != "Select Position" && email != null){
                    val collectionName = "Jugadores - $email"
                    val data = hashMapOf(
                        "Nombre" to playerName,
                        "Posición" to playerPosition,
                        "F0" to f0
                    )

                    db.collection(collectionName)
                        .add(data)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{e -> }

                    val intent = Intent(this, PlayersActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Please select a valid position", Toast.LENGTH_SHORT).show()
                }
            }
        }

         */

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage!!))

            // Save the bitmap to internal storage
            saveToInternalStorage(bitmap)

            // Set the image view to display the saved image
            imageView.setImageBitmap(bitmap)

            imageView.setImageURI(selectedImage)
            imageView.visibility = ImageView.VISIBLE
        }
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap) {
        try {
            val fileOutputStream = openFileOutput("badge_img.png", MODE_PRIVATE)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}