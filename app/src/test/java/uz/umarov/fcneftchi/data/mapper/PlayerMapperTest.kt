package uz.umarov.fcneftchi.data.mapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.ApiPlayer
import uz.umarov.fcneftchi.data.model.PlayerPosition

class PlayerMapperTest {

    private fun apiPlayer(
        firstName: String? = "Odil",
        lastName: String = "Ahmedov",
        number: Int? = 10,
        position: Int = 3,
        photo: String? = "https://cdn/odil.png",
    ) = ApiPlayer(
        id = 1,
        position = position,
        firstName = firstName,
        lastName = lastName,
        photo = photo,
        number = number,
        countryTitle = "Uzbekistan"
    )

    @Test
    fun `joins first and last name with a space`() {
        val player = apiPlayer(firstName = "Odil", lastName = "Ahmedov").toPlayer()

        assertThat(player.name).isEqualTo("Odil Ahmedov")
    }

    @Test
    fun `trims leading space when firstName is null`() {
        val player = apiPlayer(firstName = null, lastName = "Ahmedov").toPlayer()

        assertThat(player.name).isEqualTo("Ahmedov")
    }

    @Test
    fun `null number defaults to zero`() {
        val player = apiPlayer(number = null).toPlayer()

        assertThat(player.number).isEqualTo(0)
    }

    @Test
    fun `position id maps to enum`() {
        assertThat(apiPlayer(position = 1).toPlayer().position).isEqualTo(PlayerPosition.GOALKEEPER)
        assertThat(apiPlayer(position = 2).toPlayer().position).isEqualTo(PlayerPosition.DEFENDER)
        assertThat(apiPlayer(position = 3).toPlayer().position).isEqualTo(PlayerPosition.MIDFIELDER)
        assertThat(apiPlayer(position = 4).toPlayer().position).isEqualTo(PlayerPosition.ATTACKER)
    }

    @Test
    fun `unknown position id maps to UNKNOWN`() {
        val player = apiPlayer(position = 99).toPlayer()

        assertThat(player.position).isEqualTo(PlayerPosition.UNKNOWN)
    }

    @Test
    fun `photo url is preserved when present`() {
        val player = apiPlayer(photo = "https://cdn/foo.png").toPlayer()

        assertThat(player.imageUrl).isEqualTo("https://cdn/foo.png")
    }

    @Test
    fun `null photo falls back to placeholder drawable resource`() {
        val player = apiPlayer(photo = null).toPlayer()

        assertThat(player.imageUrl).isEqualTo(R.drawable.player_placeholder_inset)
    }

    @Test
    fun `nationality is carried through from countryTitle`() {
        val player = apiPlayer().toPlayer()

        assertThat(player.nationality).isEqualTo("Uzbekistan")
    }
}
