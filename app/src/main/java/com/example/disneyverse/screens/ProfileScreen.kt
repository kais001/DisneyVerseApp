package com.example.disneyverse.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.disneyverse.data.FirebaseRepository
import com.example.disneyverse.model.Film
import com.example.disneyverse.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onFilmClick: (String) -> Unit
) {
    val repo = remember { FirebaseRepository() }

    var user by remember { mutableStateOf<User?>(null) }
    var watched by remember { mutableStateOf(0) }
    var wantToWatch by remember { mutableStateOf(0) }
    var own by remember { mutableStateOf(0) }
    var getRid by remember { mutableStateOf(0) }
    var ownedFilms by remember { mutableStateOf<List<Film>>(emptyList()) }

    LaunchedEffect(Unit) {
        repo.getMyStatusesAndOwnedFilms { u, w, wt, o, g, films ->
            user = u
            watched = w
            wantToWatch = wt
            own = o
            getRid = g
            ownedFilms = films
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    TextButton(onClick = {
                        repo.logout()
                        onLogout()
                    }) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(user?.username ?: "")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(user?.email ?: "")
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Watched: $watched")
                    Text("Want to watch: $wantToWatch")
                    Text("Own: $own")
                    Text("Want to get rid of: $getRid")
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("My owned films")
                }
            }

            items(ownedFilms) { film ->
                ListItem(
                    headlineContent = { Text(film.title) },
                    supportingContent = { Text(film.releaseDate) },
                    modifier = Modifier.clickable {
                        onFilmClick(film.id)
                    }
                )
            }
        }
    }
}