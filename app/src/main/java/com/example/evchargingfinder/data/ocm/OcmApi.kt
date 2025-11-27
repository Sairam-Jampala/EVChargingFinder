package com.example.evchargefinder.data.ocm

import retrofit2.http.GET
import retrofit2.http.Query

interface OcmApi {
    @GET("v3/poi/")
    suspend fun getStations(
        @Query("output") output: String = "json",
        @Query("countrycode") country: String = "GB",
        @Query("latitude") lat: Double,
        @Query("longitude") lng: Double,
        @Query("distance") distanceKm: Int = 10,
        @Query("maxresults") maxResults: Int = 20
    ): List<OcmPoiDto>
}
