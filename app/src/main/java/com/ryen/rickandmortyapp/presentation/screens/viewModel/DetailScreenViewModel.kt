package com.ryen.rickandmortyapp.presentation.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.rickandmortyapp.model.CharacterDetailsViewState
import com.ryen.rickandmortyapp.presentation.components.common.DataPoint
import com.ryen.rickandmortyapp.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
): ViewModel(){
    private val _internalStorageFlow = MutableStateFlow<CharacterDetailsViewState>(
        value = CharacterDetailsViewState.Loading
    )
    val stateFlow = _internalStorageFlow.asStateFlow()

    fun fetchCharacter(characterId: Int) = viewModelScope.launch {
        _internalStorageFlow.update { return@update CharacterDetailsViewState.Loading }
        characterRepository.fetchCharacter(characterId).onSuccess { character ->
            val dataPoints = buildList {
                add(DataPoint(title = "Last Known Location", description = character.location.name))
                add(DataPoint(title = "Species", description = character.species))
                add(DataPoint(title = "Gender", description = character.gender.displayName))
                character.type.takeIf { it.isNotEmpty() }?.let { type ->
                    add(DataPoint(title = "Type", description = type))
                }
                add(DataPoint(title = "Origin", description = character.origin.name))
                add(DataPoint(title = "Status", description = character.status.displayName))
                add(DataPoint(title = "Episode Count", description = character.episodeIds.size.toString()))
            }
            _internalStorageFlow.update {
                return@update CharacterDetailsViewState.Success(
                    character = character,
                    characterDataPoints = dataPoints
                )
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update CharacterDetailsViewState.Error(
                    message = exception.message ?: "Unknown Error"
                )
            }
        }
    }
}