package io.github.javiewer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val code: String,
    val title: String,
    val coverUrl: String,
    val date: String,
    val hot: Boolean,
    val link: String?
)
