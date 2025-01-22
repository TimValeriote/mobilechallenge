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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
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
            AutoSizedText(
                text = podcast.title,
                style = MaterialTheme.typography.bodyLarge,
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

//Custom composable to resize text until it fits in 1 line
@Composable
fun AutoSizedText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    var textStyle by remember {
        mutableStateOf(style)
    }

    val defaultSize = MaterialTheme.typography.bodyLarge.fontSize
    Text(
        text = text,
        fontWeight = FontWeight(650),
        softWrap = false,
        style = textStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (style.fontSize.isUnspecified) {
                    textStyle = textStyle.copy(
                        fontSize = defaultSize
                    )
                }
                textStyle = textStyle.copy(
                    fontSize = textStyle.fontSize * 0.95
                )
            }
        }
    )

}