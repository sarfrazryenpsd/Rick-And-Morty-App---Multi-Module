package com.ryen.rickandmortyapp.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryen.model.domain.CharacterStatus
import com.ryen.rickandmortyapp.model.ScreenState
import com.ryen.rickandmortyapp.presentation.components.character.CharacterListItem
import com.ryen.rickandmortyapp.presentation.components.common.DataPoint
import com.ryen.rickandmortyapp.presentation.components.common.SimpleToolbar
import com.ryen.rickandmortyapp.presentation.screens.viewModel.SearchViewModel
import com.ryen.rickandmortyapp.ui.theme.RickAction
import com.ryen.rickandmortyapp.ui.theme.RickPrimary
import com.ryen.rickandmortyapp.ui.theme.RickTextPrimary

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel()
) {

    DisposableEffect(key1 = Unit) {
        val job = searchViewModel.observeUserSearch()
        onDispose { job.cancel() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleToolbar(title = "Search")

        val screenState by searchViewModel.uiState.collectAsStateWithLifecycle()

        AnimatedVisibility(visible = screenState is ScreenState.Searching) {
            LinearProgressIndicator(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth(),
                color = RickAction
            )
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Row (
                modifier = Modifier
                    .weight(1f)
                    .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search icon",
                    tint = RickPrimary
                )
                BasicTextField(
                    state = searchViewModel.searchTextFieldState,
                    modifier = Modifier.weight(1f),
                )
            }
            AnimatedVisibility(visible = searchViewModel.searchTextFieldState.text.isNotBlank()) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete icon",
                    tint = RickAction,
                    modifier = Modifier
                        .clickable {
                            searchViewModel.searchTextFieldState.edit { delete(0, length) }
                        }
                )
            }
        }

        when(val state = screenState){
            ScreenState.Empty -> {
                Text(
                    text = "Search for characters!",
                    color = RickTextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp
                )
            }
            ScreenState.Searching -> { }
            is ScreenState.Error -> {
                Text(
                    text = state.message,
                    color = RickTextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp
                )
                Button(
                    colors = ButtonDefaults.buttonColors().copy(containerColor = RickAction),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 84.dp),
                    onClick = { searchViewModel.searchTextFieldState.clearText() }
                ) {
                    Text(text = "Clear search", color = RickPrimary)
                }
            }
            is ScreenState.Content -> SearchScreenContent(
                content = state,
                onStatusSelected = searchViewModel::toggleStatus
            )
        }

    }
}
@Composable
private fun SearchScreenContent(
    content: ScreenState.Content,
    onStatusSelected: (CharacterStatus) -> Unit
    ) {
    Text(
        text = "${content.results.size} results for '${content.userQuery}'",
        color = RickTextPrimary,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
        fontSize = 14.sp,
        maxLines = 1
    )

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ){
        content.filterState.statuses.forEach { status ->
            val isSelected = content.filterState.selectedStatuses.contains(status)
            val contentColor = if (isSelected) RickAction else Color.LightGray
            val count = content.results.filter { it.status == status }.size

            Row (
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = contentColor,
                        shape = RoundedCornerShape(8.dp))
                    .clickable { onStatusSelected(status) }
                    .clip(RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = count.toString(),
                    color = RickPrimary,
                    modifier = Modifier
                        .background(color = contentColor)
                        .padding(4.dp),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = status.displayName,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 6.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    Box {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 8.dp),
            modifier = Modifier.clipToBounds()
        ) {
            val filteredResults = content.results.filter { content.filterState.selectedStatuses.contains(it.status) }
            items(
                items = filteredResults,
                key = { it.id }
            ) { character ->
                val dataPoints = buildList {
                    add(DataPoint("Last known location", character.location.name))
                    add(DataPoint("Species", character.species))
                    add(DataPoint("Gender", character.gender.displayName))
                    character.type.takeIf { it.isNotEmpty() }?.let { type ->
                        add(DataPoint("Type", type))
                    }
                    add(DataPoint("Origin", character.origin.name))
                    add(DataPoint("Episode count", character.episodeIds.size.toString()))
                }
                CharacterListItem(
                    character = character,
                    characterDataPoints = dataPoints,
                    onClick = {  },
                    modifier = Modifier.animateItem()
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(RickPrimary, Color.Transparent))
                )
        )
    }
}

