package com.ryen.rickandmortyapp.presentation.components.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryen.model.domain.CharacterStatus
import com.ryen.rickandmortyapp.ui.theme.RickAndMortyAppTheme

@Composable
fun CharacterStatusCircle(status: CharacterStatus, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(listOf(Color.Black, Color.Transparent)),
                shape = CircleShape
            )
            .size(20.dp),
        contentAlignment = Alignment.Center
    ){
        Box(modifier = modifier
            .size(6.dp)
            .background(color = status.color, shape = CircleShape))
    }
}

@Preview
@Composable
private fun CharacterStatusCircleAlivePrev() {
    Column {
        CharacterStatusCircle(status = CharacterStatus.Alive)
        Spacer(modifier = Modifier.height(16.dp))
        CharacterStatusCircle(status = CharacterStatus.Unknown)
        Spacer(modifier = Modifier.height(16.dp))
        CharacterStatusCircle(status = CharacterStatus.Dead)
    }
}