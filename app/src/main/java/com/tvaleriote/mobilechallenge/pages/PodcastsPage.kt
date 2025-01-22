package com.tvaleriote.mobilechallenge.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvaleriote.mobilechallenge.PodcastDetailsCard
import com.tvaleriote.mobilechallenge.roomdb.DatabaseManager
import com.tvaleriote.mobilechallenge.roomdb.Podcast
import com.tvaleriote.mobilechallenge.ui.theme.dividerColor
import com.tvaleriote.mobilechallenge.ui.theme.loadingIconColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PodcastsPage(onPodcastSelected: (Podcast) -> Unit) {
    var totalLoaded by remember { mutableIntStateOf(10) }
    var isLoading by remember { mutableStateOf(false) }

    val podcastsDao = DatabaseManager.database.podcastsDao()
    val podcastsFlow = remember(totalLoaded) { podcastsDao.getAllPodcastsFlow(totalLoaded) }.collectAsState(initial = emptyList())

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Function to add 10 items to the list and trigger isLoading
    fun loadMoreItems() {
        coroutineScope.launch {
            isLoading = true
            delay(1000)
            totalLoaded += 10
            isLoading = false
        }
    }

    //When the user scrolls to the bottom of the lazy column we want to load more items
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isNotEmpty() && visibleItems.last().index == totalItems - 1) {
                    //Check and see if all the items are currently being shown
                    val totalPodcasts = DatabaseManager.database.podcastsDao().countTotalPodcasts()
                    if (totalPodcasts == podcastsFlow.value.size) {
                        isLoading = false
                    } else {
                        loadMoreItems()
                    }
                }
            }
    }

    Column {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
            Text(text = "Podcasts", fontSize = 24.sp, fontWeight = FontWeight(650))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(podcastsFlow.value) { podcast ->
                //When the user selects the podcast, trigger onPodcastSelected so the main page knows to open the podcasts individual page
                Box(
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp).clickable {
                        onPodcastSelected(podcast)
                    }
                ) {
                    PodcastDetailsCard(podcast)
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp, color = dividerColor)
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = loadingIconColor)
                    }
                }
            }
        }
    }
}

