package uz.umarov.fcneftchi.data.mapper

import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.local.entity.BookmarkArticleEntity
import uz.umarov.fcneftchi.data.local.entity.BookmarkPlayerEntity
import uz.umarov.fcneftchi.data.local.entity.BookmarkVideoEntity
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition
import uz.umarov.fcneftchi.data.model.Video

fun NewsArticle.toBookmarkEntity(addedAt: Long): BookmarkArticleEntity = BookmarkArticleEntity(
    url = url,
    articleId = id,
    title = title,
    imageUrl = imageUrl,
    date = date,
    description = description,
    category = category,
    addedAt = addedAt
)

fun BookmarkArticleEntity.toArticle(): NewsArticle = NewsArticle(
    id = articleId,
    title = title,
    imageUrl = imageUrl,
    date = date,
    content = "",
    description = description,
    category = category,
    url = url
)

fun Video.toBookmarkEntity(addedAt: Long): BookmarkVideoEntity = BookmarkVideoEntity(
    id = id,
    title = title,
    thumbnailUrl = thumbnailUrl,
    videoUrl = videoUrl,
    category = category,
    date = date,
    duration = duration,
    addedAt = addedAt
)

fun BookmarkVideoEntity.toVideo(): Video = Video(
    id = id,
    title = title,
    thumbnailUrl = thumbnailUrl,
    videoUrl = videoUrl,
    category = category,
    date = date,
    duration = duration
)

fun Player.toBookmarkEntity(addedAt: Long): BookmarkPlayerEntity = BookmarkPlayerEntity(
    id = id,
    name = name,
    number = number,
    positionId = position.positionId,
    photoUrl = (imageUrl as? String),
    nationality = nationality,
    addedAt = addedAt
)

fun BookmarkPlayerEntity.toPlayer(): Player = Player(
    id = id,
    name = name,
    number = number,
    position = PlayerPosition.fromId(positionId),
    imageUrl = photoUrl ?: R.drawable.player_placeholder_inset,
    nationality = nationality
)
