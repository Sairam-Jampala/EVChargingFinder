package com.example.evchargefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evchargefinder.data.ocm.OcmPoiDto
import com.example.evchargefinder.ui.MainViewModel
import com.example.evchargefinder.ui.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EvChargeApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvChargeApp(vm: MainViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    // Call API when app starts
    LaunchedEffect(Unit) {
        vm.loadStations()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("EV Charge Finder (OCM)") }) }
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Content(state = state, onRetry = { vm.loadStations() })
        }
    }
}

@Composable
fun Content(state: UiState, onRetry: () -> Unit) {
    when {
        state.loading -> {
            Column(Modifier.padding(16.dp)) {
                LinearProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Loading nearby EV chargers…")
            }
        }
        state.error != null -> {
            Column(Modifier.padding(16.dp)) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRetry) { Text("Retry") }
            }
        }
        else -> {
            if (state.items.isEmpty()) {
                Column(Modifier.padding(16.dp)) {
                    Text("No chargers found.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRetry) { Text("Reload") }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                    items(state.items, key = { it.id }) { poi ->
                        ChargerRow(poi)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun ChargerRow(poi: OcmPoiDto) {
    val a = poi.address
    val title = a?.title ?: "Unknown station"
    val address = listOfNotNull(a?.line1, a?.town, a?.postcode)
        .filter { !it.isNullOrBlank() }
        .joinToString(", ")

    val connSummary = poi.connections
        ?.mapNotNull {
            val type = it.type?.title
            val power = it.powerKw?.let { kw -> "${kw.toInt()} kW" }
            listOfNotNull(type, power).joinToString(" ")
        }
        ?.joinToString(" • ")
        .orEmpty()

    Column(Modifier.padding(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (address.isNotBlank()) {
            Text(address, style = MaterialTheme.typography.bodyMedium)
        }
        if (connSummary.isNotBlank()) {
            Text(connSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
