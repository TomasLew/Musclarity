package com.example.musclarity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musclarity.R.id.options
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//ata class Player(val name: String, val position: String)

class PlayersActivity : AppCompatActivity(), PlayerAdapter.OnItemClickListener {
    lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var recyclerView: RecyclerView
    private lateinit var playerAdapter: PlayerAdapter
    private val playerList = mutableListOf<Player>()
    private var dataLoaded = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val email = user.email

            if (email != null) {
                val collectionName = "Jugadores - $email"
                val coleccion = db.collection(collectionName)

                coleccion.get()
                    .addOnSuccessListener { querySnapshot ->
                        playerList.clear()
                        for (document in querySnapshot) {
                            val nombre = document.getString("Nombre")
                            val pos = document.getString("Posición")
                            val F0 = document.getLong("F0")?.toInt()

                            Log.d("FirebaseData", "Nombre: $nombre, Posición: $pos")

                            if (nombre != null && pos != null) {
                                playerList.add(Player(nombre, pos))
                            }
                        }
                        playerAdapter.notifyDataSetChanged()
                        dataLoaded = true

                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseData", "Error al obtener datos", e)
                    }
            }
        }

        playerAdapter = PlayerAdapter(this,this, playerList)
        recyclerView = findViewById(R.id.recycler_view_players)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = playerAdapter


        val backButton: ImageView = findViewById(R.id.back_button)
        val addPlayerButton: Button = findViewById(R.id.add_player_button)

        addPlayerButton.setOnClickListener {
            val intent = Intent(this, PlayersActivity2::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, SquadActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onItemClick(myModel: Player) {
        if (dataLoaded && playerList.size > 0) {
            Log.d("PlayersActivity", "Clicked on player: ${myModel.name}, Position: ${myModel.position}")
            val intent_edit = Intent(this, PlayersActivity3::class.java)
            intent_edit.putExtra("player_name", myModel.name.toString())
            intent_edit.putExtra("player_position", myModel.position.toString())
            startActivity(intent_edit)
        } else{
            Log.d("PlayersActivity", "Data is not loaded yet, ignoring click.")
        }

    }

}


