package com.android.studentnews.main.news.ui.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.core.data.snackbar_controller.SnackBarActions
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.account.domain.repository.AccountRepository
import com.android.studentnews.main.account.domain.resource.AccountState
import com.android.studentnews.main.news.domain.repository.NewsDetailRepository
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class NewsDetailViewModel(
    private val newsDetailRepository: NewsDetailRepository,
    private val newsRepository: NewsRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _newsById = MutableStateFlow<NewsModel?>(null)
    val newsById = _newsById.asStateFlow()

    private val _savedNewsById = MutableStateFlow<NewsModel?>(null)
    val savedNewsById = _savedNewsById.asStateFlow()

    val newsByIdStatus = mutableStateOf("")

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()


    init {
        getCurrentUser()
    }



    fun getNewsById(newsId: String) {
        viewModelScope.launch {
            newsDetailRepository
                .getNewsById(newsId)
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Failed -> {
                            newsByIdStatus.value = Status.FAILED
                            SnackBarController.sendEvent(
                                SnackBarEvents(
                                    message = result.error.message.toString(),
                                    duration = SnackbarDuration.Long
                                )
                            )
                        }

                        NewsState.Loading -> {
                            newsByIdStatus.value = Status.Loading
                        }

                        is NewsState.Success -> {
                            _newsById.value = result.data
                            newsByIdStatus.value = Status.SUCCESS
                        }
                    }
                }
        }
    }

    fun getSavedNewsById(newsId: String) {
        viewModelScope.launch {
            newsDetailRepository
                .getSavedNewsById(newsId)
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Success -> {
                            _savedNewsById.value = result.data
                        }

                        is NewsState.Failed -> {
                            SnackBarController.sendEvent(
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

    fun onNewsSave(news: NewsModel) {
        viewModelScope.launch {
            newsRepository
                .onNewsSave(news)
                .collectLatest { result ->
                    when (result) {

                        is NewsState.Failed -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage
                                            ?: "",
                                        duration = SnackbarDuration.Long,
                                    )
                                )
                        }

                        else -> {}
                    }
                }
        }
    }

    fun onNewsRemoveFromSave(news: NewsModel) {
        viewModelScope.launch {
            newsRepository
                .onNewsRemoveFromSave(news)
                .collectLatest { result ->
                    when (result) {

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

    fun onShareNews(
        imageUrl: String,
        context: Context,
        onShare: (Uri?) -> Unit
    ) = newsDetailRepository.onNewsShare(imageUrl, context, onShare)

    fun onNewsLike(newsId: String) {
        viewModelScope.launch {
            newsDetailRepository
                .onNewsLike(newsId)
                .collectLatest { result ->
                    when (result) {
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

    fun onNewsUnLike(newsId: String) {
        viewModelScope.launch {
            newsDetailRepository
                .onNewsUnlike(newsId)
                .collectLatest { result ->
                    when (result) {
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


    fun storeShareCount(newsId: String) = newsDetailRepository.storeShareCount(newsId)



    fun getCurrentUser() {
        viewModelScope.launch {
            accountRepository
                .getCurrentUser()
                .collectLatest { result ->
                    when (result) {
                        is AccountState.Success -> {
                            _currentUser.value = result.data
                        }

                        else -> {}
                    }
                }
        }
    }


}