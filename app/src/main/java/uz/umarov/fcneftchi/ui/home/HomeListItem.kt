package uz.umarov.fcneftchi.ui.home

import androidx.annotation.StringRes
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Video

sealed class HomeListItem {
    data class HeroCarouselItem(val articles: List<NewsArticle>) : HomeListItem()
    data class NextMatchItem(val match: Match) : HomeListItem()
    data class LastResultItem(val match: Match) : HomeListItem()
    data class FeaturedVideoItem(val video: Video) : HomeListItem()
    data class HeaderItem(@StringRes val titleRes: Int, val destinationId: Int) : HomeListItem()
    data class NewsCarouselItem(val articles: List<NewsArticle>) : HomeListItem()
    data class StandingsItem(val standings: List<LeagueStanding>) : HomeListItem()
}
