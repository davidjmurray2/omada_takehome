package com.davidmurray.omada.data.remote.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Code representation of the Flickr API to be used by [retrofit2.Retrofit]
 *
 * @see <a href="https://www.flickr.com/services/api/misc.overview.html">Flickr API – Overview</a>
 */
interface FlickrApi {

    /**
     * Code representation of the flickr.photos.search method provided by the Flickr API
     * to be used by [retrofit2.Retrofit]
     * Return a list of photos matching some criteria.
     *
     * @see <a href="https://www.flickr.com/services/api/flickr.photos.search.html">Flickr API – search</a>
     */
    @GET("rest/")
    suspend fun search(
        @Query("method") method: String = "flickr.photos.search",
        @Query("api_key") apiKey: String = "a0222db495999c951dc33702500fdc4d",
        @Query("text") text: String = "test",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("extras") extras: String = "url_q,url_m",
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1
    ): FlickrResponse


    /**
     * Code representation of the flickr.photos.getRecent method provided by the Flickr API
     * to be used by [retrofit2.Retrofit]
     * Returns a list of the latest public photos uploaded to flickr.
     *
     * @see <a href="https://www.flickr.com/services/api/flickr.photos.getRecent.html">Flickr API – getRecent</a>
     */
    @GET("rest/")
    suspend fun getRecent(
        @Query("method") method: String = "flickr.photos.getRecent",
        @Query("api_key") apiKey: String = "a0222db495999c951dc33702500fdc4d",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("extras") extras: String = "url_q,url_m",
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1
    ): FlickrResponse
}