package uz.umarov.fcneftchi.data.mapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.local.entity.BookmarkArticleEntity
import uz.umarov.fcneftchi.data.local.entity.BookmarkPlayerEntity
import uz.umarov.fcneftchi.data.local.entity.BookmarkVideoEntity
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition
import uz.umarov.fcneftchi.data.model.Video

class BookmarkMappersTest {

    @Test
    fun `article entity round-trip drops the transient content field`() {
        val article = NewsArticle(
            id = "42",
            title = "Neftchi win",
            imageUrl = "https://cdn/n.png",
            date = "10 April 2026",
            content = "<div>body html</div>",
            description = "summary",
            category = "match report",
            url = "https://example.com/a",
        )

        val entity = article.toBookmarkEntity(addedAt = 1_000)
        val restored = entity.toArticle()

        assertThat(entity).isEqualTo(
            BookmarkArticleEntity(
                url = "https://example.com/a",
                articleId = "42",
                title = "Neftchi win",
                imageUrl = "https://cdn/n.png",
                date = "10 April 2026",
                description = "summary",
                category = "match report",
                addedAt = 1_000,
            )
        )
        assertThat(restored.content).isEmpty()
        assertThat(restored).isEqualTo(article.copy(content = ""))
    }

    @Test
    fun `video entity round-trips losslessly`() {
        val video = Video(
            id = "v1",
            title = "Best goals",
            thumbnailUrl = "https://cdn/t.jpg",
            videoUrl = "https://cdn/v.mp4",
            category = "highlights",
            date = "2026-04-10",
            duration = "3:21",
        )

        val entity = video.toBookmarkEntity(addedAt = 42)
        val restored = entity.toVideo()

        assertThat(entity).isEqualTo(
            BookmarkVideoEntity(
                id = "v1",
                title = "Best goals",
                thumbnailUrl = "https://cdn/t.jpg",
                videoUrl = "https://cdn/v.mp4",
                category = "highlights",
                date = "2026-04-10",
                duration = "3:21",
                addedAt = 42,
            )
        )
        assertThat(restored).isEqualTo(video)
    }

    @Test
    fun `player entity stores photo url when imageUrl is a String`() {
        val player = Player(
            id = 7,
            name = "Odil Ahmedov",
            number = 10,
            position = PlayerPosition.MIDFIELDER,
            imageUrl = "https://cdn/odil.png",
            nationality = "Uzbekistan",
        )

        val entity = player.toBookmarkEntity(addedAt = 99)

        assertThat(entity.photoUrl).isEqualTo("https://cdn/odil.png")
        assertThat(entity.positionId).isEqualTo(PlayerPosition.MIDFIELDER.positionId)
    }

    @Test
    fun `player entity stores null photoUrl when imageUrl is a drawable Int`() {
        val player = Player(
            id = 7,
            name = "Odil Ahmedov",
            number = 10,
            position = PlayerPosition.MIDFIELDER,
            imageUrl = R.drawable.player_placeholder_inset,
            nationality = "Uzbekistan",
        )

        val entity = player.toBookmarkEntity(addedAt = 99)

        assertThat(entity.photoUrl).isNull()
    }

    @Test
    fun `player entity toPlayer falls back to placeholder when photoUrl is null`() {
        val entity = BookmarkPlayerEntity(
            id = 7,
            name = "Odil Ahmedov",
            number = 10,
            positionId = PlayerPosition.MIDFIELDER.positionId,
            photoUrl = null,
            nationality = "Uzbekistan",
            addedAt = 1,
        )

        val player = entity.toPlayer()

        assertThat(player.position).isEqualTo(PlayerPosition.MIDFIELDER)
        assertThat(player.imageUrl).isEqualTo(R.drawable.player_placeholder_inset)
    }

    @Test
    fun `player entity toPlayer preserves photoUrl as String imageUrl`() {
        val entity = BookmarkPlayerEntity(
            id = 7,
            name = "X",
            number = 1,
            positionId = PlayerPosition.GOALKEEPER.positionId,
            photoUrl = "https://cdn/x.png",
            nationality = "Uzbekistan",
            addedAt = 1,
        )

        val player = entity.toPlayer()

        assertThat(player.imageUrl).isEqualTo("https://cdn/x.png")
    }
}
