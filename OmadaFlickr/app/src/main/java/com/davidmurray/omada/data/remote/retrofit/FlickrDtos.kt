package com.davidmurray.omada.data.remote.retrofit

import com.google.gson.annotations.SerializedName

data class FlickrResponse (
    val photos: FlickrPhotosPage?,
    val stat: String?,
    val code: Int? = null,
    val message: String? = null
)

data class FlickrPhotosPage(
    val page: Int,
    val pages: Int,
    @SerializedName("perpage") val perPage: Int,
    val total: Int,
    val photo: List<FlickrPhotoDto>
)

data class FlickrPhotoDto(
    val id: String,
    val owner: String? = null,
    val secret: String? = null,
    val server: String? = null,
    val title: String,
    @SerializedName("ispublic") val isPublic: Int = 0,
    @SerializedName("isfriend") val isFriend: Int = 0,
    @SerializedName("isfamily") val isFamily: Int = 0,
    @SerializedName("url_q") val urlThumb: String? = null,
    @SerializedName("url_m") val urlMedium: String? = null
)