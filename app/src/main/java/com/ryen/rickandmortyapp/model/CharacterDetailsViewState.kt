package com.ryen.rickandmortyapp.model

import com.ryen.model.domain.Character
import com.ryen.rickandmortyapp.presentation.components.common.DataPoint

sealed interface CharacterDetailsViewState{
    data object Loading: CharacterDetailsViewState
    data class Error(val message: String): CharacterDetailsViewState
    data class Success(
        val character: Character,
        val characterDataPoints: List<DataPoint>
    ): CharacterDetailsViewState
}