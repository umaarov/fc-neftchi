package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.repository.MatchRepository
import uz.umarov.fcneftchi.data.repository.NewsRepository
import uz.umarov.fcneftchi.data.repository.StandingsRepository
import uz.umarov.fcneftchi.data.repository.VideoRepository
import uz.umarov.fcneftchi.domain.model.HomeFeed
import javax.inject.Inject

class GetHomeFeedUseCase @Inject constructor(
    private val matchRepository: MatchRepository,
    private val newsRepository: NewsRepository,
    private val standingsRepository: StandingsRepository,
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): Flow<HomeFeed> = combine(
        matchRepository.getNextMatch(),
        matchRepository.getLastMatch(),
        newsRepository.getNews(),
        standingsRepository.getLeagueTable(seasonId = ClubConfig.HOME_STANDINGS_SEASON_ID),
        videoRepository.getVideos()
    ) { nextMatch, lastMatch, news, standings, videos ->
        HomeFeed(
            nextMatch = nextMatch,
            lastMatch = lastMatch,
            news = news,
            standings = standings,
            videos = videos
        )
    }
}
