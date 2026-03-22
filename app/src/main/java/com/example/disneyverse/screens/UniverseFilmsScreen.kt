package com.example.disneyverse.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.disneyverse.model.Film

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniverseFilmsScreen(
    universeId: String,
    universeName: String,
    onBack: () -> Unit,
    onFilmClick: (String) -> Unit
) {
    val repo = remember { FirebaseRepository() }
    var films by remember { mutableStateOf<List<Film>>(emptyList()) }

    LaunchedEffect(universeId) {
        repo.getFilmsByUniverse(universeId) {
            films = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(universeName) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(films) { film ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable { onFilmClick(film.id) }
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = film.posterUrl,
                            contentDescription = film.title,
                            modifier = Modifier
                                .width(100.dp)
                                .height(140.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(film.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Release: ${film.releaseDate}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Category: ${film.category}")
                        }
                    }
                }
            }
        }
    }
}