package com.tvaleriote.mobilechallenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.tvaleriote.mobilechallenge.roomdb.Podcast
import com.tvaleriote.mobilechallenge.ui.theme.favoriteColor
import com.tvaleriote.mobilechallenge.ui.theme.publisherColor

//Displays an image based on specified size and corner shape
@Composable
fun ImageDisplay(imageUrl: String, size: Int, cornerShape: Int) {
    AsyncImage(
        model = "$imageUrl",
        contentDescription = imageUrl,
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(cornerShape.dp))
    )
}

//Main component for the podcasts page, displays the information on the podcast in a row
@Composable
fun PodcastDetailsCard(podcast: Podcast) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp).height(intrinsicSize = IntrinsicSize.Max)
    ) {
        ImageDisplay(
            imageUrl = podcast.image,
            size = 75,
            cornerShape = 8
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        )  {
            Text(
                text = podcast.title,
                fontSize = 15.sp,
                fontWeight = FontWeight(650)
            )
            Text(
                text = podcast.publisher,
                fontSize = 12.sp,
                color = publisherColor
            )
            if (podcast.favorite) {
                Text(text = "Favourited", color = favoriteColor, fontSize = 12.sp)
            } else {
                Text(text = "")
            }
        }
    }
}