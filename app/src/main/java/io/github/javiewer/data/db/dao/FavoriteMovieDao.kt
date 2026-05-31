package io.github.javiewer.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.javiewer.data.db.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {

    @Query("SELECT * FROM favorite_movies ORDER BY rowid DESC")
    fun getAll(): Flow<List<FavoriteMovieEntity>>

    @Query("SELECT * FROM favorite_movies ORDER BY rowid DESC")
    suspend fun getAllSync(): List<FavoriteMovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: FavoriteMovieEntity)

    @Delete
    suspend fun delete(movie: FavoriteMovieEntity)

    @Query("DELETE FROM favorite_movies WHERE code = :code")
    suspend fun deleteByCode(code: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE code = :code)")
    suspend fun isStarred(code: String): Boolean
}
