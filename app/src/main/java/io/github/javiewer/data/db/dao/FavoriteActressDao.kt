package io.github.javiewer.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.javiewer.data.db.entity.FavoriteActressEntity
import kotlinx.coroutines.flow.Flow

/**
 * 收藏女优数据访问对象，提供收藏女优的 CRUD 操作。
 *
 * [getAll] 返回响应式 Flow，适合 Compose/LiveData 观察；
 * [getAllSync] 返回一次性列表，适合同步场景。
 */
@Dao
interface FavoriteActressDao {

    /**
     * 获取所有收藏女优，按 rowid 降序排列（最新收藏在前）。
     *
     * @return 响应式数据流，数据库变更时自动通知
     */
    @Query("SELECT * FROM favorite_actresses ORDER BY rowid DESC")
    fun getAll(): Flow<List<FavoriteActressEntity>>

    /**
     * 同步获取所有收藏女优列表。
     *
     * @return 当前时刻的收藏女优列表
     */
    @Query("SELECT * FROM favorite_actresses ORDER BY rowid DESC")
    suspend fun getAllSync(): List<FavoriteActressEntity>

    /**
     * 插入或替换收藏女优。
     *
     * @param actress 要插入的女优实体
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(actress: FavoriteActressEntity)

    /**
     * 删除指定收藏女优。
     *
     * @param actress 要删除的女优实体
     */
    @Delete
    suspend fun delete(actress: FavoriteActressEntity)

    /**
     * 根据女优名称删除收藏。
     *
     * @param name 女优名称
     */
    @Query("DELETE FROM favorite_actresses WHERE name = :name")
    suspend fun deleteByName(name: String)

    /**
     * 检查女优是否已收藏。
     *
     * @param name 女优名称
     * @return true 表示已收藏
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_actresses WHERE name = :name)")
    suspend fun isStarred(name: String): Boolean
}
