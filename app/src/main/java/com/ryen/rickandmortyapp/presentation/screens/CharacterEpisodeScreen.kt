@file:OptIn(ExperimentalFoundationApi::class)

package com.ryen.rickandmortyapp.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryen.model.domain.Character
import com.ryen.model.domain.Episode
import com.ryen.network.KtorClient
import com.ryen.rickandmortyapp.presentation.components.common.CharacterImage
import com.ryen.rickandmortyapp.presentation.components.common.CharacterNameComponent
import com.ryen.rickandmortyapp.presentation.components.common.DataPoint
import com.ryen.rickandmortyapp.presentation.components.common.DataPointComponent
import com.ryen.rickandmortyapp.presentation.components.common.LoadingState
import com.ryen.rickandmortyapp.presentation.components.common.SimpleToolbar
import com.ryen.rickandmortyapp.presentation.components.episode.EpisodeRowComponent
import com.ryen.rickandmortyapp.ui.theme.RickPrimary
import com.ryen.rickandmortyapp.ui.theme.RickTextPrimary
import kotlinx.coroutines.launch

@Composable
fun CharacterEpisodeScreen(
    characterId: Int,
    ktorClient: KtorClient,
    onBackClick: () -> Unit
    ) {
    var characterState by remember { mutableStateOf<Character?>(null) }
    var episodeState by remember { mutableStateOf<List<Episode>>(emptyList()) }

    LaunchedEffect(key1 = Unit, block = {
        ktorClient.getCharacter(characterId).onSuccess { character ->
            characterState = character
            launch{
                ktorClient.getEpisodes(episodeIds = character.episodeIds).onSuccess { episodes ->
                    episodeState = episodes
                }.onFailure {
                    // TODO:
                }
            }
        }.onFailure {
            // TODO:
        }
    })

    characterState?.let{ character ->
        MainScreen(character = character, episodes = episodeState, onBackClick = onBackClick)
    } ?: LoadingState()
}

@Composable
private fun MainScreen(
    character: Character,
    episodes: List<Episode>,
    onBackClick: ()-> Unit
    ) {
    val episodeBySeasonMap = episodes.groupBy { it.seasonNumber }
    Column {
        SimpleToolbar(title = "Episodes", onBackAction = onBackClick)
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            item { CharacterNameComponent(name = character.name) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                LazyRow {
                    episodeBySeasonMap.forEach { mapEntry ->
                        val title = "Season ${mapEntry.key}"
                        val description = "${mapEntry.value.size} ep"
                        item { DataPointComponent(dataPoint = DataPoint(title, description)) }
                        item { Spacer(modifier = Modifier.width(32.dp)) }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { CharacterImage(imageUrl = character.imageUrl) }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            episodeBySeasonMap.forEach { mapEntry ->
                stickyHeader { SeasonHeader(seasonNumber = mapEntry.key) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(mapEntry.value) { episode ->
                    EpisodeRowComponent(episode = episode)
                }
            }
        }
    }
}

@Composable
private fun SeasonHeader(seasonNumber: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = RickPrimary)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Season $seasonNumber",
            color = RickTextPrimary,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = RickTextPrimary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 4.dp)
        )
    }
}