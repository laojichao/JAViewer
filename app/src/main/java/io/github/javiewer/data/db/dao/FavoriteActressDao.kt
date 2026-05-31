package io.github.javiewer.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.javiewer.data.db.entity.FavoriteActressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteActressDao {

    @Query("SELECT * FROM favorite_actresses ORDER BY rowid DESC")
    fun getAll(): Flow<List<FavoriteActressEntity>>

    @Query("SELECT * FROM favorite_actresses ORDER BY rowid DESC")
    suspend fun getAllSync(): List<FavoriteActressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(actress: FavoriteActressEntity)

    @Delete
    suspend fun delete(actress: FavoriteActressEntity)

    @Query("DELETE FROM favorite_actresses WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_actresses WHERE name = :name)")
    suspend fun isStarred(name: String): Boolean
}
