@file:OptIn(ExperimentalFoundationApi::class)

package com.ryen.rickandmortyapp.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.rickandmortyapp.model.AllEpisodeViewState
import com.ryen.rickandmortyapp.presentation.components.common.LoadingState
import com.ryen.rickandmortyapp.presentation.components.common.SimpleToolbar
import com.ryen.rickandmortyapp.presentation.components.episode.EpisodeRowComponent
import com.ryen.rickandmortyapp.presentation.screens.viewModel.AllEpisodesScreenViewModel
import com.ryen.rickandmortyapp.ui.theme.RickAction
import com.ryen.rickandmortyapp.ui.theme.RickPrimary
import com.ryen.rickandmortyapp.ui.theme.RickSurface
import com.ryen.rickandmortyapp.ui.theme.RickTextPrimary

@Composable
fun AllEpisodesScreen(
    episodeViewModel: AllEpisodesScreenViewModel = hiltViewModel()
) {
    val uiState by episodeViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit){
        episodeViewModel.refreshAllEpisode()
    }

    when(val state = uiState){
        AllEpisodeViewState.Loading -> LoadingState()
        AllEpisodeViewState.Error -> {
            // TODO:
        }
        is AllEpisodeViewState.Success -> {
            Column {
                SimpleToolbar(title = "All episodes")
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.data.forEach{ mapEntry ->
                        val uniqueCharacterCount = mapEntry.value.flatMap { it.characterIdsInEpisode }.toSet().size
                        stickyHeader(key = mapEntry.key){
                            Column(modifier = Modifier.fillMaxWidth().background(color = RickPrimary)) {
                                Text(text = mapEntry.key, color = RickTextPrimary, fontSize = 42.sp)
                                Text(text = "$uniqueCharacterCount characters", color = RickTextPrimary, fontSize = 22.sp)
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .height(4.dp)
                                        .background(
                                    color = RickAction,
                                    shape = RoundedCornerShape(2.dp)
                                ))
                            }
                        }
                        mapEntry.value.forEach{ episode ->
                            item(key = episode.id) { EpisodeRowComponent(episode = episode) }
                        }
                    }
                }
            }
        }
    }
}