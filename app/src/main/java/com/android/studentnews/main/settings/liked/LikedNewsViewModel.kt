package com.android.studentnews.main.settings.liked

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.core.data.snackbar_controller.SnackBarActions
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LikedNewsViewModel(
    private val newsRepository: NewsRepository
): ViewModel() {

    private val _likedNewsList = MutableStateFlow<List<NewsModel?>>(emptyList())
    val likedNewsList = _likedNewsList.asStateFlow()

    var likedNewsListStatus by mutableStateOf("")
        private set


    init {
        getLikedNewsList()
    }


    fun getLikedNewsList() {
        viewModelScope.launch {
            newsRepository.getLikedNewsList()
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Loading -> {
                            likedNewsListStatus = Status.Loading
                        }
                        is NewsState.Failed -> {
                            likedNewsListStatus = Status.FAILED
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: "",
                                        duration = SnackbarDuration.Long
                                    )
                                )
                        }
                        is NewsState.Success -> {
                            likedNewsListStatus = Status.SUCCESS
                            _likedNewsList.value = result.data
                        }
                    }
                }
        }
    }



    fun onNewsSave(
        news: NewsModel,
        onSee: (String) -> Unit,
    ) {
        viewModelScope.launch {
            newsRepository
                .onNewsSave(news)
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Success -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.data,
                                        duration = SnackbarDuration.Long,
                                        action = SnackBarActions(
                                            label = "See",
                                            action = {
                                                onSee.invoke(news.newsId ?: "")
                                            }
                                        )
                                    )
                                )
                        }
                        is NewsState.Failed -> {
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