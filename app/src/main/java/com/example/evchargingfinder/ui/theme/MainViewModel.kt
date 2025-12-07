package com.example.evchargingfinder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evchargefinder.data.ocm.OcmApiProvider
import com.example.evchargefinder.data.ocm.OcmPoiDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val loading: Boolean = false,
    val error: String? = null,
    val items: List<OcmPoiDto> = emptyList()
)

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun loadStations() {
        viewModelScope.launch {
            _state.value = UiState(loading = true)

            try {
                // Fixed location: central London
                val lat = 51.509865
                val lng = -0.118092

                val result = OcmApiProvider.api.getStations(
                    lat = lat,
                    lng = lng,
                    distanceKm = 10,
                    maxResults = 20
                )

                _state.value = UiState(
                    loading = false,
                    items = result
                )
            } catch (e: Exception) {
                _state.value = UiState(
                    loading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}
