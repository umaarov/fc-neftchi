package uz.umarov.fcneftchi.data.mapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.local.entity.MatchEntity
import uz.umarov.fcneftchi.data.local.entity.PlayerEntity
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.MatchStatus
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition
import uz.umarov.fcneftchi.data.model.Team
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class EntityMappersTest {

    private val iso = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .withZone(ZoneOffset.UTC)

    @Test
    fun `article round-trips through entity with sortOrder and cachedAt`() {
        val article = NewsArticle(
            id = "99",
            title = "T",
            imageUrl = "img",
            date = "d",
            content = "c",
            description = "desc",
            category = "cat",
            url = "u",
        )

        val entity = article.toEntity(sortOrder = 3, cachedAt = 1_234)

        assertThat(entity.sortOrder).isEqualTo(3)
        assertThat(entity.cachedAt).isEqualTo(1_234)
        assertThat(entity.toArticle()).isEqualTo(article)
    }

    @Test
    fun `match toEntity flattens Team into columns`() {
        val match = Match(
            id = "m1",
            homeTeam = Team(id = 10, name = "Neftchi", logoUrl = "home.png"),
            awayTeam = Team(id = 20, name = "Pakhtakor", logoUrl = "away.png"),
            homeScore = 2,
            awayScore = 1,
            matchDate = "2024-06-10T18:00:00Z",
            status = MatchStatus.FINISHED,
            competition = "Super League",
        )

        val entity = match.toEntity(cachedAt = 77)

        assertThat(entity.homeTeamId).isEqualTo(10)
        assertThat(entity.homeTeamName).isEqualTo("Neftchi")
        assertThat(entity.homeTeamLogo).isEqualTo("home.png")
        assertThat(entity.awayTeamId).isEqualTo(20)
        assertThat(entity.cachedAt).isEqualTo(77)
        assertThat(entity.competition).isEqualTo("Super League")
    }

    @Test
    fun `matchEntity toMatch derives status from matchDate`() {
        val future = iso.format(Instant.now().plusSeconds(3600))
        val past = iso.format(Instant.now().minusSeconds(3600))

        val base = MatchEntity(
            id = "x",
            homeTeamId = 1, homeTeamName = "a", homeTeamLogo = "al",
            awayTeamId = 2, awayTeamName = "b", awayTeamLogo = "bl",
            homeScore = null, awayScore = null,
            matchDate = future,
            competition = "",
            cachedAt = 0,
        )

        assertThat(base.toMatch().status).isEqualTo(MatchStatus.SCHEDULED)
        assertThat(base.copy(matchDate = past).toMatch().status).isEqualTo(MatchStatus.FINISHED)
        assertThat(base.copy(matchDate = "broken").toMatch().status).isEqualTo(MatchStatus.FINISHED)
    }

    @Test
    fun `standing round-trips via entity with seasonId + cachedAt stamp`() {
        val standing = LeagueStanding(
            position = 3,
            team = Team(id = 17, name = "Neftchi", logoUrl = "logo"),
            played = 10, wins = 6, draws = 2, losses = 2,
            points = 20, goalDifference = 9,
        )

        val entity = standing.toEntity(seasonId = 2026, cachedAt = 500)

        assertThat(entity.seasonId).isEqualTo(2026)
        assertThat(entity.cachedAt).isEqualTo(500)
        assertThat(entity.toLeagueStanding()).isEqualTo(standing.copy(trend = standing.trend))
    }

    @Test
    fun `player toEntity writes position enum name and drops Int imageUrl to null`() {
        val photoPlayer = Player(
            id = 1, name = "x", number = 9, position = PlayerPosition.ATTACKER,
            imageUrl = "https://cdn/x.png", nationality = "UZ",
        )
        val drawablePlayer = photoPlayer.copy(imageUrl = R.drawable.player_placeholder_inset)

        assertThat(photoPlayer.toEntity(cachedAt = 0).position).isEqualTo("ATTACKER")
        assertThat(photoPlayer.toEntity(cachedAt = 0).photoUrl).isEqualTo("https://cdn/x.png")
        assertThat(drawablePlayer.toEntity(cachedAt = 0).photoUrl).isNull()
    }

    @Test
    fun `playerEntity toPlayer decodes position name, falls back to UNKNOWN on bad name`() {
        val entity = PlayerEntity(
            id = 1, name = "x", number = 9,
            position = "ATTACKER",
            photoUrl = "https://cdn/x.png",
            nationality = "UZ",
            cachedAt = 0,
        )

        assertThat(entity.toPlayer().position).isEqualTo(PlayerPosition.ATTACKER)
        assertThat(entity.copy(position = "not-a-position").toPlayer().position)
            .isEqualTo(PlayerPosition.UNKNOWN)
    }

    @Test
    fun `playerEntity toPlayer hydrates placeholder when photoUrl is null`() {
        val entity = PlayerEntity(
            id = 1, name = "x", number = 9,
            position = "DEFENDER",
            photoUrl = null,
            nationality = "UZ",
            cachedAt = 0,
        )

        assertThat(entity.toPlayer().imageUrl).isEqualTo(R.drawable.player_placeholder_inset)
    }
}
