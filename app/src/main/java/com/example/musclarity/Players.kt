package com.example.musclarity

import android.media.Image
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registered_users_table")
class Players(
    val completeName: String,
    val position: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
