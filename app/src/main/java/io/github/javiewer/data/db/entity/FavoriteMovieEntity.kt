package io.github.javiewer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room 实体，表示收藏影片的持久化数据。
 *
 * 与 [io.github.javiewer.adapter.item.Movie] 对应，使用影片编号 [code] 作为主键。
 *
 * @property code 影片编号，主键
 * @property title 影片标题
 * @property coverUrl 封面图片 URL
 * @property date 发布日期
 * @property hot 是否热门
 * @property link 详情页 URL
 */
@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val code: String,
    val title: String,
    val coverUrl: String,
    val date: String,
    val hot: Boolean,
    val link: String?
)
