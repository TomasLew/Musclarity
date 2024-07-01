package com.example.musclarity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PlayersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players)

        val backButton: ImageView = findViewById(R.id.back_button)
        val addPlayerButton: Button = findViewById(R.id.add_player_button)

        addPlayerButton.setOnClickListener { val intent1 = Intent(this, PlayersActivity2::class.java)
            startActivity(intent1);
        }

        // Move to squad activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, SquadActivity::class.java)
            startActivity(intent1);
        }


    }
}