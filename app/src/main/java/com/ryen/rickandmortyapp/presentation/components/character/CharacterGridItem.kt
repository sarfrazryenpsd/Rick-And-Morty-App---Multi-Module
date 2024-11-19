package com.ryen.rickandmortyapp.presentation.components.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryen.model.domain.Character
import com.ryen.model.domain.CharacterStatus
import com.ryen.rickandmortyapp.presentation.components.common.CharacterImage
import com.ryen.rickandmortyapp.ui.theme.RickAction

@Composable
fun CharacterGridItem(
    modifier: Modifier = Modifier,
    character: Character,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(colors = listOf(Color.Transparent, RickAction)),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(){
            CharacterImage(imageUrl = character.imageUrl)
            CharacterStatusCircle(
                status = character.status,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 6.dp, top = 6.dp))
        }
        Text(
            text = character.name,
            color = RickAction,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 26.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

