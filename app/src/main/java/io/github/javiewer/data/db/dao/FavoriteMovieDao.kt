package io.github.javiewer.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.javiewer.data.db.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow

/**
 * 收藏影片数据访问对象，提供收藏影片的 CRUD 操作。
 *
 * [getAll] 返回响应式 Flow，适合 Compose/LiveData 观察；
 * [getAllSync] 返回一次性列表，适合同步场景。
 */
@Dao
interface FavoriteMovieDao {

    /**
     * 获取所有收藏影片，按 rowid 降序排列（最新收藏在前）。
     *
     * @return 响应式数据流，数据库变更时自动通知
     */
    @Query("SELECT * FROM favorite_movies ORDER BY rowid DESC")
    fun getAll(): Flow<List<FavoriteMovieEntity>>

    /**
     * 同步获取所有收藏影片列表。
     *
     * @return 当前时刻的收藏影片列表
     */
    @Query("SELECT * FROM favorite_movies ORDER BY rowid DESC")
    suspend fun getAllSync(): List<FavoriteMovieEntity>

    /**
     * 插入或替换收藏影片。
     *
     * @param movie 要插入的影片实体
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: FavoriteMovieEntity)

    /**
     * 删除指定收藏影片。
     *
     * @param movie 要删除的影片实体
     */
    @Delete
    suspend fun delete(movie: FavoriteMovieEntity)

    /**
     * 根据影片编号删除收藏。
     *
     * @param code 影片编号
     */
    @Query("DELETE FROM favorite_movies WHERE code = :code")
    suspend fun deleteByCode(code: String)

    /**
     * 检查影片是否已收藏。
     *
     * @param code 影片编号
     * @return true 表示已收藏
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE code = :code)")
    suspend fun isStarred(code: String): Boolean
}
