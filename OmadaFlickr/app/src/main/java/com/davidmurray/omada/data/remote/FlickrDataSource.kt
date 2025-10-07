package com.davidmurray.omada.data.remote

import com.davidmurray.omada.data.model.PhotoPage
import com.davidmurray.omada.data.remote.retrofit.FlickrResponse

/** Interface that represents a connection to Flickr in order to access Photo data */
interface FlickrDataSource {

    /**
     * Search Flickr for photos matching some criteria, namely the tags (text).
     * @return [FlickrResponse] with the success/fail status + values for the provided criteria
     */
    suspend fun searchPhotos(text: String, numPerPage: Int, page: Int): FlickrResponse

    /**
     * Search Flickr for the latest public photos uploaded.
     * @return [FlickrResponse] with the success/fail status + values for the provided criteria
     */
    suspend fun getRecentPhotos(numPerPage: Int, page: Int): FlickrResponse
}