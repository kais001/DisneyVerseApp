package com.example.disneyverse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.disneyverse.data.FirebaseRepository
import com.example.disneyverse.model.Film
import com.example.disneyverse.model.User

private val ProfileGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF08101E),
        Color(0xFF10172E),
        Color(0xFF191F43),
        Color(0xFF080A13)
    )
)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    ),
                    title = {
                        Text(
                            text = "Profile",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Surface(
                                color = Color.White.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Box(modifier = Modifier.padding(8.dp)) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                repo.logout()
                                onLogout()
                            }
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Box(modifier = Modifier.padding(8.dp)) {
                                    Icon(
                                        imageVector = Icons.Rounded.ExitToApp,
                                        contentDescription = "Logout",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.statusBarsPadding()
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    ProfileHeroCard(user = user)
                }

                item {
                    StatsRow(
                        watched = watched,
                        wantToWatch = wantToWatch,
                        own = own,
                        getRid = getRid
                    )
                }

                item {
                    OwnedCollectionHeader(count = ownedFilms.size)
                }

                if (ownedFilms.isEmpty()) {
                    item {
                        Surface(
                            color = Color.White.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "No owned films yet.",
                                color = Color.White.copy(alpha = 0.78f),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(ownedFilms) { film ->
                                OwnedFilmCard(
                                    film = film,
                                    onClick = { onFilmClick(film.id) }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(10.dp)) }
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF6B7CFF).copy(alpha = 0.22f)
                ) {
                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.username?.take(1)?.uppercase() ?: "?",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = user?.username ?: "",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user?.email ?: "",
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                color = Color(0xFF8CA8FF).copy(alpha = 0.16f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Your DisneyVerse collection overview",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsRow(
    watched: Int,
    wantToWatch: Int,
    own: Int,
    getRid: Int
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        item { StatCard("Watched", watched.toString()) }
        item { StatCard("Wishlist", wantToWatch.toString()) }
        item { StatCard("Owned", own.toString()) }
        item { StatCard("Get rid", getRid.toString()) }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.07f)
        )
    ) {
        Column(
            modifier = Modifier
                .width(132.dp)
                .padding(18.dp)
        ) {
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun OwnedCollectionHeader(count: Int) {
    Column {
        Text(
            text = "My owned films",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "$count item(s) in your personal collection",
            color = Color.White.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun OwnedFilmCard(
    film: Film,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.07f)
        )
    ) {
        Column {
            AsyncImage(
                model = film.posterUrl,
                contentDescription = film.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = film.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = film.releaseDate,
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}