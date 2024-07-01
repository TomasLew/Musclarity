package com.example.musclarity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val registerButton: Button = findViewById(R.id.Register_button)
        val loginButton: TextView = findViewById(R.id.logIn_button)

        loginButton.setOnClickListener {
            // Start the LoginActivity when the loginButton is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        registerButton.setOnClickListener {
            // Start the RegisterActivity when the registerButton is clicked
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
