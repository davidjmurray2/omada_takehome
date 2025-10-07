package com.davidmurray.omada.data.remote.retrofit

import com.google.gson.annotations.SerializedName

data class FlickrResponse (
    val photos: FlickrPhotosPage?,
    val stat: String?,
    val code: Int?,
    val message: String?
)

data class FlickrPhotosPage(
    val page: Int,
    val pages: Int,
    @SerializedName("perpage") val perPage: Int,
    val total: String,
    val photo: List<FlickrPhotoDto>
)

data class FlickrPhotoDto(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val title: String,
    @SerializedName("ispublic") val isPublic: Int,
    @SerializedName("isfriend") val isFriend: Int,
    @SerializedName("isfamily") val isFamily: Int,
    @SerializedName("url_q") val urlThumb: String? = null,
    @SerializedName("url_m") val urlMedium: String? = null
)