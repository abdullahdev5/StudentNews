package com.android.studentnews.main.settings.saved.ui.viewModels

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertFooterItem
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.android.studentnews.core.data.snackbar_controller.SnackBarActions
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.news.data.repository.SAVED_NEWS_LIST_PAGE_SIZE
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class SavedNewsViewModel(
    private val newsRepository: NewsRepository,
) : ViewModel() {

    private val _savedNewsList = MutableStateFlow<PagingData<NewsModel>>(PagingData.empty())
    val savedNewsList: StateFlow<PagingData<NewsModel>> = _savedNewsList

    var newsRemoveFromSaveStatus by mutableStateOf("")
        private set

    init {
        getSavedNewsList()
    }


    fun getSavedNewsList() {
        viewModelScope.launch {
            newsRepository
                .getSavedNewsList(limit = SAVED_NEWS_LIST_PAGE_SIZE)
                .cachedIn(this)
                .collectLatest { pagingData ->
                    _savedNewsList.value = pagingData
                }
        }
    }


    fun onNewsRemoveFromSave(news: NewsModel) {
        viewModelScope.launch {
            delay(1000)
            _savedNewsList.update {
                it.filter { data ->
                    news != data
                }
            }
            newsRemoveFromSaveStatus = Status.Loading
            newsRepository
                .onNewsRemoveFromSave(news)
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Success -> {
                            newsRemoveFromSaveStatus = Status.SUCCESS
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.data,
                                        duration = SnackbarDuration.Long,
                                    )
                                )
                        }

                        is NewsState.Failed -> {
                            newsRemoveFromSaveStatus = Status.FAILED
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: "",
                                        duration = SnackbarDuration.Long
                                    )
                                )
                        }

                        else -> {}
                    }
                }
        }
    }

}