package com.davidmurray.omada.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.davidmurray.omada.R
import com.davidmurray.omada.data.model.Photo


/**
 * UI for the Flickr Search Photos screen -- shows a search bar with a button triggering a search
 * When there are photos available, it shows them in a scrollable view
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPhotosScreen(
    state: SearchPhotosScreenState,
    onTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLoadMore: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.omada_dark),
                    titleContentColor = Color.White
                )
            )
        },
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            var text by remember { mutableStateOf(TextFieldValue(state.text)) }
            val keyboardController = LocalSoftwareKeyboardController.current

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onTextChange(it.text)
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text(stringResource(R.string.search_photos)) },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onSearch()
                        }
                    )
                )
                Button(
                    onClick = {
                        keyboardController?.hide()
                        onSearch()
                    }, colors = ButtonColors(
                        containerColor = colorResource(R.color.omada_orange),
                        contentColor = colorResource(R.color.white),
                        disabledContainerColor = colorResource(R.color.omada_orange),
                        disabledContentColor = colorResource(R.color.omada_orange),
                    )
                ) {
                    Text(stringResource(R.string.go))
                }
            }

            Spacer(Modifier.height(12.dp))

            when {
                state.isLoading -> {
                    if (state.photos.isEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                state.error != null -> {
                    Text(state.error)
                }
                else -> {
                    PhotoGrid(state, onLoadMore)
                }
            }
        }
    }
}

/** Display the Grid of Photos, requesting more when it reaches the near bottom of the list */
@Composable
private fun PhotoGrid(state: SearchPhotosScreenState,
                      onLoadMore: () -> Unit) {
    val gridState = rememberLazyGridState()

    // Trigger load more when near the bottom
    LaunchedEffect(gridState, state.photos.size) {
        snapshotFlow {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleIndex ->
            val totalCount = state.photos.size
            val threshold = 5 // how close to the end before loading more
            if (lastVisibleIndex != null && lastVisibleIndex >= totalCount - threshold) {
                onLoadMore()
            }
        }
    }

    // 3-column grid of square tiles
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = state.photos.size,
            key = { i -> state.photos[i].id }
        ) { i ->
            val photo = state.photos[i]
            PhotoGridItem(photo = photo)
        }
    }
}


/** Composable for displaying Photo with the Title overlaid, if it exists */
@Composable
@OptIn(ExperimentalGlideComposeApi::class)
private fun PhotoGridItem(photo: Photo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Gray)
    ) {
        GlideImage(
            model = photo.urlThumb,
            contentDescription = photo.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().padding(1.dp)
        )
        Text(
            text = photo.title,
            // make the white font visible by adding a black drop shadow
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(x = 3.0f, y = 3.0f)
                )
            ),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(4.dp),
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}