package com.davidmurray.omada.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davidmurray.omada.OmadaChallengeApplication
import com.davidmurray.omada.ui.search.SearchPhotosScreen
import com.davidmurray.omada.ui.search.SearchPhotosViewModel
import com.davidmurray.omada.ui.theme.OmadaFlickrTheme

/**
 * Main Activity class for UI of the Omada Health Coding Challenge
 */
class OmadaChallengeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmadaFlickrTheme {
                val photoRepo = (application as OmadaChallengeApplication).photoRepository
                val searchPhotosViewModel: SearchPhotosViewModel = viewModel(factory = SearchPhotosViewModel.Factory(photoRepo))

                val state = searchPhotosViewModel.state.collectAsState()
                SearchPhotosScreen(
                    state = state.value,
                    onTextChange = { text ->
                        searchPhotosViewModel.onTextChange(text)
                    },
                    onSearch = {
                        searchPhotosViewModel.searchPhotos(reset = true)
                    },
                    onLoadMore = {
                        searchPhotosViewModel.searchPhotos(reset = false)
                    }
                )
            }
        }
    }
}