package com.ryen.rickandmortyapp.repository

import com.ryen.model.domain.Character
import com.ryen.model.domain.CharacterPage
import com.ryen.network.ApiOperations
import com.ryen.network.KtorClient
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val ktorClient: KtorClient
){
    suspend fun fetchCharacterByPage(
        page: Int,
        params: Map<String, String> = emptyMap()

    ): ApiOperations<CharacterPage> {
        return ktorClient.getCharacterByPage(pageNumber = page, queryParams = params)
    }

    suspend fun fetchCharacter(characterId: Int): ApiOperations<Character> {
        return ktorClient.getCharacter(id = characterId)
    }

    suspend fun fetchAllCharactersByName(searchQuery: String): ApiOperations<List<Character>> {
        return ktorClient.searchAllCharactersByName(searchQuery = searchQuery)
    }
}