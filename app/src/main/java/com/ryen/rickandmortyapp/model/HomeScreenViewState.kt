package com.ryen.rickandmortyapp.model

import com.ryen.model.domain.Character

sealed interface HomeScreenViewState{
    data object Loading: HomeScreenViewState
    data class GridDisplay(val characters: List<Character> = emptyList()): HomeScreenViewState
}