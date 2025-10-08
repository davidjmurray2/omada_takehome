package com.davidmurray.omada.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.davidmurray.omada.data.repo.PhotoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel representing the Searching screen when searching Flickr for Photos,
 * whether its getting the recent photos, or searching by tag/text
 *
 * @see PhotoRepository
 */
class SearchPhotosViewModel(
    private val photoRepo: PhotoRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _state = MutableStateFlow(SearchPhotosScreenState())
    val state: StateFlow<SearchPhotosScreenState> = _state.asStateFlow()

    // Paging Config
    /** The current page to be loaded next */
    private var currentPage = 1
    /** Whether or not there's more pages to pull from the API */
    private var hasMore = true
    /** Whether or not we are currently pulling the next page of Photos from the API */
    private var isLoadingMore = false

    init {
        // start with the most recent photos loaded or loading
        searchPhotos(reset = true)
    }

    /**
     * Adjusts what the current text for the searching screen is, aka what tags the user would
     * like to search Flickr for
     */
    fun onTextChange(text: String) {
        val text = text.trim()
        _state.value = _state.value.copy(text = text)
    }

    /**
     * Initiate an asynchronous call with a result to get the search results of a text for the
     * current text. If the text is empty, it will call to get the most recent photos instead.
     * @param reset whether we need a fresh call (true) or if they've scrolled (false)
     */
    fun searchPhotos(reset: Boolean) {
        // We only want to run this method if we are refreshing OR
        // if we don't already have a process going to load more AND we haven't already loaded all
        if (!reset && (isLoadingMore || !hasMore)) return

        // This method is fairly complicated --
        // It handles:
        // - getRecentPhotos (when the text provided by the user is empty)
        // - searchPhotos (with the provided text from the user)
        // - success/fail cases for both methods
        // - load more when the user reaches the end of the already loaded photos
        //
        // It does the last one of these by using class variables (hasMore, isLoadingMore, currPage)
        // When it loads more, it appends the new results to the end of the current
        // When resetting, it clears all the variables


        // Grab the text from the state
        val text = _state.value.text
        if (reset) {
            // when resetting, clear the variables for accessing more from the api
            currentPage = 1
            hasMore = true
            _state.value = _state.value.copy(isLoading = true, error = null, photos = emptyList())
        } else {
            // if we aren't resetting, we're loading more
            isLoadingMore = true
        }

        if (text.isEmpty()) {

            // getRecentPhotos
            viewModelScope.launch(dispatcher) {
                photoRepo.getRecentPhotos(100, currentPage)
                    .onSuccess { page ->

                        // Note: the photos below, when appended to the end are also trimmed down
                        // to distinct by id. There was an odd issue where there were overlapping ids
                        val photos = (_state.value.photos + page.photos).distinctBy { it.id }
                        _state.value = _state.value.copy(isLoading = false, photos = photos, error = null)
                        // update the received values and update the flags for loading more
                        if (hasMore) {
                            hasMore = page.page < page.pages
                            currentPage++
                        }
                        isLoadingMore = false
                    }
                    .onFailure { e ->
                        _state.value = _state.value.copy(isLoading = false, error = e.message)
                        isLoadingMore = false
                    }
            }
        }
        else {

            // searchPhotos
            viewModelScope.launch(dispatcher) {
                photoRepo.searchPhotos(text, 100, currentPage)
                    .onSuccess { page ->

                        val photos = (_state.value.photos + page.photos).distinctBy { it.id }
                        _state.value = _state.value.copy(isLoading = false, photos = photos, error = null)
                        // update the received values and update the flags for loading more
                        if (hasMore) {
                            hasMore = page.page < page.pages
                            currentPage++
                        }
                        isLoadingMore = false
                    }
                    .onFailure { e ->
                        _state.value = _state.value.copy(isLoading = false, error = e.message)
                        isLoadingMore = false
                    }
            }
        }
    }

    /**
     * ViewModel Factory for instantiating the [SearchPhotosViewModel]
     */
    class Factory(
        private val photoRepo: PhotoRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchPhotosViewModel(photoRepo) as T
        }
    }
}