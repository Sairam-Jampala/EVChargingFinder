package com.example.evchargingfinder.presentation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.evchargingfinder.domain.ChargerLocation
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun ChargerMapScreen(
    chargers: List<ChargerLocation>
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            // Configure osmdroid
            Configuration.getInstance().load(
                context,
                context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
            )

            MapView(context).apply {
                setMultiTouchControls(true)

                // Start position: first charger if available, otherwise London
                val start = chargers.firstOrNull { it.latitude != null && it.longitude != null }
                val startPoint = if (start != null) {
                    GeoPoint(start.latitude!!, start.longitude!!)
                } else {
                    GeoPoint(51.5074, -0.1278) // London fallback
                }

                controller.setZoom(11.0)
                controller.setCenter(startPoint)

                // Add a marker for each charger
                chargers.forEach { charger ->
                    val lat = charger.latitude
                    val lng = charger.longitude
                    if (lat != null && lng != null) {
                        val marker = Marker(this)
                        marker.position = GeoPoint(lat, lng)
                        marker.title = charger.title
                        marker.snippet = charger.connectorsSummary
                        overlays.add(marker)
                    }
                }
            }
        },
        update = { mapView ->
            // If you want to update markers later, you can do it here
        }
    )
}
