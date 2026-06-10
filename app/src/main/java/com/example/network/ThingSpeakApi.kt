package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class ThingSpeakResponse(
    val channel: ChannelInfo,
    val feeds: List<Feed>
)

@JsonClass(generateAdapter = true)
data class ChannelInfo(
    val id: Int,
    val name: String,
    val field1: String?,
    val field2: String?,
    val field3: String?,
    val field4: String?,
    val field5: String?
)

@JsonClass(generateAdapter = true)
data class Feed(
    val created_at: String,
    val entry_id: Long,
    val field1: String?, // Landslide (Soil Water Capacity)
    val field2: String?, // Bridge Collapsing (Soil Erosion)
    val field3: String?, // Earthquake X
    val field4: String?, // Earthquake Y
    val field5: String?  // Earthquake Z
)

interface ThingSpeakService {
    @GET("channels/3198728/feeds.json")
    suspend fun getFeeds(
        @Query("api_key") apiKey: String = "DHR915PHZC2T4MJF",
        @Query("results") results: Int = 1
    ): ThingSpeakResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://api.thingspeak.com/"

    val service: ThingSpeakService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ThingSpeakService::class.java)
    }
}
