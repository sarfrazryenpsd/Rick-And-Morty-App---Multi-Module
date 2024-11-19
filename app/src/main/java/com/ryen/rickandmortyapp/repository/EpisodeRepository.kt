package com.ryen.rickandmortyapp.repository

import com.ryen.model.domain.Episode
import com.ryen.network.ApiOperations
import com.ryen.network.KtorClient
import javax.inject.Inject

class EpisodeRepository @Inject constructor(
    private val ktorClient: KtorClient
) {
    suspend fun fetchAllEpisodes(): ApiOperations<List<Episode>> = ktorClient.getAllEpisodes()
}