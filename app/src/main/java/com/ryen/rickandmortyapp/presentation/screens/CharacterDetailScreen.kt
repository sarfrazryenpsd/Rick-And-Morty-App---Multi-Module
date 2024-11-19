package com.ryen.rickandmortyapp.presentation.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import com.ryen.rickandmortyapp.model.CharacterDetailsViewState
import com.ryen.rickandmortyapp.presentation.components.character.CharacterDetailsNamePlateComponent
import com.ryen.rickandmortyapp.presentation.components.common.DataPointComponent
import com.ryen.rickandmortyapp.presentation.components.common.LoadingState
import com.ryen.rickandmortyapp.presentation.components.common.SimpleToolbar
import com.ryen.rickandmortyapp.presentation.screens.viewModel.CharacterDetailsViewModel
import com.ryen.rickandmortyapp.ui.theme.RickAction

@Composable
fun CharacterDetailScreen(
    characterId: Int,
    viewModel: CharacterDetailsViewModel = hiltViewModel(),
    onEpisodeClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {

    LaunchedEffect(key1 = Unit, block = {
        viewModel.fetchCharacter(characterId)
    })

    val state by viewModel.stateFlow.collectAsState()

    Column {
        SimpleToolbar(title = "Character details", onBackAction = onBackClick)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            when (val viewState = state) {
                CharacterDetailsViewState.Loading -> item { LoadingState() }
                is CharacterDetailsViewState.Error -> {
                    // TODO:
                }

                is CharacterDetailsViewState.Success -> {
                    //Name plate
                    item {
                        CharacterDetailsNamePlateComponent(
                            name = viewState.character.name,
                            status = viewState.character.status
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    //Image
                    item {
                        SubcomposeAsyncImage(
                            model = viewState.character.imageUrl,
                            contentDescription = "Character Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            loading = { LoadingState() }
                        )
                    }
                    //DataPoints
                    items(viewState.characterDataPoints) {
                        Spacer(modifier = Modifier.height(32.dp))
                        DataPointComponent(dataPoint = it)
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    //Button
                    item {
                        Text(
                            text = "View all episodes",
                            color = RickAction,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .border(
                                    width = 1.dp,
                                    color = RickAction,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onEpisodeClick(characterId) }
                                .padding(8.dp)
                                .fillMaxWidth()

                        )
                    }
                }
            }
        }
    }

}