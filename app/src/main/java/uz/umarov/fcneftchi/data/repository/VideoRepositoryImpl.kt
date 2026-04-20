package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.util.DateFormatter
import java.util.regex.Pattern
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val apiService: PflApiService
) : VideoRepository {

    override fun getVideos(): Flow<List<Video>> = flow {
        coroutineScope {
            val newsResponse = apiService.getNews(ClubConfig.CLUB_ID)
            val videoNewsItems = newsResponse.data.list
                .filter { it.category.id == ClubConfig.VIDEO_CATEGORY_ID }

            val videos = videoNewsItems.map { newsItem ->
                async {
                    try {
                        val detail = apiService.getNewsDetail(newsItem.contents.url)
                        val youtubeUrl = extractYouTubeUrl(
                            detail.data.text.firstOrNull()?.value
                        )
                        youtubeUrl?.let {
                            Video(
                                id = newsItem.id.toString(),
                                title = newsItem.contents.title,
                                thumbnailUrl = newsItem.image ?: "",
                                videoUrl = it,
                                category = newsItem.category.title,
                                date = DateFormatter.formatArticleDateOrRaw(newsItem.publicDate),
                                duration = ""
                            )
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
            }.mapNotNull { it.await() }

            emit(videos)
        }
    }

    private fun extractYouTubeUrl(htmlContent: String?): String? {
        if (htmlContent == null) return null
        val matcher = SRC_PATTERN.matcher(htmlContent)
        return if (matcher.find()) matcher.group(1) else null
    }

    private companion object {
        val SRC_PATTERN: Pattern = Pattern.compile("src=\"(.*?)\"")
    }
}
