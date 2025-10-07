package com.davidmurray.omada.data.repo

import com.davidmurray.omada.data.model.PhotoPage
import com.davidmurray.omada.data.remote.FlickrWrapper

/**
 * Default implementation of [PhotoRepository] responsible for providing photo data
 * to the application from a Flickr data source
 *
 * NOTE: While this isn't strictly necessary for this coding challenge, the intention is to
 * convey a context where expansion would be natural and a Repository is standard practice.
 */
class DefaultPhotoRepository(
    val flickr: FlickrWrapper
) : PhotoRepository {

    override suspend fun searchPhotos(text: String, numPerPage: Int, page: Int): Result<PhotoPage> {
        return runCatching {
            flickr.searchPhotos(text, numPerPage, page)
        }
    }


    override suspend fun getRecentPhotos(numPerPage: Int, page: Int): Result<PhotoPage> {
        return runCatching {
            flickr.getRecentPhotos(numPerPage, page)
        }
    }
}