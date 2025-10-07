package com.davidmurray.omada.data.remote

import com.davidmurray.omada.data.model.Photo
import com.davidmurray.omada.data.model.PhotoPage
import com.davidmurray.omada.data.remote.retrofit.FlickrApi
import com.davidmurray.omada.data.remote.retrofit.FlickrResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Wrapper class for accessing methods from the [FlickrApi] via [Retrofit] and converting to
 * application-specific domain models
 *
 * @see FlickrApi
 * @see PhotoPage
 */
class FlickrWrapper {


    /** Retrofit-instantiated accessor of the Flickr API */
    private val api: FlickrApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.flickr.com/services/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlickrApi::class.java)
    }


    /**
     * Runs the [FlickrApi.search] method, converting its Dto objects into the data models used
     * by the application.
     * Return a list of photos matching some criteria, namely the tags (text).
     *
     * @see FlickrApi
     * @see FlickrApi.search
     */
    suspend fun searchPhotos(text: String, numPerPage: Int, page: Int): PhotoPage {
        return withContext(Dispatchers.IO) {
            val rsp: FlickrResponse = api.search(text = text, perPage = numPerPage, page = page)
            val photos = rsp.photos ?: throw FlickrException(
                rsp.code,
                rsp.message ?: "Flickr error occurred"
            )

            PhotoPage(
                page = photos.page,
                pages = photos.pages,
                perPage = photos.perPage,
                total = photos.total,
                photos = photos.photo.map { flickrPhoto ->
                    Photo(
                        flickrPhoto.id,
                        flickrPhoto.title,
                        flickrPhoto.urlThumb,
                        flickrPhoto.urlMedium
                    )
                }
            )
        }
    }


    /**
     * Runs the [FlickrApi.getRecent] method, converting its Dto objects into the data models used
     * by the application.
     * Returns a list of the latest public photos uploaded to flickr.
     *
     * @see FlickrApi
     * @see FlickrApi.getRecent
     */
    suspend fun getRecentPhotos(numPerPage: Int, page: Int): PhotoPage {
        return withContext(Dispatchers.IO) {
            val rsp: FlickrResponse = api.getRecent(perPage = numPerPage, page = page)
            val photos = rsp.photos ?: throw FlickrException(
                rsp.code,
                rsp.message ?: "Flickr error occurred"
            )

            PhotoPage(
                page = photos.page,
                pages = photos.pages,
                perPage = photos.perPage,
                total = photos.total,
                photos = photos.photo.map { flickrPhoto ->
                    Photo(
                        flickrPhoto.id,
                        flickrPhoto.title,
                        flickrPhoto.urlThumb,
                        flickrPhoto.urlMedium
                    )
                }
            )
        }
    }
}