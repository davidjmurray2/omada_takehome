package com.davidmurray.omada.data.remote

import com.davidmurray.omada.data.model.Photo
import com.davidmurray.omada.data.model.PhotoPage
import com.davidmurray.omada.data.remote.retrofit.FlickrApi
import com.davidmurray.omada.data.remote.retrofit.FlickrResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

/**
 * Wrapper class for accessing methods from a [FlickrDataSource] and converting to
 * application-specific domain models
 *
 * @see FlickrDataSource
 * @see PhotoPage
 */
class FlickrWrapper(
    val flickrDataSource: FlickrDataSource
) {

    suspend fun searchPhotos(text: String, numPerPage: Int, page: Int): PhotoPage {
        return withContext(Dispatchers.IO) {
            val rsp: FlickrResponse = flickrDataSource.searchPhotos(text = text, numPerPage = numPerPage, page = page)
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

    suspend fun getRecentPhotos(numPerPage: Int, page: Int): PhotoPage {
        return withContext(Dispatchers.IO) {
            val rsp: FlickrResponse = flickrDataSource.getRecentPhotos(numPerPage = numPerPage, page = page)
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

/**
 * Wrapper class for accessing methods from the [FlickrApi] via [Retrofit]
 *
 * @see FlickrApi
 * @see FlickrResponse
 */
class FlickrDataSourceRetrofit(
    val api: FlickrApi
) : FlickrDataSource {


    /**
     * Runs the [FlickrApi.search] method, providing the [FlickrResponse] the query provides
     *
     * @see FlickrApi
     * @see FlickrApi.search
     * @return [FlickrResponse] with the success/fail status + values for the provided criteria
     */
    override suspend fun searchPhotos(text: String, numPerPage: Int, page: Int): FlickrResponse {
        return api.search(text = text, perPage = numPerPage, page = page)
    }


    /**
     * Runs the [FlickrApi.getRecent] method, providing the [FlickrResponse] the query provides
     *
     * @see FlickrApi
     * @see FlickrApi.getRecent
    @return [FlickrResponse] with the success/fail status + values for the provided criteria
     */
    override suspend fun getRecentPhotos(numPerPage: Int, page: Int): FlickrResponse {
        return api.getRecent(perPage = numPerPage, page = page)
    }
}