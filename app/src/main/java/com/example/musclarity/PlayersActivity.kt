/*package com.example.musclarity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import org.apache.commons.math3.stat.descriptive.summary.Product

class PlayersActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("Jugadores")

    private lateinit var tableLayout: TableLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players)

        val backButton: ImageView = findViewById(R.id.back_button)
        val addPlayerButton: Button = findViewById(R.id.add_player_button)

        coleccion.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot)
                {
                    val nombre = document.getString("Nombre")
                    val pos = document.getString("Posición")

                    Log.d("FirebaseData", "Nombre: $nombre, Posición: $pos")

                    if (nombre != null && pos != null)
                    {
                        val row = LayoutInflater.from(this).inflate(R.layout.player_row, tableLayout, false) as TableRow
                        val playerName = row.findViewById<TextView>(R.id.player_name)
                        val playerPosition = row.findViewById<TextView>(R.id.player_position)

                        playerName.text = nombre
                        playerPosition.text = pos

                        Log.d("FirebaseData 2", "Nombre: ${playerName.text}, Posición: ${playerPosition.text}")


                        tableLayout.addView(row)
                    }


                }
                //adapter.setData((listaJugadores))
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseData", "Error al obtener datos", e)
            }

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

 */

package com.example.musclarity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//ata class Player(val name: String, val position: String)

class PlayersActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()


    private lateinit var recyclerView: RecyclerView
    private lateinit var playerAdapter: PlayerAdapter
    private val playerList = mutableListOf<Player>()


    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val email = user.email

            if (email != null) {
                val collectionName = "Jugadores - $email"
                val coleccion = db.collection(collectionName)

                coleccion.get()
                    .addOnSuccessListener { querySnapshot ->
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
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseData", "Error al obtener datos", e)
                    }
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players)

        recyclerView = findViewById(R.id.recycler_view_players)
        playerAdapter = PlayerAdapter(this, playerList)
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
}
