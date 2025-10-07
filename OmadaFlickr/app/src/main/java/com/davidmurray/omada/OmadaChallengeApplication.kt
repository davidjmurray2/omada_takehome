package com.davidmurray.omada

import android.app.Application
import com.davidmurray.omada.data.repo.PhotoRepository

/** Override of the Application class to be used for placement of singletons */
class OmadaChallengeApplication : Application() {

    /** Application-level instantiation of [PhotoRepository] singleton */
    val photoRepository: PhotoRepository by lazy {
        PhotoRepository()
    }
}