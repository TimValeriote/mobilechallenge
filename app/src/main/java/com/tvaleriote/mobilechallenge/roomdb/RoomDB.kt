package com.tvaleriote.mobilechallenge.roomdb

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.internal.synchronized

@Entity(tableName = "podcasts")
data class Podcast(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "publisher") val publisher: String,
    @ColumnInfo(name = "description") val description: String,

    @ColumnInfo(name = "favorite") val favorite: Boolean = false,
)

@Dao
interface PodcastsDao {
    @Insert
    suspend fun insert(podcast: Podcast)

    @Upsert
    suspend fun upsert(podcast: Podcast)

    @Query("UPDATE podcasts SET favorite = 1 WHERE id = :id")
    suspend fun favoritePodcast(id: String)

    @Query("SELECT * FROM podcasts")
    fun getAllPodcastsFlow(): Flow<List<Podcast>>

    @Query("SELECT * FROM podcasts")
    suspend fun getAllPodcasts(): List<Podcast>
}

@Database(entities = [Podcast::class], version = 1)
abstract class RoomDB : RoomDatabase() {
    abstract fun podcastsDao(): PodcastsDao
}

object DatabaseManager {
    private var INSTANCE: RoomDB? = null

    @OptIn(InternalCoroutinesApi::class)
    fun getDatabase(context: Context): RoomDB {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                RoomDB::class.java,
                "podcasts_datbase"
            ).build()
            INSTANCE = instance
            instance
        }
    }

    fun closeDatabase() {
        INSTANCE?.close()
        INSTANCE = null
    }
}