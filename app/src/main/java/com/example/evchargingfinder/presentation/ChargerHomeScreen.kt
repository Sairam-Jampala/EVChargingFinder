package com.example.evchargingfinder.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evchargingfinder.domain.ChargerLocation

// ---------- MAIN SCREEN ----------

@Composable
fun ChargerHomeScreen() {
    val viewModel: ChargerViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var showMap by remember { mutableStateOf(false) }

    // selected charger for details dialog
    var selectedCharger by remember { mutableStateOf<ChargerLocation?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is ChargerUiState.Loading -> LoadingState()

            is ChargerUiState.Error -> ErrorState(
                message = (uiState as ChargerUiState.Error).message
            )

            is ChargerUiState.Success -> {
                val chargers = (uiState as ChargerUiState.Success).chargers

                Column(modifier = Modifier.fillMaxSize()) {
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
                        ChargerMapScreen(chargers = chargers)
                    } else {
                        ChargersList(
                            chargers = chargers,
                            onChargerClick = { selectedCharger = it }
                        )
                    }
                }
            }
        }

        // shared details dialog for list + map
        if (selectedCharger != null) {
            ChargerDetailsDialog(
                charger = selectedCharger!!,
                onDismiss = { selectedCharger = null }
            )
        }
    }
}

// ---------- LIST + ROW ----------

@Composable
private fun ChargersList(
    chargers: List<ChargerLocation>,
    onChargerClick: (ChargerLocation) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(chargers, key = { it.id }) { charger ->
            ChargerRow(
                charger = charger,
                onClick = { onChargerClick(charger) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ChargerRow(
    charger: ChargerLocation,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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

// ---------- SIMPLE STATES ----------

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading nearby chargers…")
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Failed to load chargers",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ---------- DETAILS DIALOG ----------

@Composable
private fun ChargerDetailsDialog(
    charger: ChargerLocation,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(charger.title) },
        text = {
            Column {
                val address = listOfNotNull(
                    charger.addressLine,
                    charger.town
                ).joinToString(", ")

                if (address.isNotBlank()) {
                    Text(address)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Connectors: ${charger.connectorsSummary}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
