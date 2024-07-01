package com.example.musclarity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text
import java.io.IOException

class PlayersActivity2 : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players2)

        val backButton: ImageView = findViewById(R.id.back_button)
        val spinner: Spinner = findViewById(R.id.position_spinner)
        val uploadButton: TextView = findViewById(R.id.upload_button)
        val textName: TextView = findViewById(R.id.player_name)
        val addPlayerButton: Button = findViewById(R.id.register_button)
        var flagName: Boolean = false
        var flagPosition: Boolean = false
        var flagImg: Boolean = false
        imageView = findViewById(R.id.player_img)

        fun updateButton(flag1: Boolean, flag2: Boolean, flag3: Boolean, button: Button) {
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

        uploadButton.setOnClickListener {
            openGallery()
            flagImg = true
            updateButton(flagName, flagPosition, flagImg, addPlayerButton)
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