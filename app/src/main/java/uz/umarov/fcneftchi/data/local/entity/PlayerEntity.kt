package uz.umarov.fcneftchi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val number: Int,
    val position: String,
    val photoUrl: String?,
    val nationality: String,
    val cachedAt: Long
)
