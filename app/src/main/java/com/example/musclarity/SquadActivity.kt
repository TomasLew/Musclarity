package com.example.musclarity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class SquadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_squad)

        val spinner: Spinner = findViewById(R.id.spinner)
        val logoutButton: ImageView = findViewById(R.id.logout_button)
        val graphButton: ImageView = findViewById(R.id.graph_button)
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

        mco.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            // Start the LoginActivity when the loginButton is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
    }
}

