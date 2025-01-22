package com.tvaleriote.mobilechallenge

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.tvaleriote.mobilechallenge.pages.PodcastDetailsPage
import com.tvaleriote.mobilechallenge.pages.PodcastsPage
import com.tvaleriote.mobilechallenge.roomdb.DatabaseManager
import com.tvaleriote.mobilechallenge.roomdb.Podcast
import com.tvaleriote.mobilechallenge.roomdb.PodcastsDao
import com.tvaleriote.mobilechallenge.roomdb.RoomDB
import com.tvaleriote.mobilechallenge.services.PodcastsService
import com.tvaleriote.mobilechallenge.ui.theme.MobilechallengeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val currentPage = mutableStateOf("podcasts")
    private val podcastService = PodcastsService()
    private lateinit var database: RoomDB
    private lateinit var podcastsDao: PodcastsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get current database and create instance of podcast dao
        database = DatabaseManager.getDatabase(this)
        podcastsDao = database.podcastsDao()

        //Fetch all the podcasts and save them to the DB (if there werent already in there)
        lifecycleScope.launch {
            fetchAndSavePodcasts()
        }

        enableEdgeToEdge()
        setContent {
            MobilechallengeTheme {
                var selectedPodcast by remember { mutableStateOf<Podcast?>(null) }
                val coroutineScope = rememberCoroutineScope()

                Scaffold { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        //Track which page we are on in order to display either all podcasts or an individual one
                        when(currentPage.value) {
                            "podcasts" -> PodcastsPage(onPodcastSelected = { podcast ->
                                selectedPodcast = podcast
                                currentPage.value = "podcast"
                            })
                            "podcast" -> selectedPodcast?.let {
                                PodcastDetailsPage(onBackSelected = {
                                    currentPage.value = "podcasts"
                                }, podcast = it, onPodcastFavorited = { podcastId ->
                                    //When the user favorites the podcasts, save that to the DB and reload the selected podcast so the button changes displaying its favorited
                                    run {
                                        coroutineScope.launch {
                                            database.podcastsDao().favoritePodcast(podcastId)
                                            selectedPodcast = database.podcastsDao().getPodcastById(podcastId)
                                        }
                                    }
                                }, onPodcastUnfavorited = { podcastId ->
                                    //When the user un-favorites the podcast, do the same as before but in reverse
                                    run {
                                        coroutineScope.launch {
                                            database.podcastsDao().unFavoritePodcast(podcastId)
                                            selectedPodcast = database.podcastsDao().getPodcastById(podcastId)
                                        }
                                    }
                                })
                            }
                        }
                    }
                }

            }
        }
    }

    /*
        Obtains podcasts via the podcast service
        Creates new database podcast object and upserts it in the database
     */
    private suspend fun fetchAndSavePodcasts() {
        try {
            val podcasts = podcastService.getPodcasts()

            podcasts?.let {
                for (podcast in it) {
                    val dbPodcast = Podcast(
                        id = podcast.id,
                        title = podcast.title,
                        image = podcast.image,
                        publisher = podcast.publisher,
                        description = podcast.description
                    )
                    podcastsDao.upsert(dbPodcast)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error fetching or saving podcasts: ${e.message}")
        }
    }
}