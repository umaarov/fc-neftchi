package uz.umarov.fcneftchi.util

object YouTubeUrlParser {
    private val regExp =
        "^(?:https?://)?(?:www\\.)?(?:m\\.)?(?:youtu\\.be/|youtube\\.com/(?:embed/|v/|watch\\?v=|watch\\?.+&v=))([\\w-]{11})(?:\\S+)?$".toRegex()

    fun extractVideoId(videoUrl: String): String? {
        val matcher = regExp.find(videoUrl)
        return matcher?.groups?.get(1)?.value
    }
}