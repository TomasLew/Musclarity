package com.example.musclarity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PlayerAdapter(private val itemClickListener: OnItemClickListener, private val context: Context, private val playerList: List<Player>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

        inner class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val playerName: TextView = view.findViewById(R.id.player_name)
            val playerPosition: TextView = view.findViewById(R.id.player_position)

            init {
                view.setOnClickListener{
                    val position = adapterPosition
                    if (position!= RecyclerView.NO_POSITION){
                        val myModel = playerList[position]
                        itemClickListener.onItemClick(myModel)
                    }
                }
            }

        }
        interface OnItemClickListener{
            fun onItemClick(myModel: Player)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.player_row, parent, false)
            return PlayerViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
            val player = playerList[position]
            holder.playerName.text = player.name
            holder.playerPosition.text = player.position
        }

        override fun getItemCount(): Int {
            return playerList.size
        }
}
