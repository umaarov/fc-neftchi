package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import uz.umarov.fcneftchi.domain.model.HomeFeed
import javax.inject.Inject

class GetHomeFeedUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(): Flow<HomeFeed> = combine(
        repository.getNextMatch(),
        repository.getLastMatch(),
        repository.getNews(),
        repository.getLeagueTable(seasonId = ClubConfig.HOME_STANDINGS_SEASON_ID),
        repository.getVideos()
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
