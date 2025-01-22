package com.tvaleriote.mobilechallenge

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tvaleriote.mobilechallenge.roomdb.DatabaseManager
import com.tvaleriote.mobilechallenge.roomdb.Podcast
import kotlinx.coroutines.launch

@Composable
fun PodcastDetailsPage(podcast: Podcast, onBackSelected: () -> Unit) {
    val podcastsDao = DatabaseManager.database.podcastsDao()

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {

        Text(text = "Back", modifier = Modifier.clickable {
            onBackSelected()
        })

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = podcast.title)
            Text(text = podcast.publisher)

            ImageDisplay(podcast.image, 100, 6)

            Button(
                onClick = {
                    coroutineScope.launch {
                        podcastsDao.favoritePodcast(podcast.id)
                    }
                }
            ) {
                Text(text = "Favourite")
            }

            Text(text = podcast.description)
        }
    }
}