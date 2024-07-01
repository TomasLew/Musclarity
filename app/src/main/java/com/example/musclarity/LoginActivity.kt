package com.example.musclarity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()

        val backButton: ImageView = findViewById(R.id.back_button)
        val loginButton: Button = findViewById(R.id.login_button)
        val textEmail: EditText = findViewById(R.id.username)
        val textPass: EditText = findViewById(R.id.password)
        val logoColor = ContextCompat.getColor(this, R.color.logoColor)
        val logoColorTransparente = ContextCompat.getColor(this, R.color.logoColor_transparente)

        fun updateButtonColor(button: Button) {
            if (button.isEnabled) {
                // Set background color when button is enabled
                button.setBackgroundColor(logoColor)// Change to desired color
            } else {
                // Set background color when button is disabled
                button.setBackgroundColor(logoColorTransparente) // Change to desired color
            }
        }

        // Move to main activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, MainActivity::class.java)
            startActivity(intent1);
        }

        // Text change listener for editText1
        textEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable the button if both EditText fields are not empty
                loginButton.isEnabled = !s.isNullOrBlank() && !textPass.text.isNullOrBlank()
                updateButtonColor(loginButton)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Text change listener for editText2
        textPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable the button if both EditText fields are not empty
                loginButton.isEnabled = !s.isNullOrBlank() && !textEmail.text.isNullOrBlank()
                updateButtonColor(loginButton)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Move to squad activity on click
        loginButton.setOnClickListener {
            val intent = Intent(this, SquadActivity::class.java)
            startActivity(intent);
        }

    }
}