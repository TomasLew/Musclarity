package com.example.musclarity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  authStateListener: FirebaseAuth.AuthStateListener

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
        val forgotPassword: TextView = findViewById(R.id.olvide_contraseña)

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

        /*// Move to squad activity on click
        loginButton.setOnClickListener {
            val intent = Intent(this, SquadActivity::class.java)
            startActivity(intent);
        }
         */

        firebaseAuth = Firebase.auth
        loginButton.setOnClickListener {
            signIn(textEmail.text.toString(), textPass.text.toString())
            //Toast.makeText(this, "Username: ${textEmail.text.toString()}, Password: ${textPass.text.toString()}", Toast.LENGTH_SHORT).show()
        }

        forgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                //Toast.makeText(baseContext, user?.uid.toString(), Toast.LENGTH_SHORT).show()
                // Acá vamos a ir a la segunda activity
                val intent = Intent(this, SquadActivity::class.java)
                startActivity(intent);
            } else {
                val toast = Toast.makeText(baseContext, "Error de Email y/o Contraseña", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }

    private fun sendPasswordReset (email : String) {
        if (email.isNotBlank()) {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    //Toast.makeText(baseContext, "", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Error, the process couldn't be done",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        else {
            Toast.makeText(
                baseContext,
                "Error, the email is missing",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun showForgotPasswordDialog() {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.forgot_password_banner, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.email_pass)

        // Create the AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton("OK") { dialog, _ ->
            val email = emailEditText.text.toString()
            sendPasswordReset(email)
            dialog.dismiss()
        }

        // Show the dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()

        // Retrieve the positive button and apply the custom style
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.logoColor))
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.myBackgroundColor))

        dialog.window?.setBackgroundDrawableResource(R.color.myBackgroundColor)
    }
}