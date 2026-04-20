package uz.umarov.fcneftchi.data

/**
 * Static configuration for the club and league this app is built for.
 *
 * IDs come from the PFL Uzbekistan API (api.pfl.uz).
 *
 * When a new season starts, update [CURRENT_SEASON_ID] and add the previous
 * season to [SEASONS_BY_NAME].
 */
object ClubConfig {

    /** Neftchi Fergana in the PFL backend. */
    const val CLUB_ID = 7

    /** Uzbekistan Super League (O'zbekiston Superligasi). */
    const val TOURNAMENT_ID = 1

    /**
     * Season the app shows as "current" for home/squad/player stats.
     * Bump each season.
     */
    const val CURRENT_SEASON_ID = 11

    /**
     * Season id used for the home screen "league table preview" card.
     * We fall back to the previous season until the new one has enough
     * matches played to look meaningful on the home page.
     */
    const val HOME_STANDINGS_SEASON_ID = 10

    /** News category id that contains video content (filtered into /videos tab). */
    const val VIDEO_CATEGORY_ID = 13

    /**
     * All seasons that have data in the backend, in display order.
     * Keys are the human-facing labels shown in the season dropdown,
     * values are the backend season ids.
     *
     * Newest first.
     */
    val SEASONS_BY_NAME: Map<String, Int> = linkedMapOf(
        "2026" to 11,
        "2025" to 10,
        "2024" to 1,
        "2023" to 2,
        "2022" to 3
    )

    /** Default season shown when the user first opens the league table. */
    const val DEFAULT_SEASON_NAME = "2026"
}
