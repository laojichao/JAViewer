package io.github.javiewer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room 实体，表示收藏女优的持久化数据。
 *
 * 与 [io.github.javiewer.adapter.item.Actress] 对应，使用女优名称 [name] 作为主键。
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
