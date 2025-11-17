package com.eco.musicplayer.audioplayer.music.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eco.musicplayer.audioplayer.music.roomdb.converter.Converters
import com.eco.musicplayer.audioplayer.music.roomdb.dao.PostDao
import com.eco.musicplayer.audioplayer.music.roomdb.dao.UserDao
import com.eco.musicplayer.audioplayer.music.roomdb.model.Address
import com.eco.musicplayer.audioplayer.music.roomdb.model.Post
import com.eco.musicplayer.audioplayer.music.roomdb.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile

@Database(
    entities = [User::class, Post::class],
    version = 2,
    exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }

        private fun buildDatabase(appContext: Context): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, "room_demo.db")
                .addMigrations(MIGRATION_1_2)
                .addCallback(object  : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // prepare data
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(appContext)
                            val uDao = database.userDao()
                            val pDao = database.postDao()

                            val uid1 = uDao.insertReplace(User(name = "Nguyen Van A", email = "nva@gmail.com", address = Address("1st Trang Thi", "Hanoi")))
                            val uid2 = uDao.insertReplace(User(name = "Nguyen Van B", email = "nvb@gmail.com", address = Address("2nd My Dinh", "Hanoi")))

                            pDao.insertPost(Post(title = "Welcome", content = "Hello from Nguyen Van A", userId = uid1.toInt()))
                            pDao.insertPost(Post(title = "B's Post", content = "B's content", userId = uid2.toInt()))
                        }
                    }
                }).build()

        }
    }
}