package com.davidmurray.omada.ui.temp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.davidmurray.omada.data.remote.FlickrException
import com.davidmurray.omada.data.repo.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchPhotosViewModel(val photoRepo: PhotoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchPhotosUiState(isLoading = false))
    val uiState = _uiState.asStateFlow()

    fun loadPosts() {
        _uiState.value = SearchPhotosUiState(isLoading = true)

        viewModelScope.launch {
            photoRepo.getRecentPhotos(100, 1)
                .onSuccess { photoPage ->
                    _uiState.value = SearchPhotosUiState(
                        isLoading = false,
                        data = photoPage,
                        error = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = SearchPhotosUiState(
                        isLoading = false,
                        data = null,
                        error = if (e is FlickrException) "Error Code (${e.code}): + ${e.message}" else e.message
                    )
                }
        }
    }


    class Factory(
        private val photoRepo: PhotoRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchPhotosViewModel(photoRepo) as T
        }
    }
}