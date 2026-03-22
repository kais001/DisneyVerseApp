package com.example.disneyverse.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.disneyverse.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmDetailScreen(
    filmId: String,
    onBack: () -> Unit
) {
    val repo = remember { FirebaseRepository() }

    var film by remember { mutableStateOf<Film?>(null) }
    var myStatus by remember { mutableStateOf<String?>(null) }
    var owners by remember { mutableStateOf<List<User>>(emptyList()) }
    var getRidUsers by remember { mutableStateOf<List<User>>(emptyList()) }
    var message by remember { mutableStateOf("") }

    fun reload() {
        repo.getFilmById(filmId) { film = it }
        repo.getCurrentUserFilmStatus(filmId) { myStatus = it }
        repo.getOwnersAndGetRidUsers(filmId) { o, g ->
            owners = o
            getRidUsers = g
        }
    }

    LaunchedEffect(filmId) {
        reload()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(film?.title ?: "Film detail") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                film?.let { f ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(
                            model = f.posterUrl,
                            contentDescription = f.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(f.title, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Release date: ${f.releaseDate}")
                        Text("Category: ${f.category}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(f.description)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("My status: ${myStatus ?: "none"}")

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                repo.saveFilmStatus(
                                    filmId = filmId,
                                    status = "watched",
                                    onSuccess = {
                                        message = "Status saved"
                                        reload()
                                    },
                                    onError = { message = it }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Watched")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                repo.saveFilmStatus(
                                    filmId = filmId,
                                    status = "want_to_watch",
                                    onSuccess = {
                                        message = "Status saved"
                                        reload()
                                    },
                                    onError = { message = it }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Want to watch")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                repo.saveFilmStatus(
                                    filmId = filmId,
                                    status = "own",
                                    onSuccess = {
                                        message = "Status saved"
                                        reload()
                                    },
                                    onError = { message = it }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Own on DVD/Blu-ray")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                repo.saveFilmStatus(
                                    filmId = filmId,
                                    status = "want_to_get_rid",
                                    onSuccess = {
                                        message = "Status saved"
                                        reload()
                                    },
                                    onError = { message = it }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Want to get rid of")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                repo.removeFilmStatus(
                                    filmId = filmId,
                                    onSuccess = {
                                        message = "Status removed"
                                        reload()
                                    },
                                    onError = { message = it }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Remove my status")
                        }

                        if (message.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(message)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Users who own it", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            items(owners) { user ->
                ListItem(
                    headlineContent = { Text(user.username) },
                    supportingContent = { Text(user.email) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    "Users who want to get rid of it",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(getRidUsers) { user ->
                ListItem(
                    headlineContent = { Text(user.username) },
                    supportingContent = { Text(user.email) }
                )
            }
        }
    }
}