package com.ryen.rickandmortyapp.presentation.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.rickandmortyapp.model.AllEpisodeViewState
import com.ryen.rickandmortyapp.repository.CharacterRepository
import com.ryen.rickandmortyapp.repository.EpisodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllEpisodesScreenViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<AllEpisodeViewState>(AllEpisodeViewState.Loading)
    val uiState = _uiState.asStateFlow()

    fun refreshAllEpisode(forceRefresh: Boolean = false) = viewModelScope.launch {
        if(forceRefresh){ _uiState.update { AllEpisodeViewState.Loading } }
        episodeRepository.fetchAllEpisodes().onSuccess { episodeList ->
            _uiState.update {
                AllEpisodeViewState.Success(
                    data = episodeList.groupBy {
                        it.seasonNumber.toString() /*  -> ["1", listOf()]   */
                    }.mapKeys {
                        "Season ${it.key}"         /* ->  ["Season 1", listOf()]   */
                    }
                )
            }
        }.onFailure {
            _uiState.update { AllEpisodeViewState.Error }
        }
    }
}