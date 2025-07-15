package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.MatchStatus
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.Team

class MockNeftchiRepository : NeftchiRepository {

    private val neftchi = Team(
        1,
        "Neftchi Fergana",
        "https://upload.wikimedia.org/wikipedia/en/c/c2/FC_Neftchi_Farg%27ona_logo.png"
    )
    private val pakhtakor = Team(
        2,
        "Pakhtakor",
        "https://upload.wikimedia.org/wikipedia/en/thumb/1/10/FC_Pakhtakor_Tashkent_logo.svg/1200px-FC_Pakhtakor_Tashkent_logo.svg.png"
    )
    private val nasaf = Team(
        3,
        "Nasaf",
        "https://upload.wikimedia.org/wikipedia/en/thumb/f/f3/FC_Nasaf_logo.svg/1200px-FC_Nasaf_logo.svg.png"
    )
    private val allNews = listOf(
        NewsArticle(
            "news1",
            "Neftchi signs new striker!",
            "https://placehold.co/600x400/008000/FFFFFF?text=Player",
            "July 15, 2025",
            "In a major move, FC Neftchi has announced the signing of Brazilian forward Adriano, a prolific goalscorer from the Campeonato Brasileiro Série A. The deal is reported to be for three years. Fans are ecstatic about the new addition to the squad."
        ),
        NewsArticle(
            "news2",
            "Match preview: Neftchi vs Pakhtakor",
            "https://placehold.co/600x400/CCCCCC/FFFFFF?text=Stadium",
            "July 14, 2025",
            "The eternal derby is upon us. This Sunday, all eyes will be on the Istiqlol Stadium as Neftchi hosts arch-rivals Pakhtakor Tashkent in what promises to be a thrilling encounter. Both teams are in top form, making this a must-watch match for any football fan in Uzbekistan."
        )
    )


    override fun getNextMatch(): Flow<Match> = flow {
        delay(500)
        emit(
            Match(
                "match1",
                neftchi,
                pakhtakor,
                null,
                null,
                "2025-07-20T19:00:00Z",
                MatchStatus.SCHEDULED,
                "Uzbekistan Super League"
            )
        )
    }

    override fun getLastMatch(): Flow<Match> = flow {
        delay(300)
        emit(
            Match(
                "match2",
                nasaf,
                neftchi,
                1,
                2,
                "2025-07-12T18:00:00Z",
                MatchStatus.FINISHED,
                "Uzbekistan Super League"
            )
        )
    }

    override fun getAllFixtures(): Flow<List<Match>> = flow {
        delay(800)
        emit(
            listOf(
                Match(
                    "fix1",
                    neftchi,
                    pakhtakor,
                    null,
                    null,
                    "2025-07-20T19:00:00Z",
                    MatchStatus.SCHEDULED,
                    "Super League"
                ),
                Match(
                    "fix2",
                    neftchi,
                    Team(4, "Bunyodkor", ""),
                    null,
                    null,
                    "2025-07-28T19:00:00Z",
                    MatchStatus.SCHEDULED,
                    "Super League"
                )
            )
        )
    }

    override fun getAllResults(): Flow<List<Match>> = flow {
        delay(800)
        emit(
            listOf(
                Match(
                    "res1",
                    nasaf,
                    neftchi,
                    1,
                    2,
                    "2025-07-12T18:00:00Z",
                    MatchStatus.FINISHED,
                    "Super League"
                ),
                Match(
                    "res2",
                    neftchi,
                    Team(5, "AGMK", ""),
                    3,
                    0,
                    "2025-07-05T18:00:00Z",
                    MatchStatus.FINISHED,
                    "Super League"
                )
            )
        )
    }

    override fun getNews(): Flow<List<NewsArticle>> = flow {
        delay(1200)
        emit(allNews)
    }

    override fun getNewsArticleById(id: String): Flow<NewsArticle?> = flow {
        delay(200)
        emit(allNews.find { it.id == id })
    }

    override fun getTeam(): Flow<List<Player>> = flow {
        delay(600)
        emit(
            listOf(
                Player(
                    1,
                    "Sharof Mukhiddinov",
                    10,
                    "Midfielder",
                    "https://placehold.co/400x400/CCCCCC/FFFFFF?text=Player1",
                    "Uzbekistan"
                ),
                Player(
                    2,
                    "Botirali Ergashev",
                    1,
                    "Goalkeeper",
                    "https://placehold.co/400x400/CCCCCC/FFFFFF?text=Player2",
                    "Uzbekistan"
                ),
                Player(
                    3,
                    "Andrei Mishenko",
                    5,
                    "Defender",
                    "https://placehold.co/400x400/CCCCCC/FFFFFF?text=Player3",
                    "Ukraine"
                )
            )
        )
    }

    override fun getLeagueTable(): Flow<List<LeagueStanding>> = flow {
        delay(400)
        emit(
            listOf(
                LeagueStanding(1, pakhtakor, 15, 12, 2, 1, 38),
                LeagueStanding(2, nasaf, 15, 11, 3, 1, 36),
                LeagueStanding(3, neftchi, 15, 10, 2, 3, 32),
                LeagueStanding(4, Team(5, "AGMK", ""), 15, 8, 4, 3, 28)
            )
        )
    }
}
