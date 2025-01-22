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
import androidx.room.Transaction
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

    //Want to ensure the `favorite` column doesnt get changed / reset when upserting
    //Check if the podcast to be upserted exists or not, if it does then update, if it doesnt, just insert
    @Transaction
    suspend fun upsert(podcast: Podcast) {
        val existingPodcast = getPodcastById(podcast.id)
        if (existingPodcast != null) {
            updatePodcastWithoutFavorite(
                podcast.id, podcast.title, podcast.image, podcast.publisher, podcast.description
            )
        } else {
            insert(podcast)
        }
    }

    @Query("UPDATE podcasts SET title = :title, image = :image, publisher = :publisher, description = :description WHERE id = :id")
    suspend fun updatePodcastWithoutFavorite(id: String, title: String, image: String, publisher: String, description: String)

    @Query("SELECT * FROM podcasts WHERE id = :id")
    suspend fun getPodcastById(id: String): Podcast?

    @Query("UPDATE podcasts SET favorite = 1 WHERE id = :id")
    suspend fun favoritePodcast(id: String)

    @Query("UPDATE podcasts SET favorite = 0 WHERE id = :id")
    suspend fun unFavoritePodcast(id: String)

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

    val database: RoomDB
        get() {
            if (INSTANCE == null) {
                throw IllegalStateException("Database is not initialized. Make sure to call initialize() first.")
            }
            return INSTANCE!!
        }

    fun closeDatabase() {
        INSTANCE?.close()
        INSTANCE = null
    }
}