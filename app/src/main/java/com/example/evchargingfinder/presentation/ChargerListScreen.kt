package com.example.evchargingfinder.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evchargingfinder.domain.ChargerLocation

@Composable
fun ChargerHomeScreen() {
    val viewModel: ChargerViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var showMap by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is ChargerUiState.Loading -> LoadingState()

            is ChargerUiState.Error -> ErrorState(
                message = (uiState as ChargerUiState.Error).message
            )

            is ChargerUiState.Success -> {
                val chargers = (uiState as ChargerUiState.Success).chargers

                Column(modifier = Modifier.fillMaxSize()) {
                    // Simple toggle buttons at the top
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showMap = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("List")
                        }
                        OutlinedButton(
                            onClick = { showMap = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Map")
                        }
                    }

                    if (showMap) {
                        ChargerMapScreen(chargers)
                    } else {
                        ChargersList(chargers)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Loading nearby chargers…")
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Failed to load chargers",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message)
    }
}

@Composable
private fun ChargersList(chargers: List<ChargerLocation>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(chargers, key = { it.id }) { charger ->
            ChargerRow(charger)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ChargerRow(charger: ChargerLocation) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = charger.title,
            style = MaterialTheme.typography.titleMedium
        )
        val address = listOfNotNull(
            charger.addressLine,
            charger.town
        ).joinToString(", ")
        if (address.isNotBlank()) {
            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Connectors: ${charger.connectorsSummary}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
