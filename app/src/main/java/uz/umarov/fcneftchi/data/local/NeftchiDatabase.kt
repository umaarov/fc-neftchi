package uz.umarov.fcneftchi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.umarov.fcneftchi.data.local.dao.BookmarkArticleDao
import uz.umarov.fcneftchi.data.local.dao.BookmarkPlayerDao
import uz.umarov.fcneftchi.data.local.dao.BookmarkVideoDao
import uz.umarov.fcneftchi.data.local.dao.LeagueStandingDao
import uz.umarov.fcneftchi.data.local.dao.MatchDao
import uz.umarov.fcneftchi.data.local.dao.NewsArticleDao
import uz.umarov.fcneftchi.data.local.dao.PlayerDao
import uz.umarov.fcneftchi.data.local.entity.BookmarkArticleEntity
import uz.umarov.fcneftchi.data.local.entity.BookmarkPlayerEntity
import uz.umarov.fcneftchi.data.local.entity.BookmarkVideoEntity
import uz.umarov.fcneftchi.data.local.entity.LeagueStandingEntity
import uz.umarov.fcneftchi.data.local.entity.MatchEntity
import uz.umarov.fcneftchi.data.local.entity.NewsArticleEntity
import uz.umarov.fcneftchi.data.local.entity.PlayerEntity

@Database(
    entities = [
        NewsArticleEntity::class,
        MatchEntity::class,
        LeagueStandingEntity::class,
        PlayerEntity::class,
        BookmarkArticleEntity::class,
        BookmarkVideoEntity::class,
        BookmarkPlayerEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class NeftchiDatabase : RoomDatabase() {
    abstract fun newsArticleDao(): NewsArticleDao
    abstract fun matchDao(): MatchDao
    abstract fun leagueStandingDao(): LeagueStandingDao
    abstract fun playerDao(): PlayerDao
    abstract fun bookmarkArticleDao(): BookmarkArticleDao
    abstract fun bookmarkVideoDao(): BookmarkVideoDao
    abstract fun bookmarkPlayerDao(): BookmarkPlayerDao
}
