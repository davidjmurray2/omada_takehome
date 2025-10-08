package com.davidmurray.omada.ui.search

import com.davidmurray.omada.data.model.Photo

/** Screen state of the Flickr Search Photos Screen */
data class SearchPhotosScreenState(
    val text: String = "",
    val isLoading: Boolean = false,
    val photos: List<Photo> = emptyList(),
    val error: String? = null
)