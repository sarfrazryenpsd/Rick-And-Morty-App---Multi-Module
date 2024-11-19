package com.ryen.rickandmortyapp.model

import com.ryen.model.domain.Character
import com.ryen.model.domain.CharacterStatus

sealed interface SearchState {
    data object Empty: SearchState
    data class UserQuery(val query: String): SearchState
}

sealed interface ScreenState {
    data object Empty: ScreenState
    data object Searching: ScreenState
    data class Error(val message: String): ScreenState
    data class Content(
        val userQuery: String,
        val results: List<Character>,
        val filterState: FilterState
    ): ScreenState{
        data class FilterState(
            val statuses: List<CharacterStatus>,
            val selectedStatuses: List<CharacterStatus>
        )
    }
}