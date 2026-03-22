package com.example.disneyverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.disneyverse.navigation.AppNavGraph
import com.example.disneyverse.ui.theme.DisneyVerseTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination =
            if (FirebaseAuth.getInstance().currentUser != null) "home" else "login"

        setContent {
            DisneyVerseTheme {
                AppNavGraph(startDestination = startDestination)
            }
        }
    }
}