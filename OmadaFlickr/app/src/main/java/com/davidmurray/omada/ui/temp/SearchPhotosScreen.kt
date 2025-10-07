package com.davidmurray.omada.ui.temp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davidmurray.omada.OmadaChallengeApplication
import com.davidmurray.omada.data.model.Photo

@Composable
fun PhotosScreen(modifier: Modifier = Modifier) {
    // Temporary -- this screen will be heavily modified, not intended as final appearance
    val app = LocalContext.current.applicationContext as OmadaChallengeApplication
    val vm: SearchPhotosViewModel = viewModel(
        factory = SearchPhotosViewModel.Factory(app.photoRepository)
    )
    val state by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.loadPosts() }

    val data = state.data
    when {
        state.isLoading || data == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.error != null -> {
            Text(
                text = "Error: ${state.error}",
                modifier = Modifier.padding(16.dp)
            )
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(data.photos) { photo ->
                    PhotoRow(photo)
                }
            }
        }
    }
}

@Composable
private fun PhotoRow(photo: Photo) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(photo.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            val dataFields =
            "id = ${photo.id}, \n" +
            "title = ${photo.title}, \n" +
            "urlThumb = ${photo.urlThumb}, \n" +
            "urlMedium = ${photo.urlMedium}"
            Text(dataFields, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
