package com.example.musclarity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SquadActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_squad)
        firebaseAuth = Firebase.auth

        val spinner: Spinner = findViewById(R.id.spinner)
        val logoutButton: ImageView = findViewById(R.id.logout_button)
        val graphButton: ImageView = findViewById(R.id.graph_button)
        val gk = findViewById<ImageView>(R.id.GK)
        val dfd = findViewById<ImageView>(R.id.DFD)
        val dfc1 = findViewById<ImageView>(R.id.DFC1)
        val dfc2 = findViewById<ImageView>(R.id.DFC2)
        val dfi = findViewById<ImageView>(R.id.DFI)
        val mc1 = findViewById<ImageView>(R.id.MC1)
        val mc2 = findViewById<ImageView>(R.id.MC2)
        val mco = findViewById<ImageView>(R.id.DC2_MCO)
        val mi = findViewById<ImageView>(R.id.MI)
        val md = findViewById<ImageView>(R.id.MD)
        val dc = findViewById<ImageView>(R.id.DC1)

        graphButton.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        gk.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "gk")
            }
            startActivity(intent)
        }

        dfd.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "dfd")
            }
            startActivity(intent)
        }

        dfc1.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "dfc1")
            }
            startActivity(intent)
        }

        dfc2.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "dfc2")
            }
            startActivity(intent)
        }

        dfi.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "dfi")
            }
            startActivity(intent)
        }

        mc1.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "mc1")
            }
            startActivity(intent)
        }

        mc2.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "mc2")
            }
            startActivity(intent)
        }

        md.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "md")
            }
            startActivity(intent)
        }

        mi.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "mi")
            }
            startActivity(intent)
        }

        mco.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "mco")
            }
            startActivity(intent)
        }

        dc.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putExtra("posicion", "dc")
            }
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            showLogOutDialog()
        }

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_items,
            R.layout.spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(R.layout.spinner_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val formation = parent.getItemAtPosition(position).toString()

                // Get the current layout params of the players
                val layoutParamsMC1 = mc1.layoutParams as ConstraintLayout.LayoutParams
                val layoutParamsMC2 = mc2.layoutParams as ConstraintLayout.LayoutParams
                val layoutParamsMCO = mco.layoutParams as ConstraintLayout.LayoutParams
                val layoutParamsMI = mi.layoutParams as ConstraintLayout.LayoutParams
                val layoutParamsMD = md.layoutParams as ConstraintLayout.LayoutParams
                val layoutParamsDC = dc.layoutParams as ConstraintLayout.LayoutParams

                // Set the horizontal bias based on the selected item
                if (formation == "4–4–2") {
                    layoutParamsMC1.verticalBias = 0.52f
                    layoutParamsMC1.horizontalBias = 0.67f

                    layoutParamsMC2.verticalBias = 0.52f
                    layoutParamsMC2.horizontalBias = 0.33f

                    layoutParamsMCO.verticalBias = 0.28f
                    layoutParamsMCO.horizontalBias = 0.33f

                    layoutParamsMI.verticalBias = 0.46f

                    layoutParamsMD.verticalBias = 0.46f

                    layoutParamsDC.verticalBias = 0.28f
                    layoutParamsDC.horizontalBias = 0.67f

                } else if (formation == "4–3–3") {
                    layoutParamsMC1.verticalBias = 0.56f
                    layoutParamsMC1.horizontalBias = 0.72f

                    layoutParamsMC2.verticalBias = 0.56f
                    layoutParamsMC2.horizontalBias = 0.28f

                    layoutParamsMCO.verticalBias = 0.44f
                    layoutParamsMCO.horizontalBias = 0.50f

                    layoutParamsMI.verticalBias = 0.35f

                    layoutParamsMD.verticalBias = 0.35f

                    layoutParamsDC.verticalBias = 0.28f
                    layoutParamsDC.horizontalBias = 0.50f
                }
                mc1.layoutParams = layoutParamsMC1
                mc2.layoutParams = layoutParamsMC2
                mco.layoutParams = layoutParamsMCO
                mi.layoutParams = layoutParamsMI
                md.layoutParams = layoutParamsMD
                dc.layoutParams = layoutParamsDC
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }

        val posicion = intent.getStringExtra("posicion2")
        if (!posicion.isNullOrBlank()) {
            Toast.makeText(baseContext, posicion, Toast.LENGTH_SHORT).show()
        }
    }
    private fun logOut () {
        firebaseAuth.signOut()

        // Go to the MainActivity when the logOut is clicked
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showLogOutDialog() {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.sign_out_banner, null)
        val btnNO: TextView = dialogView.findViewById(R.id.btnNO)
        val btnYES: Button = dialogView.findViewById(R.id.btnYES)

        /*
        // Create the AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        builder.setPositiveButton("YES") { dialog, _ ->
            dialog.dismiss()
            logOut()
        }
        builder.setNegativeButton("NO") { dialog, _ ->
            dialog.dismiss()
        }
         */

        // Create AlertDialog with custom buttons layout
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Optional: Make dialog non-cancelable by clicking outside

        // Create and show the dialog
        val dialog = builder.create()
        btnYES.setOnClickListener {
            dialog.dismiss()
            logOut()
        }

        btnNO.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        // Optionally set dialog window background color
        dialog.window?.setBackgroundDrawableResource(R.color.myBackgroundColor)

        /*
        // Show the dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()


        // Retrieve the positive button and apply the custom style
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.logoColor))
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.myBackgroundColor))

        // Retrieve the negative button and apply the custom style
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negativeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green_pitch))
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.logoColor))


        dialog.window?.setBackgroundDrawableResource(R.color.myBackgroundColor)

         */
    }
}

