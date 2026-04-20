package uz.umarov.fcneftchi.domain.model

import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Video

data class HomeFeed(
    val nextMatch: Match?,
    val lastMatch: Match?,
    val news: List<NewsArticle>,
    val standings: List<LeagueStanding>,
    val videos: List<Video>
)
