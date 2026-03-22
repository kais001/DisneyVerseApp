package com.example.disneyverse.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.disneyverse.data.FirebaseRepository
import com.example.disneyverse.model.Universe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUniverseClick: (String, String) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val repo = remember { FirebaseRepository() }
    var universes by remember { mutableStateOf<List<Universe>>(emptyList()) }

    LaunchedEffect(Unit) {
        repo.getUniverses {
            universes = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DisneyVerse") },
                actions = {
                    TextButton(onClick = onProfileClick) { Text("Profile") }
                    TextButton(onClick = {
                        repo.logout()
                        onLogout()
                    }) { Text("Logout") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(universes) { universe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable {
                            onUniverseClick(universe.id, universe.name)
                        }
                ) {
                    Column {
                        AsyncImage(
                            model = universe.imageUrl,
                            contentDescription = universe.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                        )
                        Text(
                            text = universe.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}