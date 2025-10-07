package com.davidmurray.omada.data.remote

import com.davidmurray.omada.data.model.Photo
import com.davidmurray.omada.data.model.PhotoPage
import com.davidmurray.omada.data.remote.retrofit.FlickrPhotoDto
import com.davidmurray.omada.data.remote.retrofit.FlickrPhotosPage
import com.davidmurray.omada.data.remote.retrofit.FlickrResponse
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Unit tests for testing [FlickrWrapper]
 * The tests in this class will determine if the Conversions from the Flickr API are done
 * successfully into this app's data classes
 */
class FlickrWrapperTest {

    // Tests:
    // - Test conversions
    // - Test failed request

    @Test
    fun testDataConversions() = runTest {

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

        val flickr = FlickrWrapper(
            flickrDataSource = flickrDataSource
        )

        // Check the searchPhotos method
        val searchPhotoPage: PhotoPage = flickr.searchPhotos("test", 100, 1)
        val searchPhotos : List<Photo> = searchPhotoPage.photos
        assert(searchPhotos.size == 3)

        // Set up expected output and compare
        val photo1 = Photo(
            id = "1",
            title = "photo1"
        )

        val photo2 = Photo(
            id = "2",
            title = "photo2"
        )

        val photo3 = Photo(
            id = "3",
            title = "photo3"
        )
        val compareSearchPhotos = listOf(photo1, photo2, photo3)

        assertEquals(searchPhotos[0], compareSearchPhotos[0])
        assertEquals(searchPhotos[1], compareSearchPhotos[1])
        assertEquals(searchPhotos[2], compareSearchPhotos[2])

        // Not necessary to test all the individual fields, but will do anyway
        assertEquals(searchPhotos[0].id, compareSearchPhotos[0].id)
        assertEquals(searchPhotos[0].title, compareSearchPhotos[0].title)
        assertEquals(searchPhotos[1].id, compareSearchPhotos[1].id)
        assertEquals(searchPhotos[1].title, compareSearchPhotos[1].title)
        assertEquals(searchPhotos[2].id, compareSearchPhotos[2].id)
        assertEquals(searchPhotos[2].title, compareSearchPhotos[2].title)

        // Check the getRecentPhotos method
        val getRecentPhotoPage: PhotoPage = flickr.getRecentPhotos(100, 1)
        val getRecentPhotos : List<Photo> = getRecentPhotoPage.photos
        assert(getRecentPhotos.size == 3)

        // Set up expected output and compare
        val photoX1 = Photo(
            id = "x1",
            title = "photo_x1"
        )

        val photoX2 = Photo(
            id = "x2",
            title = "photo_x2"
        )

        val photoX3 = Photo(
            id = "x3",
            title = "photo_x3"
        )
        val compareGetRecentPhotos = listOf(photoX1, photoX2, photoX3)

        assertEquals(getRecentPhotos[0], compareGetRecentPhotos[0])
        assertEquals(getRecentPhotos[1], compareGetRecentPhotos[1])
        assertEquals(getRecentPhotos[2], compareGetRecentPhotos[2])
    }

    @Test
    fun testFailedRequest() = runTest {

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

        val flickr = FlickrWrapper(
            flickrDataSource = flickrDataSource
        )

        // Using try/catch methods here since we throw the custom FlickrException here.
        // Other unit tests at a higher level test the success/fail cases

        // Check the searchPhotos method
        try {
            val searchPhotoPage: PhotoPage = flickr.searchPhotos("test", 100, 1)
            println("$searchPhotoPage")
        } catch (e: Exception) {
            assertTrue(e is FlickrException)
            assertEquals(e.message, "You don't have permission to view this pool")
            assertEquals((e as FlickrException).code, 4)
        }

        // Check the getRecentPhotos method
        try {
            val getRecentPhotoPage: PhotoPage = flickr.getRecentPhotos(100, 1)
            println("$getRecentPhotoPage")
        } catch (e: Exception) {
            assertTrue(e is FlickrException)
            assertEquals(e.message, "Invalid SOAP envelope")
            assertEquals((e as FlickrException).code, 114)
        }
    }
}