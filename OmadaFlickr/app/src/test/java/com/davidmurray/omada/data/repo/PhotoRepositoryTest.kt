package com.davidmurray.omada.data.repo

import com.davidmurray.omada.data.model.PhotoPage
import com.davidmurray.omada.data.remote.FlickrDataSource
import com.davidmurray.omada.data.remote.FlickrException
import com.davidmurray.omada.data.remote.FlickrWrapper
import com.davidmurray.omada.data.remote.retrofit.FlickrPhotoDto
import com.davidmurray.omada.data.remote.retrofit.FlickrPhotosPage
import com.davidmurray.omada.data.remote.retrofit.FlickrResponse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Unit tests for testing [PhotoRepository]
 * The tests in this class will determine if the Repository responds correctly to successful
 * and unsuccessful calls to the [FlickrDataSource]
 */
class PhotoRepositoryTest {

    // Tests:
    // - Successful request responses
    // - Failed request responses

    @Test
    fun testSuccessfulRequests() = runTest {

        // Create a fake data source that provides mock data for successful requests
        val flickrDataSource = object : FlickrDataSource {

            override suspend fun searchPhotos(text: String, numPerPage: Int, page: Int): FlickrResponse =
                FlickrResponse(
                    photos = FlickrPhotosPage(
                        page = 1,
                        pages = 3,
                        perPage = 100,
                        total = 300,
                        photo = listOf(
                            FlickrPhotoDto(id = "1", title = "photo1"),
                            FlickrPhotoDto(id = "2", title = "photo2"),
                            FlickrPhotoDto(id = "3", title = "photo3")
                        )
                    ),
                    stat = "ok"
                )

            override suspend fun getRecentPhotos(numPerPage: Int, page: Int): FlickrResponse =
                FlickrResponse(
                    photos = FlickrPhotosPage(
                        page = 1,
                        pages = 3,
                        perPage = 100,
                        total = 300,
                        photo = listOf(
                            FlickrPhotoDto(id = "x1", title = "photo_x1"),
                            FlickrPhotoDto(id = "x2", title = "photo_x2"),
                            FlickrPhotoDto(id = "x3", title = "photo_x3")
                        )
                    ),
                    stat = "ok"
                )
        }
        val repo = DefaultPhotoRepository(FlickrWrapper(flickrDataSource))

        // Check the searchPhotos request
        val searchPhotosResult: Result<PhotoPage> = repo.searchPhotos("test", 100, 1)
        assertTrue(searchPhotosResult.isSuccess)

        val searchPhotosPage = searchPhotosResult.getOrNull()
        assertNotNull(searchPhotosPage)
        val searchPhotosPagePhotos = searchPhotosPage?.photos
        assertNotNull(searchPhotosPagePhotos)
        assertTrue(searchPhotosPagePhotos?.size == 3)

        // Check the getRecentPhotos request
        val getRecentPhotosResult: Result<PhotoPage> = repo.getRecentPhotos(100, 1)
        assertTrue(getRecentPhotosResult.isSuccess)

        val getRecentPhotosPage = getRecentPhotosResult.getOrNull()
        assertNotNull(getRecentPhotosPage)
        val getRecentPhotosPagePhotos = getRecentPhotosPage?.photos
        assertNotNull(getRecentPhotosPagePhotos)
        assertTrue(getRecentPhotosPagePhotos?.size == 3)
    }

    @Test
    fun testFailureRequests() = runTest {

        // Create a fake data source that provides mock data for failed requests
        val flickrDataSource = object : FlickrDataSource {

            override suspend fun searchPhotos(text: String, numPerPage: Int, page: Int): FlickrResponse =
                FlickrResponse(
                    photos = null,
                    stat = "fail",
                    code = 4,
                    message = "You don't have permission to view this pool"
                )

            override suspend fun getRecentPhotos(numPerPage: Int, page: Int): FlickrResponse =
                FlickrResponse(
                    photos = null,
                    stat = "fail",
                    code = 114,
                    message = "Invalid SOAP envelope"
                )
        }
        val repo = DefaultPhotoRepository(FlickrWrapper(flickrDataSource))

        // Check the searchPhotos request
        val searchPhotosPage = repo.searchPhotos("test", 100, 1)
        assertTrue(searchPhotosPage.isFailure)
        assertTrue(searchPhotosPage.exceptionOrNull() is FlickrException)
        assertNull(searchPhotosPage.getOrNull())

        // Check the getRecentPhotos request
        val getRecentPhotosPage = repo.getRecentPhotos(100, 1)
        assertTrue(getRecentPhotosPage.isFailure)
        assertTrue(getRecentPhotosPage.exceptionOrNull() is FlickrException)
        assertNull(getRecentPhotosPage.getOrNull())
    }
}