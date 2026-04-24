package uz.umarov.fcneftchi.data.mapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import uz.umarov.fcneftchi.data.model.ApiGameClub
import uz.umarov.fcneftchi.data.model.ApiGameTeam
import uz.umarov.fcneftchi.data.model.GameCalendarMatch
import uz.umarov.fcneftchi.data.model.MatchStatus
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MatchMapperTest {

    private val iso = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .withZone(ZoneOffset.UTC)

    private fun apiMatch(
        id: Int = 7,
        startDate: String,
        homeGoal: Int = 0,
        awayGoal: Int = 0,
    ) = GameCalendarMatch(
        id = id,
        startDate = startDate,
        endDate = null,
        homeTeam = ApiGameTeam(
            id = 1,
            title = "FC Neftchi",
            club = ApiGameClub(id = 10, title = "Neftchi", logo = "https://cdn/neftchi.png")
        ),
        awayTeam = ApiGameTeam(
            id = 2,
            title = "FC Pakhtakor",
            club = ApiGameClub(id = 20, title = "Pakhtakor", logo = "https://cdn/pakhtakor.png")
        ),
        homeGoal = homeGoal,
        awayGoal = awayGoal,
    )

    @Test
    fun `maps both teams from club ids, titles, logos`() {
        val match = apiMatch(startDate = "2030-01-01T18:00:00Z").toMatch()

        assertThat(match.homeTeam.id).isEqualTo(10)
        assertThat(match.homeTeam.name).isEqualTo("Neftchi")
        assertThat(match.homeTeam.logoUrl).isEqualTo("https://cdn/neftchi.png")
        assertThat(match.awayTeam.id).isEqualTo(20)
        assertThat(match.awayTeam.name).isEqualTo("Pakhtakor")
        assertThat(match.awayTeam.logoUrl).isEqualTo("https://cdn/pakhtakor.png")
    }

    @Test
    fun `stringifies id and preserves scores and startDate`() {
        val match = apiMatch(id = 42, startDate = "2024-06-10T18:00:00Z", homeGoal = 3, awayGoal = 1).toMatch()

        assertThat(match.id).isEqualTo("42")
        assertThat(match.homeScore).isEqualTo(3)
        assertThat(match.awayScore).isEqualTo(1)
        assertThat(match.matchDate).isEqualTo("2024-06-10T18:00:00Z")
        assertThat(match.competition).isEmpty()
    }

    @Test
    fun `future startDate yields SCHEDULED`() {
        val future = iso.format(Instant.now().plusSeconds(7 * 24 * 3600))

        val match = apiMatch(startDate = future).toMatch()

        assertThat(match.status).isEqualTo(MatchStatus.SCHEDULED)
    }

    @Test
    fun `past startDate yields FINISHED`() {
        val past = iso.format(Instant.now().minusSeconds(7 * 24 * 3600))

        val match = apiMatch(startDate = past).toMatch()

        assertThat(match.status).isEqualTo(MatchStatus.FINISHED)
    }

    @Test
    fun `unparseable startDate yields FINISHED`() {
        val match = apiMatch(startDate = "not-a-date").toMatch()

        assertThat(match.status).isEqualTo(MatchStatus.FINISHED)
    }
}
