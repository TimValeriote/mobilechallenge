package com.tvaleriote.mobilechallenge.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvaleriote.mobilechallenge.ImageDisplay
import com.tvaleriote.mobilechallenge.roomdb.Podcast
import com.tvaleriote.mobilechallenge.ui.theme.favoriteButtonColor
import com.tvaleriote.mobilechallenge.ui.theme.favoriteColor
import com.tvaleriote.mobilechallenge.ui.theme.publisherColor

@Composable
fun PodcastDetailsPage(podcast: Podcast, onBackSelected: () -> Unit, onPodcastFavorited: (String) -> Unit, onPodcastUnfavorited: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxHeight().fillMaxWidth().padding(start = 16.dp, end = 16.dp)) {

        //When the user clicks on this row, trigger onBackSelected so the main page knows to return
        Row(
            modifier = Modifier.clickable {
                onBackSelected()
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back")
            Text(text = "Back", fontWeight = FontWeight(650))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = podcast.title, fontSize = 16.sp, fontWeight = FontWeight(650))
            Spacer(modifier = Modifier.height(4.dp))

            Text(text = podcast.publisher, fontSize = 12.sp, color = publisherColor)
            Spacer(modifier = Modifier.height(16.dp))

            ImageDisplay(podcast.image, 200, 8)
            Spacer(modifier = Modifier.height(16.dp))

            //Onclick we trigger either onPodcastFavorited or onPodcastUnfavorited based on the current favourited status
            Button(
                onClick = {
                    if (podcast.favorite) {
                        onPodcastUnfavorited(podcast.id)
                    } else {
                        onPodcastFavorited(podcast.id)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (podcast.favorite) favoriteColor else favoriteButtonColor
                ),
                modifier = Modifier.width(125.dp)
            ) {
                Text(text = if (podcast.favorite) "Favourited" else "Favourite")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = podcast.description, fontSize = 12.sp, color = publisherColor, textAlign = TextAlign.Center, lineHeight = 12.sp)
        }
    }
}
