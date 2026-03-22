package com.example.disneyverse.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

private val DetailGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF060B16),
        Color(0xFF10172B),
        Color(0xFF161D3A),
        Color(0xFF070910)
    )
)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailGradient)
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
                            text = film?.title ?: "Film detail",
                            maxLines = 1,
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
                film?.let { f ->
                    item {
                        FilmHeroCard(film = f, myStatus = myStatus)
                    }

                    item {
                        ActionSection(
                            onWatched = {
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
                            onWantToWatch = {
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
                            onOwn = {
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
                            onGetRid = {
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
                            onRemove = {
                                repo.removeFilmStatus(
                                    filmId = filmId,
                                    onSuccess = {
                                        message = "Status removed"
                                        reload()
                                    },
                                    onError = { message = it }
                                )
                            }
                        )
                    }

                    if (message.isNotEmpty()) {
                        item {
                            Surface(
                                color = Color(0xFF7E8BFF).copy(alpha = 0.16f),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text(
                                    text = message,
                                    color = Color.White,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }

                    item {
                        UsersSection(
                            title = "Users who own it",
                            users = owners
                        )
                    }

                    item {
                        UsersSection(
                            title = "Users who want to get rid of it",
                            users = getRidUsers
                        )
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
            }
        }
    }
}

@Composable
private fun FilmHeroCard(
    film: Film,
    myStatus: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f))
    ) {
        Column {
            Box {
                AsyncImage(
                    model = film.posterUrl,
                    contentDescription = film.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.20f),
                                    Color.Black.copy(alpha = 0.80f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    Text(
                        text = film.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        StatusChip(text = film.category.ifBlank { "Movie" })
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusChip(text = film.releaseDate)
                    }
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Description",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = film.description,
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color(0xFF6B7CFF).copy(alpha = 0.16f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "My status: ${myStatus ?: "none"}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ActionSection(
    onWatched: () -> Unit,
    onWantToWatch: () -> Unit,
    onOwn: () -> Unit,
    onGetRid: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Choose your status",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                userScrollEnabled = false,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(208.dp)
            ) {
                items(
                    listOf(
                        ActionButtonData("Watched", Icons.Rounded.CheckCircle, onWatched),
                        ActionButtonData("Want to watch", Icons.Rounded.FavoriteBorder, onWantToWatch),
                        ActionButtonData("Own", Icons.Rounded.Star, onOwn),
                        ActionButtonData("Get rid", Icons.Rounded.Delete, onGetRid)
                    )
                ) { item ->
                    Button(
                        onClick = item.onClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF202A52),
                            contentColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.label,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRemove,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Remove my status")
            }
        }
    }
}

private data class ActionButtonData(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun UsersSection(
    title: String,
    users: List<User>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (users.isEmpty()) {
                Surface(
                    color = Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "No users found for this status yet.",
                        color = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                users.forEach { user ->
                    UserRow(user = user)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun UserRow(user: User) {
    Surface(
        color = Color.White.copy(alpha = 0.06f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF6B7CFF).copy(alpha = 0.25f)
            ) {
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.username.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = user.username,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.email,
                    color = Color.White.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}