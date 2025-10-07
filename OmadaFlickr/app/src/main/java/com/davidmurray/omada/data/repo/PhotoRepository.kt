package com.davidmurray.omada.data.repo

import com.davidmurray.omada.data.model.PhotoPage
import com.davidmurray.omada.data.remote.FlickrWrapper

/**
 * Interface representing the repository responsible for providing photo data
 * to the application from a Flickr data source
 *
 * NOTE: While this isn't strictly necessary for this coding challenge, the intention is to
 * convey a context where expansion would be natural and a Repository is standard practice.
 */
interface PhotoRepository {


    /**
     * Runs the [FlickrWrapper.searchPhotos] method for a [Result], providing the correct
     * associated [PhotoPage] based on the provided parameters.
     *
     * @see FlickrWrapper
     * @see FlickrWrapper.searchPhotos
     */
    suspend fun searchPhotos(text: String, numPerPage: Int, page: Int): Result<PhotoPage>

    /**
     * Runs the [FlickrWrapper.getRecentPhotos] method for a [Result], providing the correct
     * associated [PhotoPage] based on the provided parameters.
     *
     * @see FlickrWrapper
     * @see FlickrWrapper.getRecentPhotos
     */
    suspend fun getRecentPhotos(numPerPage: Int, page: Int): Result<PhotoPage>
}