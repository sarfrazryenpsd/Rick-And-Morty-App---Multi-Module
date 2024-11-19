package com.ryen.network

import com.ryen.model.domain.Character
import com.ryen.model.domain.CharacterPage
import com.ryen.model.domain.Episode
import com.ryen.model.domain.EpisodePage
import com.ryen.model.remote.RemoteCharacter
import com.ryen.model.remote.RemoteCharacterPage
import com.ryen.model.remote.RemoteEpisode
import com.ryen.model.remote.RemoteEpisodePage
import com.ryen.model.remote.toDomainCharacter
import com.ryen.model.remote.toDomainCharacterPage
import com.ryen.model.remote.toDomainEpisode
import com.ryen.model.remote.toDomainEpisodePage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorClient {
    private val client = HttpClient(OkHttp){
        defaultRequest { url("https://rickandmortyapi.com/api/") }

        install(Logging){
            logger = Logger.SIMPLE
        }

        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private var characterCache = mutableMapOf<Int,Character>()

    suspend fun getCharacter(id: Int): ApiOperations<Character> {
        characterCache[id]?.let { return ApiOperations.Success( it) }
        return safeApiCall{
            client.get("character/$id")
                .body<RemoteCharacter>()
                .toDomainCharacter()
                .also { characterCache[id] = it }
        }
    }

    suspend fun getCharacterByPage(
        pageNumber: Int,
        queryParams: Map<String, String>
    ): ApiOperations<CharacterPage> {
        return safeApiCall {
            client.get("character") {
                url {
                    parameters.append("page", pageNumber.toString())
                    queryParams.forEach { parameters.append(it.key, it.value) }
                }
            }
                .body<RemoteCharacterPage>()
                .toDomainCharacterPage()
        }
    }

    suspend fun searchAllCharactersByName(searchQuery: String): ApiOperations<List<Character>> {
        val data = mutableListOf<Character>()
        var exception: Exception? = null

        getCharacterByPage(
            pageNumber = 1,
            queryParams = mapOf("name" to searchQuery)
        ).onSuccess { firstPage ->
            val totalPageCount = firstPage.info.pages
            data.addAll(firstPage.characters)

            repeat(totalPageCount - 1) { index ->
                getCharacterByPage(
                    pageNumber = index + 2,
                    queryParams = mapOf("name" to searchQuery)
                ).onSuccess { nextPage ->
                    data.addAll(nextPage.characters)
                }.onFailure { error ->
                    exception = error
                }

                if (exception != null) { return@onSuccess }
            }
        }.onFailure {
            exception = it
        }

        return exception?.let { ApiOperations.Failure(it) } ?: ApiOperations.Success(data)
    }

    private suspend fun getEpisode(episodeId: Int): ApiOperations<Episode> {
        return safeApiCall{
            client.get("episode/$episodeId")
                .body<RemoteEpisode>()
                .toDomainEpisode()
        }
    }

    suspend fun getEpisodes(episodeIds: List<Int>): ApiOperations<List<Episode>> {
        return if (episodeIds.size==1){
            getEpisode(episodeIds.first()).mapSuccess {
                listOf(it)
            }
        }
        else{
            val idsCommaSeparated = episodeIds.joinToString(",")
            safeApiCall{
                client.get("episode/$idsCommaSeparated")
                    .body<List<RemoteEpisode>>().map {
                        it.toDomainEpisode()
                    }
            }
        }

    }

    private suspend fun getEpisodesByPage(
        pageIndex: Int,
        ): ApiOperations<EpisodePage> {
        return safeApiCall {
            client.get("episode"){
                url {
                    parameters.append("page", pageIndex.toString())
                }
            }
                .body<RemoteEpisodePage>()
                .toDomainEpisodePage()
        }
    }

    suspend fun getAllEpisodes(): ApiOperations<List<Episode>> {
        val data = mutableListOf<Episode>()
        var exception : Exception? = null

        getEpisodesByPage(pageIndex = 1).onSuccess { firstPage ->
            val totalPageCount = firstPage.info.pages
            data.addAll(firstPage.episodes)

            repeat(totalPageCount - 1){ index ->
                getEpisodesByPage(pageIndex = index + 2).onSuccess { nextPage ->
                    data.addAll(nextPage.episodes)
                }.onFailure { error ->
                    exception = error
                }
                if(exception==null) return@onSuccess
            }
        }.onFailure {
            exception = it
        }
        return exception?.let { ApiOperations.Failure(it) } ?: ApiOperations.Success(data)
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): ApiOperations<T> {
        return try {
            ApiOperations.Success(data = apiCall())
        }catch (e: Exception){
            ApiOperations.Failure(exception = e)
        }
    }
}

sealed interface ApiOperations<T> {
    data class Success<T>(val data: T) : ApiOperations<T>
    data class Failure<T>(val exception: Exception) : ApiOperations<T>

    fun <R> mapSuccess(transform: (T) -> R): ApiOperations<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Failure -> Failure(exception)
        }
    }

    suspend fun onSuccess(block: suspend (T) -> Unit): ApiOperations<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): ApiOperations<T> {
        if (this is Failure) block(exception)
        return this
    }
}