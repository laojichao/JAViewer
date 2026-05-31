package io.github.javiewer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_actresses")
data class FavoriteActressEntity(
    @PrimaryKey val name: String,
    val imageUrl: String,
    val link: String?
)
