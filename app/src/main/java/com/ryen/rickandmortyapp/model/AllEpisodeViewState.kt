package com.ryen.rickandmortyapp.model

import com.ryen.model.domain.Episode

sealed interface AllEpisodeViewState {
    data object Loading : AllEpisodeViewState
    data object Error: AllEpisodeViewState
    data class Success(val data:Map<String, List<Episode>>) : AllEpisodeViewState
}