package com.davidmurray.omada.data.model

/**
 * Domain Model representing a Page of photos that can be seen on Flickr
 * @see Photo
 */
data class PhotoPage (
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val total: String,
    val photos: List<Photo>
)