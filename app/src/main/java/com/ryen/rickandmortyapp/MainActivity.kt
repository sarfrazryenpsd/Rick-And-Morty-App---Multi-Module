package com.ryen.rickandmortyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryen.model.domain.Character
import com.ryen.network.KtorClient
import com.ryen.rickandmortyapp.ui.theme.RickAndMortyAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var character by remember { mutableStateOf<Character?>(null) }

            LaunchedEffect(key1 = Unit, block = {
                delay(3000)
                val id = (0..100).random()
                character = ktorClient.getCharacter(id)
            })
            RickAndMortyAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column{ Spacer(modifier = Modifier.height(40.dp))
                        Text(text = character?.name ?: "No Character") }
                }
            }
        }
    }
}
