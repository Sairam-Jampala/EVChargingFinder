package com.example.evchargingfinder.data.ocm

import com.squareup.moshi.Json

data class OcmPoiDto(
    @Json(name = "ID") val id: Long,
    @Json(name = "AddressInfo") val addressInfo: AddressInfoDto?,
    @Json(name = "Connections") val connections: List<ConnectionDto>?
)

data class AddressInfoDto(
    @Json(name = "Title") val title: String?,
    @Json(name = "AddressLine1") val addressLine1: String?,
    @Json(name = "Town") val town: String?,
    @Json(name = "StateOrProvince") val state: String?,
    @Json(name = "Postcode") val postcode: String?,
    @Json(name = "Latitude") val latitude: Double?,
    @Json(name = "Longitude") val longitude: Double?
)

data class ConnectionDto(
    @Json(name = "ConnectionType") val connectionType: ConnectionTypeDto?,
    @Json(name = "PowerKW") val powerKW: Double?
)

data class ConnectionTypeDto(
    @Json(name = "Title") val title: String?
)
