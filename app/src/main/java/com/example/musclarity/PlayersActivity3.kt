package com.example.musclarity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.io.IOException
import java.util.UUID
import android.content.SharedPreferences

class PlayersActivity3 : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imageDownloadUrl: String = ""
    lateinit var jugadores: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players3)

        lateinit var auth: FirebaseAuth
        val backButton: ImageView = findViewById(R.id.back_button)
        val spinner: Spinner = findViewById(R.id.position_spinner)
        val textName: TextView = findViewById(R.id.player_name)
        imageView = findViewById(R.id.imagen)
        val EditButton: Button = findViewById(R.id.edit_button)
        val DeleteButton: Button = findViewById(R.id.delete_button)
        val uploadButton: TextView = findViewById(R.id.upload_button)
        val joseboton:Button=findViewById(R.id.joseboton)
        // Obtener datos del Intent
        val playerName = intent.getStringExtra("player_name")
        val playerPosition = intent.getStringExtra("player_position")
        val playerUrl = intent.getStringExtra("url")

        Log.d("PlayersActivity3", "Player Name: $playerName")
        Log.d("PlayersActivity3", "Player Position: $playerPosition")
        Log.d("PlayersActivity3", "Player URL: $playerUrl")

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

                                    col.document(documentId)
                                        .delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Jugador eliminado exitosamente",Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener{ exception ->
                                            Toast.makeText(this,"Error en la deleción de datos: " + exception,Toast.LENGTH_SHORT).show()
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
                val intent_prev = Intent(this, PlayersActivity::class.java)
                startActivity(intent_prev)

            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PlayersActivity3.REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PlayersActivity3.REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data

            uploadImageToFirebase(selectedImage!!, randomString(20)) { imageUrl ->
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

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}