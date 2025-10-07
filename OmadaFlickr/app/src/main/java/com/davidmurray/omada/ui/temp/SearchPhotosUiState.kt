package com.davidmurray.omada.ui.temp

import com.davidmurray.omada.data.model.PhotoPage

data class SearchPhotosUiState (
    val isLoading: Boolean = false,
    val data: PhotoPage? = null,
    val error: String? = null
)