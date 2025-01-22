package com.tvaleriote.mobilechallenge

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tvaleriote.mobilechallenge.roomdb.DatabaseManager
import com.tvaleriote.mobilechallenge.roomdb.Podcast
import com.tvaleriote.mobilechallenge.ui.theme.dividerColor

@Composable
fun PodcastsPage(onPodcastSelected: (Podcast) -> Unit) {
    val podcastsDao = DatabaseManager.database.podcastsDao()
    val podcastsFlow = podcastsDao.getAllPodcastsFlow().collectAsState(initial = emptyList())

    Column {
        Text(text = "Podcasts")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(podcastsFlow.value) { podcast ->
                Box(
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp).clickable {
                        onPodcastSelected(podcast)
                    }
                ) {
                    PodcastDetailsCard(podcast)
                }

                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.5.dp, color = dividerColor)
            }
        }
    }
}