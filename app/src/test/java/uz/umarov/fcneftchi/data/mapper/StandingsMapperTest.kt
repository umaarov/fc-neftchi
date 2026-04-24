package uz.umarov.fcneftchi.data.mapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import uz.umarov.fcneftchi.data.model.LeagueTableItem

class StandingsMapperTest {

    private val neftchi = LeagueTableItem(
        id = 17,
        title = "Neftchi",
        logo = "https://cdn/neftchi.png",
        games = 12,
        wins = 8,
        draws = 2,
        losses = 2,
        goalDifference = 14,
        points = 26
    )

    @Test
    fun `toLeagueStanding maps fields and stamps the supplied position`() {
        val standing = neftchi.toLeagueStanding(position = 1)

        assertThat(standing.position).isEqualTo(1)
        assertThat(standing.team.id).isEqualTo(17)
        assertThat(standing.team.name).isEqualTo("Neftchi")
        assertThat(standing.team.logoUrl).isEqualTo("https://cdn/neftchi.png")
        assertThat(standing.played).isEqualTo(12)
        assertThat(standing.wins).isEqualTo(8)
        assertThat(standing.draws).isEqualTo(2)
        assertThat(standing.losses).isEqualTo(2)
        assertThat(standing.points).isEqualTo(26)
        assertThat(standing.goalDifference).isEqualTo(14)
    }

    @Test
    fun `position is taken from caller, not from item id`() {
        val standing = neftchi.toLeagueStanding(position = 5)

        assertThat(standing.position).isEqualTo(5)
        assertThat(standing.team.id).isEqualTo(17)
    }
}
