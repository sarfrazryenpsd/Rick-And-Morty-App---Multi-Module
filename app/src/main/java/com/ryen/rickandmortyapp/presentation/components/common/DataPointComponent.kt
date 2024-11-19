package com.ryen.rickandmortyapp.presentation.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ryen.rickandmortyapp.ui.theme.RickAction
import com.ryen.rickandmortyapp.ui.theme.RickAndMortyAppTheme
import com.ryen.rickandmortyapp.ui.theme.RickTextPrimary

data class DataPoint(
    val title: String,
    val description: String
)

@Composable
fun DataPointComponent(dataPoint: DataPoint) {
    Column {
        Text(
            text = dataPoint.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = RickAction
        )
        Text(
            text = dataPoint.description,
            fontSize = 24.sp,
            color = RickTextPrimary
        )
    }
}

@Preview
@Composable
private fun DataPointComponentPreview() {
    RickAndMortyAppTheme {
        DataPointComponent(dataPoint = DataPoint(title = "Last Known Location", description = "Citadel of Ricks"))

    }
}