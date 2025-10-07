package com.davidmurray.omada

import android.app.Application
import com.davidmurray.omada.data.remote.FlickrDataSourceRetrofit
import com.davidmurray.omada.data.remote.FlickrWrapper
import com.davidmurray.omada.data.remote.retrofit.FlickrApi
import com.davidmurray.omada.data.repo.DefaultPhotoRepository
import com.davidmurray.omada.data.repo.PhotoRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Override of the Application class to be used for placement of singletons */
class OmadaChallengeApplication : Application() {

    /** Retrofit-instantiated accessor of the Flickr API */
    private val flickrApi: FlickrApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.flickr.com/services/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlickrApi::class.java)
    }

    /** Application-level instantiation of [PhotoRepository] singleton */
    val photoRepository: PhotoRepository by lazy {
        DefaultPhotoRepository(
            FlickrWrapper(
                // Abstracting out here to ensure there's one instance of the Retrofit FlickrApi
                FlickrDataSourceRetrofit(flickrApi)
            )
        )
    }
}