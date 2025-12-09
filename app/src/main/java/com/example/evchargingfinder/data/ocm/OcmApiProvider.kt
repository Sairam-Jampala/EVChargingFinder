package com.example.evchargingfinder.data.ocm

import com.example.evchargingfinder.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object OcmApiProvider {

    private const val BASE_URL = "https://api.openchargemap.io/v3/"

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: OcmApi = retrofit.create(OcmApi::class.java)

    // Read from gradle.properties via buildConfigField in your module gradle file
    val apiKey: String
        get() = BuildConfig.OCM_API_KEY
}
