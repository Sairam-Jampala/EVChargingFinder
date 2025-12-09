package com.example.evchargingfinder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evchargingfinder.data.ocm.OcmApiProvider
import com.example.evchargingfinder.domain.ChargerLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// State that the UI observes
sealed interface ChargerUiState {
    object Loading : ChargerUiState
    data class Success(val chargers: List<ChargerLocation>) : ChargerUiState
    data class Error(val message: String) : ChargerUiState
}

class ChargerViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<ChargerUiState> =
        MutableStateFlow(ChargerUiState.Loading)
    val uiState: StateFlow<ChargerUiState> = _uiState

    init {
        loadChargers()
    }

    private fun loadChargers() {
        viewModelScope.launch {
            try {
                val api = OcmApiProvider.api
                val key = OcmApiProvider.apiKey

                // Temporary fixed location: London
                val lat = 51.5074
                val lng = -0.1278

                val result = api.getChargePoints(
                    apiKey = key,
                    latitude = lat,
                    longitude = lng,
                    distanceKm = 10.0,
                    maxResults = 25
                )

                val mapped = result.mapNotNull { dto ->
                    val addr = dto.addressInfo ?: return@mapNotNull null

                    val connectors = dto.connections.orEmpty()
                    val connectorTypes = connectors
                        .mapNotNull { it.connectionType?.title }
                        .distinct()
                        .joinToString(", ")
                        .ifBlank { "Unknown connectors" }

                    ChargerLocation(
                        id = dto.id,
                        title = addr.title ?: "Unnamed charger",
                        addressLine = addr.addressLine1,
                        town = addr.town,
                        latitude = addr.latitude,
                        longitude = addr.longitude,
                        connectorsSummary = connectorTypes
                    )
                }

                _uiState.value = ChargerUiState.Success(mapped)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ChargerUiState.Error(
                    e.message ?: "Unknown error loading chargers"
                )
            }
        }
    }
}
