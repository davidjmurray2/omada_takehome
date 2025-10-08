package com.davidmurray.omada.ui

import com.davidmurray.omada.data.model.Photo
import com.davidmurray.omada.data.model.PhotoPage
import com.davidmurray.omada.data.remote.FlickrException
import com.davidmurray.omada.data.repo.PhotoRepository
import com.davidmurray.omada.ui.search.SearchPhotosViewModel
import com.davidmurray.omada.ui.search.SearchPhotosScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

/**
 * Unit tests for testing the [SearchPhotosViewModel]
 * The tests in this class will determine if the ViewModel's behavior is as expected
 * to ensure the screen state is what's anticipated
 * @see SearchPhotosScreenState
 */
class SearchPhotosViewModelTest {

    val testPhotoPage = PhotoPage(
        page = 1,
        pages = 10,
        total = 10,
        perPage = 1,
        photos = listOf(
            Photo(id = "1", "photo1")
        )
    )

    // Tests:
    // - Initial ScreenState
    // - Success path
    // - Failure path

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testInitialState() = runTest {
        val repo: PhotoRepository = object : PhotoRepository {
            override suspend fun searchPhotos(
                text: String,
                numPerPage: Int,
                page: Int
            ): Result<PhotoPage> = Result.success(testPhotoPage)

            override suspend fun getRecentPhotos(
                numPerPage: Int,
                page: Int
            ): Result<PhotoPage> = Result.success(testPhotoPage)
        }
        val viewModel = SearchPhotosViewModel(repo, StandardTestDispatcher(testScheduler))

        Assert.assertTrue(viewModel.state.value.text.isEmpty())
        Assert.assertTrue(viewModel.state.value.photos.isEmpty())
        Assert.assertTrue(viewModel.state.value.isLoading) // the initial state fires off a getRecentPhotos request
        assert(viewModel.state.value.photos.isEmpty())
        Assert.assertNull(viewModel.state.value.error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSuccessPath() = runTest {
        val repo: PhotoRepository = object : PhotoRepository {
            override suspend fun searchPhotos(
                text: String,
                numPerPage: Int,
                page: Int
            ): Result<PhotoPage> = Result.success(testPhotoPage)

            override suspend fun getRecentPhotos(
                numPerPage: Int,
                page: Int
            ): Result<PhotoPage> = Result.success(testPhotoPage)
        }
        val viewModel = SearchPhotosViewModel(repo, StandardTestDispatcher(testScheduler))

        Assert.assertTrue(viewModel.state.value.text.isEmpty())
        Assert.assertTrue(viewModel.state.value.photos.isEmpty())
        Assert.assertTrue(viewModel.state.value.isLoading) // the initial state fires off a getRecentPhotos request
        assert(viewModel.state.value.photos.isEmpty())
        Assert.assertNull(viewModel.state.value.error)

        advanceUntilIdle()

        Assert.assertFalse(viewModel.state.value.isLoading)
        assert(viewModel.state.value.photos.size == 1)

        viewModel.onTextChange("test")
        Assert.assertEquals(viewModel.state.value.text, "test")

        viewModel.searchPhotos(true)

        Assert.assertTrue(viewModel.state.value.isLoading)
        Assert.assertTrue(viewModel.state.value.photos.isEmpty())

        advanceUntilIdle()

        Assert.assertFalse(viewModel.state.value.isLoading)
        assert(viewModel.state.value.photos.size == 1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFailurePath() = runTest {
        val repo: PhotoRepository = object : PhotoRepository {
            override suspend fun searchPhotos(
                text: String,
                numPerPage: Int,
                page: Int
            ): Result<PhotoPage> = Result.failure(FlickrException(1, "Test searchPhotos failed"))

            override suspend fun getRecentPhotos(
                numPerPage: Int,
                page: Int
            ): Result<PhotoPage> = Result.failure(FlickrException(1, "Test getRecentPhotos failed"))
        }
        val viewModel = SearchPhotosViewModel(repo, StandardTestDispatcher(testScheduler))

        Assert.assertTrue(viewModel.state.value.text.isEmpty())
        Assert.assertTrue(viewModel.state.value.photos.isEmpty())
        Assert.assertTrue(viewModel.state.value.isLoading) // the initial state fires off a getRecentPhotos request
        assert(viewModel.state.value.photos.isEmpty())
        Assert.assertNull(viewModel.state.value.error)

        advanceUntilIdle()

        Assert.assertFalse(viewModel.state.value.isLoading)
        Assert.assertTrue(viewModel.state.value.photos.isEmpty())
        Assert.assertNotNull(viewModel.state.value.error)
        Assert.assertEquals(viewModel.state.value.error, "Test getRecentPhotos failed")

        viewModel.onTextChange("test")
        Assert.assertEquals(viewModel.state.value.text, "test")

        viewModel.searchPhotos(true)

        Assert.assertTrue(viewModel.state.value.isLoading)
        Assert.assertTrue(viewModel.state.value.photos.isEmpty())

        advanceUntilIdle()

        Assert.assertFalse(viewModel.state.value.isLoading)
        Assert.assertTrue(viewModel.state.value.photos.isEmpty())
        Assert.assertNotNull(viewModel.state.value.error)
        Assert.assertEquals(viewModel.state.value.error, "Test searchPhotos failed")
    }
}