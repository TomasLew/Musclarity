package com.example.musclarity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class RegisterActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val backButton: ImageView= findViewById(R.id.back_button)
        val registerButton: Button = findViewById(R.id.register_button)
        val textUser: EditText = findViewById(R.id.new_username)
        val textEmail: EditText = findViewById(R.id.email)
        val textPass: EditText = findViewById(R.id.new_password)
        val textConfirmPass: EditText = findViewById(R.id.confirm_password)
        val logoColor = ContextCompat.getColor(this, R.color.logoColor)
        val logoColorTransparente = ContextCompat.getColor(this, R.color.logoColor_transparente)
        var flagUser: Boolean = false
        var flagEmail: Boolean = false
        var flagPass: Boolean = false
        var flagConfirmPass: Boolean = false
        var flagImg: Boolean = false
        val uploadButton: TextView = findViewById(R.id.upload_button)
        imageView = findViewById(R.id.badge_img)

        fun updateButtonState(button: Button, flag1: Boolean, flag2: Boolean, flag3: Boolean, flag4: Boolean, flag5: Boolean, text1: EditText, text2: EditText) {
            val txt1 = text1.text.toString()
            val txt2 = text2.text.toString()
            if (flag1 && flag2 && flag3 && flag4 && flag5 && txt1 == txt2) {
                // Set background color when button is enabled
                button.isEnabled = true
                button.setBackgroundColor(logoColor)// Change to desired color
            } else {
                // Set background color when button is disabled
                button.isEnabled = false
                button.setBackgroundColor(logoColorTransparente) // Change to desired color
            }
        }

        uploadButton.setOnClickListener {
            openGallery()
            flagImg = true
            updateButtonState(registerButton, flagUser, flagEmail, flagPass, flagConfirmPass, flagImg, textPass, textConfirmPass)
            }

        // Text change listener for textUser
        textUser.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable the button if both EditText fields are not empty
                flagUser = !s.isNullOrBlank()
                updateButtonState(registerButton, flagUser, flagEmail, flagPass, flagConfirmPass, flagImg, textPass, textConfirmPass)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Text change listener for textEmail
        textEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable the button if both EditText fields are not empty
                flagEmail = !s.isNullOrBlank()
                updateButtonState(registerButton, flagUser, flagEmail, flagPass, flagConfirmPass, flagImg, textPass, textConfirmPass)            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Text change listener for textPass
        textPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable the button if both EditText fields are not empty
                flagPass = !s.isNullOrBlank()
                updateButtonState(registerButton, flagUser, flagEmail, flagPass, flagConfirmPass, flagImg, textPass, textConfirmPass)            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Text change listener for textConfirmPass
        textConfirmPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable the button if both EditText fields are not empty
                flagConfirmPass = !s.isNullOrBlank()
                updateButtonState(registerButton, flagUser, flagEmail, flagPass, flagConfirmPass, flagImg, textPass, textConfirmPass)            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        // Move to main activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, MainActivity::class.java)
            startActivity(intent1);
        }

        // Move to main activity on click
        registerButton.setOnClickListener {
            val intent = Intent(this, SquadActivity::class.java)
            startActivity(intent);
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
