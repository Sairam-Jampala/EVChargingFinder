package com.example.evchargingfinder.data.ocm

import retrofit2.http.GET
import retrofit2.http.Query

interface OcmApi {

    @GET("poi/")
    suspend fun getChargePoints(
        @Query("key") apiKey: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distance") distanceKm: Double = 10.0,
        @Query("distanceunit") distanceUnit: String = "KM",
        @Query("maxresults") maxResults: Int = 25
    ): List<OcmPoiDto>
}
