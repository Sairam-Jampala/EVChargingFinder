package com.example.evchargingfinder.domain

data class ChargerLocation(
    val id: Long,
    val title: String,
    val addressLine: String?,
    val town: String?,
    val latitude: Double?,
    val longitude: Double?,
    val connectorsSummary: String
)