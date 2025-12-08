package com.example.evchargefinder.data.ocm

import com.squareup.moshi.Json

data class OcmPoiDto(
    @Json(name = "ID") val id: Long,
    @Json(name = "AddressInfo") val address: AddressInfo?,
    @Json(name = "Connections") val connections: List<Connection>?
) {
    data class AddressInfo(
        @Json(name = "Title") val title: String?,
        @Json(name = "AddressLine1") val line1: String?,
        @Json(name = "Town") val town: String?,
        @Json(name = "StateOrProvince") val state: String?,
        @Json(name = "Postcode") val postcode: String?,
        @Json(name = "Latitude") val lat: Double?,
        @Json(name = "Longitude") val lng: Double?
    )

    data class Connection(
        @Json(name = "PowerKW") val powerKw: Double?,
        @Json(name = "ConnectionType") val type: ConnType?
    ) {
        data class ConnType(
            @Json(name = "Title") val title: String?
        )
    }
}
