package uz.umarov.fcneftchi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_players")
data class BookmarkPlayerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val number: Int,
    val positionId: Int,
    val photoUrl: String?,
    val nationality: String,
    val addedAt: Long,
)
