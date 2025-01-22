package com.tvaleriote.mobilechallenge.services

import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tvaleriote.mobilechallenge.models.Podcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

data class PodcastResponse(
    @Json(name = "podcasts") val podcasts: List<Podcast>
)

class PodcastsService {
    private val client = OkHttpClient()
    private val apiURL = "https://listen-api-test.listennotes.com/api/v2/best_podcasts"
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private suspend fun getAllPodcasts(): PodcastResponse? = coroutineScope {
        val request = Request.Builder().url(apiURL).build()

        return@coroutineScope withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    responseBody?.let {
                        val type = Types.newParameterizedType(PodcastResponse::class.java, Podcast::class.java)
                        val jsonAdapter: JsonAdapter<PodcastResponse> = moshi.adapter(type)

                        try {
                            jsonAdapter.fromJson(responseBody)
                        } catch (e: Exception) {
                            Log.e("PodcastsService", "Error parsing JSON: ${e.message}")
                            null
                        }
                    }
                } else {
                    Log.e("PodcastsService", "Error fetching podcasts: HTTP ${response.code}")
                    null
                }
            } catch (e: IOException) {
                Log.e("PodcastsService", "Error making the API call: ${e.message}")
                null
            }
        }
    }

    suspend fun getPodcasts(): List<Podcast>? {
        return try {
            getAllPodcasts()?.podcasts
        } catch (e: Exception) {
            Log.e("PodcastsService", "Exception in getPodcasts: ${e.message}")
            null
        }
    }
}