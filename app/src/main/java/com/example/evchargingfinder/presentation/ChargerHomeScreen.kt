package com.example.evchargingfinder.presentation

import androidx.compose.foundation.clickable
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
fun ChargerHomeScreen(
    viewModel: ChargerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showMap by remember { mutableStateOf(false) }
    var selectedCharger by remember { mutableStateOf<ChargerLocation?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {

        // ✅ Top buttons (List / Map)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showMap = false },
                modifier = Modifier.weight(1f),
                enabled = showMap
            ) {
                Text("List")
            }

            Button(
                onClick = { showMap = true },
                modifier = Modifier.weight(1f),
                enabled = !showMap
            ) {
                Text("Map")
            }
        }

        // ✅ UI State handling (Loading / Error / Success)
        when (val state = uiState) {

            is ChargerUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ChargerUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Failed to load chargers",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.message)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text("Retry")
                    }
                }
            }

            is ChargerUiState.Success -> {
                val chargers = state.chargers

                Column(modifier = Modifier.fillMaxSize()) {

                    // ✅ Charger count (nice Sprint 5 polish)
                    Text(
                        text = "${chargers.size} charging locations found",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )

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
    }

    // ✅ Details dialog (click list item)
    if (selectedCharger != null) {
        val c = selectedCharger!!

        val address = listOfNotNull(c.addressLine, c.town)
            .joinToString(", ")

        AlertDialog(
            onDismissRequest = { selectedCharger = null },
            confirmButton = {
                TextButton(onClick = { selectedCharger = null }) {
                    Text("Close")
                }
            },
            title = { Text(c.title) },
            text = {
                Column {
                    if (address.isNotBlank()) {
                        Text(address)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text("Connectors: ${c.connectorsSummary}")
                }
            }
        )
    }
}

@Composable
private fun ChargersList(
    chargers: List<ChargerLocation>,
    onChargerClick: (ChargerLocation) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(chargers) { charger ->
            ChargerListItem(
                charger = charger,
                onClick = { onChargerClick(charger) }
            )
        }
    }
}

@Composable
private fun ChargerListItem(
    charger: ChargerLocation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = charger.title,
                style = MaterialTheme.typography.titleMedium
            )

            val address = listOfNotNull(charger.addressLine, charger.town)
                .joinToString(", ")

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
}
