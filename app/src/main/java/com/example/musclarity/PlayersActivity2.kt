package com.example.musclarity

import android.app.Activity
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
import org.w3c.dom.Text
import java.io.IOException

class PlayersActivity2 : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var addPlayerButton: Button
    private var imageDownloadUrl: String = ""
    private var flagClicked: Boolean = false
    private var flagImg: Boolean = false
    private var flagName: Boolean = false
    private var flagPosition: Boolean = false
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players2)

        auth = FirebaseAuth.getInstance()
        addPlayerButton = findViewById(R.id.register_button)

        val backButton: ImageView = findViewById(R.id.back_button)
        val spinner: Spinner = findViewById(R.id.position_spinner)
        val uploadButton: TextView = findViewById(R.id.upload_button)
        val textName: TextView = findViewById(R.id.player_name)
        imageView = findViewById(R.id.player_img)

        uploadButton.setOnClickListener {
            openGallery()
            }

        // Move to previous activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, PlayersActivity::class.java)
            startActivity(intent1)
        }

        // Get the array resource containing hint and actual data items
        val spinnerItems = resources.getStringArray(R.array.spinner_position_items)

        // Create an ArrayAdapter using the string array and a default spinner layout.
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_position_item,
            spinnerItems
        )

        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(R.layout.spinner_position_item)

        // Apply the adapter to the spinner.
        spinner.adapter = adapter

        // Set the hint text color to gray
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
        addPlayerButton.setOnClickListener() {
            flagClicked = true

            val user = auth.currentUser

            if (user != null) {
                val email = user.email
                val playerName = textName.text.toString()
                val playerPosition = spinner.selectedItem.toString()
                val f0 = 0
                Log.d("URL", "Image URL: ${imageDownloadUrl}")
                if (playerPosition != "Select Position" && email != null){
                    val collectionName = "Jugadores - $email"
                    val data = hashMapOf(
                        "Nombre" to playerName,
                        "Posición" to playerPosition,
                        "F0" to f0,
                        "url" to imageDownloadUrl
                    )

                    val db = FirebaseFirestore.getInstance()
                    db.collection(collectionName)
                        .add(data)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    val intent = Intent(this, PlayersActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Please select a valid position", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun updateButton(flag1: Boolean, flag2: Boolean, flag3: Boolean, button: Button) {
        if (flag1 && flag2 && flag3) {
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
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val textName: TextView = findViewById(R.id.player_name)
            val name = textName.text.toString()

            uploadImageToFirebase(selectedImage!!, name) { imageUrl ->
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
            flagImg = true
            Log.d("Flags", "Name: $flagName, Pos: $flagPosition, Img: $flagImg")
            updateButton(flagName, flagPosition, flagImg, addPlayerButton)
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

    private fun uploadImageToFirebase(imageUri: Uri, playerName: String, callback: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageReference = storageReference.child("images/$playerName.jpg")

        imageReference.putFile(imageUri)
            .addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    callback(downloadUrl)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                callback("") // Llamar al callback con un valor vacío en caso de falla
            }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}