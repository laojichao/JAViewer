package io.github.javiewer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room 实体，表示收藏女优的持久化数据。
 *
 * 与 [io.github.javiewer.adapter.item.Actress] 对应，使用女优名称 [name] 作为主键。
 *
 * **已知限制**：使用 [name] 作为主键意味着同名女优只能保存一条记录，
 * 后收藏的会覆盖先收藏的（OnConflictStrategy.REPLACE）。
 * 这在实际使用中极少发生（日本 AV 界艺名通常唯一），
 * 但如需支持同名女优，需迁移到自增 ID + 唯一索引方案（Room 版本 1→2）。
 *
 * @property name 女优名称，主键
 * @property imageUrl 头像图片 URL
 * @property link 详情页 URL
 */
@Entity(tableName = "favorite_actresses")
data class FavoriteActressEntity(
    @PrimaryKey val name: String,
    val imageUrl: String,
    val link: String?
)
