package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.Video

interface VideoRepository {
    fun getVideos(): Flow<List<Video>>
}
