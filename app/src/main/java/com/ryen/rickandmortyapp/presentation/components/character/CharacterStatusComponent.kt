package com.ryen.rickandmortyapp.presentation.components.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryen.model.domain.CharacterStatus
import com.ryen.rickandmortyapp.ui.theme.RickAndMortyAppTheme
import com.ryen.rickandmortyapp.ui.theme.RickTextPrimary

@Composable
fun CharacterStatusComponent(characterStatus: CharacterStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(IntrinsicSize.Max)
//            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = characterStatus.color, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text = "Status: ", fontSize = 20.sp, color = Color.LightGray)
        Text(text = characterStatus.displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RickTextPrimary)
    }
}









@Preview
@Composable
private fun CharacterStatusComponentPreviewAlive() {
    RickAndMortyAppTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Alive)
    }
}
@Preview
@Composable
private fun CharacterStatusComponentPreviewDead() {
    RickAndMortyAppTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Dead)
    }
}
@Preview
@Composable
private fun CharacterStatusComponentPreviewUnknown() {
    RickAndMortyAppTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Unknown)
    }
}