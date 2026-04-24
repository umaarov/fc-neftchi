package uz.umarov.fcneftchi.data.mapper

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Locale
import java.util.TimeZone
import uz.umarov.fcneftchi.data.model.NewsCategory
import uz.umarov.fcneftchi.data.model.NewsContents
import uz.umarov.fcneftchi.data.model.NewsDetailData
import uz.umarov.fcneftchi.data.model.NewsListItem
import uz.umarov.fcneftchi.data.model.NewsTextItem

class NewsMapperTest {

    private lateinit var originalLocale: Locale
    private lateinit var originalTimeZone: TimeZone

    @Before
    fun pinLocaleAndTimeZone() {
        originalLocale = Locale.getDefault()
        originalTimeZone = TimeZone.getDefault()
        Locale.setDefault(Locale.ENGLISH)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun restoreLocaleAndTimeZone() {
        Locale.setDefault(originalLocale)
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun `NewsListItem toNewsArticle maps every field`() {
        val item = NewsListItem(
            id = 42,
            image = "https://cdn.example.com/hero.jpg",
            publicDate = "2026-04-10T10:30:00Z",
            contents = NewsContents(
                title = "Neftchi win 3-0",
                description = "Match report...",
                url = "https://example.com/neftchi-win"
            ),
            category = NewsCategory(id = 1, title = "Match report")
        )

        val article = item.toNewsArticle()

        assertThat(article.id).isEqualTo("42")
        assertThat(article.title).isEqualTo("Neftchi win 3-0")
        assertThat(article.imageUrl).isEqualTo("https://cdn.example.com/hero.jpg")
        assertThat(article.content).isEqualTo("Match report...")
        assertThat(article.description).isEqualTo("Match report...")
        assertThat(article.url).isEqualTo("https://example.com/neftchi-win")
        assertThat(article.category).isEqualTo("Match report")
        assertThat(article.date).isEqualTo("10 April 2026")
    }

    @Test
    fun `NewsListItem toNewsArticle falls back to placeholder when image is null`() {
        val item = NewsListItem(
            id = 1,
            image = null,
            publicDate = "2026-04-10T10:30:00Z",
            contents = NewsContents("Title", null, "https://example.com/x"),
            category = NewsCategory(1, "News")
        )

        val article = item.toNewsArticle()

        assertThat(article.imageUrl).contains("placehold.co")
    }

    @Test
    fun `NewsListItem toNewsArticle maps null description to empty string`() {
        val item = NewsListItem(
            id = 1,
            image = "https://cdn.example.com/x.jpg",
            publicDate = "2026-04-10T10:30:00Z",
            contents = NewsContents("Title", null, "https://example.com/x"),
            category = NewsCategory(1, "News")
        )

        val article = item.toNewsArticle()

        assertThat(article.description).isEmpty()
        assertThat(article.content).isEmpty()
    }

    @Test
    fun `NewsListItem toNewsArticle passes through raw date when unparseable`() {
        val item = NewsListItem(
            id = 1,
            image = null,
            publicDate = "not-a-date",
            contents = NewsContents("Title", null, "https://example.com/x"),
            category = NewsCategory(1, "News")
        )

        val article = item.toNewsArticle()

        assertThat(article.date).isEqualTo("not-a-date")
    }

    @Test
    fun `NewsDetailData toNewsArticle wraps each text item in a div`() {
        val detail = NewsDetailData(
            id = 99,
            image = "https://cdn.example.com/detail.jpg",
            publicDate = "2026-04-10T10:30:00Z",
            title = "Full story",
            description = "Short summary",
            category = NewsCategory(2, "Long read"),
            text = listOf(
                NewsTextItem(type = "paragraph", value = "First paragraph."),
                NewsTextItem(type = "paragraph", value = "Second paragraph."),
                NewsTextItem(type = "paragraph", value = null),
            )
        )

        val article = detail.toNewsArticle("https://example.com/full-story")

        assertThat(article.id).isEqualTo("99")
        assertThat(article.title).isEqualTo("Full story")
        assertThat(article.description).isEqualTo("Short summary")
        assertThat(article.url).isEqualTo("https://example.com/full-story")
        assertThat(article.category).isEqualTo("Long read")
        assertThat(article.content)
            .isEqualTo("<div>First paragraph.</div><div>Second paragraph.</div>")
    }

    @Test
    fun `NewsDetailData toNewsArticle handles null category and description`() {
        val detail = NewsDetailData(
            id = 1,
            image = null,
            publicDate = "2026-04-10T10:30:00Z",
            title = "T",
            description = null,
            category = null,
            text = emptyList()
        )

        val article = detail.toNewsArticle("https://example.com/x")

        assertThat(article.category).isEmpty()
        assertThat(article.description).isEmpty()
        assertThat(article.content).isEmpty()
        assertThat(article.imageUrl).contains("placehold.co")
    }
}
