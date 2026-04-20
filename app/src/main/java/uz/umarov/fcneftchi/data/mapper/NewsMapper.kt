package uz.umarov.fcneftchi.data.mapper

import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.NewsDetailData
import uz.umarov.fcneftchi.data.model.NewsListItem
import uz.umarov.fcneftchi.util.DateFormatter

private const val PLACEHOLDER_IMAGE =
    "https://placehold.co/600x400/CCCCCC/FFFFFF?text=No+Image"

fun NewsListItem.toNewsArticle(): NewsArticle = NewsArticle(
    id = id.toString(),
    title = contents.title,
    imageUrl = image ?: PLACEHOLDER_IMAGE,
    date = DateFormatter.formatArticleDateOrRaw(publicDate),
    content = contents.description ?: "",
    url = contents.url,
    description = contents.description ?: "",
    category = category.title
)

fun NewsDetailData.toNewsArticle(url: String): NewsArticle {
    val fullContentHtml = text.joinToString(separator = "") { textItem ->
        textItem.value?.let { "<div>$it</div>" } ?: ""
    }
    return NewsArticle(
        id = id.toString(),
        title = title,
        imageUrl = image ?: PLACEHOLDER_IMAGE,
        date = DateFormatter.formatArticleDateOrRaw(publicDate),
        content = fullContentHtml,
        url = url,
        description = description ?: "",
        category = category?.title ?: ""
    )
}
