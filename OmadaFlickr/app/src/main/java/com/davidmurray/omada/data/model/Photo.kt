package com.davidmurray.omada.data.model

/** Domain Model representing a Photo that can be seen on Flickr */
data class Photo(
    val id: String,
    val title: String,
    val urlThumb: String? = null,
    val urlMedium: String? = null
)