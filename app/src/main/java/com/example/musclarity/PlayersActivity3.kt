package com.example.musclarity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.UUID
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.round
import android.content.Context as Context1

class PlayersActivity3 : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imageDownloadUrl: String = ""
    private var flagF0 : Boolean = false

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players3)

        val backButton: ImageView = findViewById(R.id.back_button)
        val spinner: Spinner = findViewById(R.id.position_spinner)
        val textName: TextView = findViewById(R.id.player_name)
        val textF0: TextView = findViewById(R.id.player_F0)
        imageView = findViewById(R.id.imagen)
        val EditButton: Button = findViewById(R.id.edit_button)
        val DeleteButton: Button = findViewById(R.id.delete_button)
        val uploadButton: TextView = findViewById(R.id.upload_button)
        val joseboton:TextView=findViewById(R.id.joseboton)
        val addButton: Button = findViewById(R.id.add_button)

        // Obtener datos del Intent
        var playerName = intent.getStringExtra("player_name")
        var playerPosition = intent.getStringExtra("player_position")
        var playerUrl = intent.getStringExtra("url")
        var playerF0 = intent.getStringExtra("F0")
        val squadPosition = intent.getStringExtra("squad_position")

        Log.d("PlayersActivity3", "Player Name: $playerName")
        Log.d("PlayersActivity3", "Player Position: $playerPosition")
        Log.d("PlayersActivity3", "Player URL: $playerUrl")
        Log.d("PlayersActivity3", "Player URL: $playerF0")
        Log.d("PlayersActivity3", "Selected Squad Position: $squadPosition")

        // Configurar la vista con los datos recibidos
        if (playerName != null && playerPosition != null) {
            textName.text = playerName
            if (playerF0?.toDouble()!!.toInt() != 0){
                var floatF0 = playerF0.toFloat()
                //floatF0 = roundToDecimals(floatF0, 2)
                textF0.text = "Calibration freq.: " + floatF0.toString() + "Hz"
                flagF0 = true
                updateButton(flagF0, addButton)
            }

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
        Picasso.get().load(playerUrl).into(imageView)

        // Move to previous activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, PlayersActivity::class.java)
            startActivity(intent1)
        }

        uploadButton.setOnClickListener {
            openGallery()
        }

        joseboton.setOnClickListener{
            val sharedPreferences_player = getSharedPreferences("MyPlayerPref", Context.MODE_PRIVATE)
            val editor_player: SharedPreferences.Editor = sharedPreferences_player.edit()
            editor_player.putString("Player", playerName)
            editor_player.putString("Position", playerPosition)
            editor_player.putString("url", playerUrl)
            editor_player.putString("F0",playerF0)
            editor_player.putString("squad_position", squadPosition)
            editor_player.putBoolean("flag", true)
            editor_player.apply()
            val intent2 = Intent(this, CalibActivity::class.java)
            startActivity(intent2)
        }

        EditButton.setOnClickListener{
            auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            val user = auth.currentUser
            if (user != null) {
                val email = user.email
                val newplayerName = textName.text.toString()
                val newplayerPosition = spinner.selectedItem.toString()
                val f0 = playerF0?.toFloat()
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
                                    if (imageDownloadUrl == ""){
                                        imageDownloadUrl = playerUrl.toString()
                                    } else {
                                        Picasso.get().load(imageDownloadUrl).into(imageView)
                                    }
                                    val data = hashMapOf(
                                        "Nombre" to newplayerName,
                                        "Posición" to newplayerPosition,
                                        "F0" to f0,
                                        "url" to imageDownloadUrl
                                    )

                                    playerName = newplayerName
                                    playerPosition = newplayerPosition
                                    playerF0 = f0.toString()
                                    playerUrl = imageDownloadUrl

                                    Log.d("PlayersActivity3 - DATA", "${data}")

                                    col.document(documentId)
                                        .update(data as Map<String, Any>)
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Success updating data.",Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener{ exception ->
                                            Toast.makeText(this,"Data update error: " + exception,Toast.LENGTH_SHORT).show()
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

        DeleteButton.setOnClickListener{
            auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            val user = auth.currentUser

            if (user != null) {
                val email = user.email
                var documentId : String

                if (email != null){
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
                                    val actUrl = document.getString("url") // Usa getString() para obtener el campo como String

                                    col.document(documentId)
                                        .delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Success deleting player.",Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener{ exception ->
                                            Toast.makeText(this,"Deletion error: " + exception,Toast.LENGTH_SHORT).show()
                                        }

                                    if (actUrl != null) {
                                        deleteImageFromFirebaseStorage(actUrl)
                                    } else {
                                        Log.d("Player deletion","The corresponding image could not be deleted because the url was not found.")
                                    }
                                }

                            } else {
                                Log.d("Firestore", "No se encontraron documentos con los datos provistos.")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error accediendo a los documentos: ", exception)
                        }

                }


            }
        }

        addButton.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            val user = auth.currentUser
            if (user != null) {
                val email = user.email
                val newplayerName = textName.text.toString()
                val newplayerPosition = spinner.selectedItem.toString()
                val f0 = playerF0?.toFloat()
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
                                    if (imageDownloadUrl == ""){
                                        imageDownloadUrl = playerUrl.toString()
                                    } else {
                                        Picasso.get().load(imageDownloadUrl).into(imageView)
                                    }
                                    val data = hashMapOf(
                                        "Nombre" to newplayerName,
                                        "Posición" to newplayerPosition,
                                        "F0" to f0,
                                        "url" to imageDownloadUrl
                                    )

                                    Log.d("PlayersActivity3 - DATA", "${data}")

                                    col.document(documentId)
                                        .update(data as Map<String, Any>)

                                }

                            } else {
                                Log.d("Firestore", "No se encontraron documentos con los datos provistos")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error accediendo a los documentos: ", exception)
                        }

                }
                val intent_back = Intent(this, SquadActivity::class.java)
                intent_back.putExtra("player_name", newplayerName)
                intent_back.putExtra("player_url", playerUrl)
                intent_back.putExtra("squad_position", squadPosition)
                startActivity(intent_back)
            }
        }
    }

    private fun roundToDecimals(num: Float, decimals: Int): Float {
        val factor = 10.0.pow(decimals)
        return (round(num.toDouble() * factor) / factor).toFloat()
    }

    private fun updateButton(flag1: Boolean, button: Button) {
        if (flag1) {
            // Set background color when button is enabled
            button.isEnabled = true
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.logoColor))// Change to desired color

        } else {
            // Set background color when button is disabled
            button.isEnabled = false
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.logoColor_transparente)) // Change to desired color
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PlayersActivity3.REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val email = user?.email
        val textName: TextView = findViewById(R.id.player_name)
        val name = textName.text.toString()

        val photoname = "${email.toString()}_${name}"
        Log.d("Upload", "Photoname: $photoname")

        if (requestCode == PlayersActivity3.REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data

            uploadImageToFirebase(selectedImage!!, photoname) { imageUrl ->
                imageDownloadUrl = imageUrl
                Log.d("Upload", "Image uploaded successfully, URL: $imageDownloadUrl")
            }

            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage!!))

            // Save the bitmap to internal storage
            saveToInternalStorage(bitmap)

            // Set the image view to display the saved image
            imageView.setImageBitmap(bitmap)

            imageView.setImageURI(selectedImage)
            imageView.visibility = ImageView.VISIBLE

        }
    }

    private fun randomString(i: Int): String {
        return UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, i)
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

    private fun uploadImageToFirebase(imageUri: Uri, playerName: String, callback: (String) -> Unit) {

        val storageReference = FirebaseStorage.getInstance().reference
        Log.d("storageReference", "${storageReference}")
        val imageReference = storageReference.child("images/$playerName.jpg")
        Log.d("imageReference", "${imageReference}")

        imageReference.putFile(imageUri)
            .addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.d("uploadImageToFirebase", "Initial URL: $downloadUrl")
                    callback(downloadUrl)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("uploadImageToFirebase","Error al subir la imagen: ${exception.message}")
                callback("") // Llamar al callback con un valor vacío en caso de falla
            }
    }

    fun deleteImageFromFirebaseStorage(imageUrl: String) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete()
            .addOnSuccessListener {
                // Archivo eliminado exitosamente
                Log.d("Delete Old Image from Firebase:","Success")
            }
            .addOnFailureListener { exception ->
                // Manejar la falla
                Log.d("Delete Old Image from Firebase:","Error: ${exception.message}")
                }
    }


    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}