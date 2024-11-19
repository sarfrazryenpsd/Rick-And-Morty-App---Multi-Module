@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.ryen.rickandmortyapp.presentation.screens.viewModel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.model.domain.CharacterStatus
import com.ryen.rickandmortyapp.model.ScreenState
import com.ryen.rickandmortyapp.model.SearchState
import com.ryen.rickandmortyapp.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    val uiState = _uiState.asStateFlow()

    val searchTextFieldState = TextFieldState()

    private val searchTextState: StateFlow<SearchState> = snapshotFlow { searchTextFieldState.text }
        .debounce(500)
        .mapLatest { if (it.isBlank()) SearchState.Empty else SearchState.UserQuery(it.toString()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchState.Empty
        )

    fun observeUserSearch() = viewModelScope.launch { //return a job
        searchTextState.collectLatest { searchState ->
            when(searchState){
                is SearchState.Empty -> { _uiState.update { ScreenState.Empty } }
                is SearchState.UserQuery -> searchAllCharacters(searchState.query)
            }
        }
    }

    fun toggleStatus(status: CharacterStatus){
        _uiState.update {
            val currentState = (it as? ScreenState.Content) ?: return@update it
            val currentSelectedStatuses = currentState.filterState.selectedStatuses
            val newSelectedStatuses = if (currentSelectedStatuses.contains(status)) {
                currentSelectedStatuses - status
            } else {
                currentSelectedStatuses + status
            }
            return@update currentState.copy(
                filterState = currentState.filterState.copy(selectedStatuses = newSelectedStatuses)
            )
        }
    }

    fun searchAllCharacters(query: String) = viewModelScope.launch {
        _uiState.update { ScreenState.Searching }
        delay(2000)
        characterRepository.fetchAllCharactersByName(searchQuery = query).onSuccess { characters ->
            val allStatuses = characters.map { it.status }.toSet().toList().sortedBy { it.displayName }
            _uiState.update { ScreenState.Content(
                userQuery = query,
                results = characters,
                filterState = ScreenState.Content.FilterState(
                    statuses = allStatuses,
                    selectedStatuses = allStatuses
                )
            ) }
        }.onFailure { exception ->
            _uiState.update { ScreenState.Error("No search results found") }
        }
    }
}