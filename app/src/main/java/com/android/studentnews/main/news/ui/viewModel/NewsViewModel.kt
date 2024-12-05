package com.android.studentnews.news.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _newsList = MutableStateFlow<List<NewsModel>>(emptyList())
    val newsList = _newsList.asStateFlow()

    private val _categoriesList = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categoriesList = _categoriesList.asStateFlow()

    var isRefreshing by mutableStateOf(false)

    var errorMsg by mutableStateOf("")
        private set

    var newsListStatus by mutableStateOf("")
        private set

    // Current User
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()


    init {
        isRefreshing = true
        getCategoriesList()
        getCurrentUser()
        setupPeriodicNewsWorkRequest()
//        cancelPeriodicNewsWorkRequest()
    }


    // News
    fun getNewsList() {
        newsListStatus = Status.Loading
        viewModelScope.launch {
            delay(1000L)
            newsRepository
                .getNewsList()
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Failed -> {
                            newsListStatus = Status.FAILED
                            errorMsg = result.error.localizedMessage ?: ""
                        }

                        NewsState.Loading -> {
                            newsListStatus = Status.Loading
                        }

                        is NewsState.Success -> {
                            _newsList.value = result.data
                            newsListStatus = Status.SUCCESS
                        }
                    }
                }
        }
    }


    // Category
    fun getNewsListByCategory(category: String) {
        newsListStatus = Status.Loading
        viewModelScope.launch {
            delay(1000L)
            newsRepository
                .getNewsListByCategory(category)
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Failed -> {
                            newsListStatus = Status.FAILED
                            errorMsg = result.error.localizedMessage ?: ""
                        }

                        NewsState.Loading -> {
                            newsListStatus = Status.Loading
                        }

                        is NewsState.Success -> {
                            _newsList.value = result.data
                            newsListStatus = Status.SUCCESS
                        }
                    }
                }
        }
    }

    fun getCategoriesList() {
        viewModelScope.launch {
            newsRepository
                .getCategoriesList()
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Success -> {
                            _categoriesList.value = result.data as List<CategoryModel>
                        }

                        else -> {}
                    }
                }
        }
    }

    // currentUser
    fun getCurrentUser() {
        viewModelScope.launch {
            authRepository
                .getCurrentUser()
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Success -> {
                            _currentUser.value = result.data
                        }

                        else -> {}
                    }
                }
        }
    }

    fun signOut() = authRepository.signOut()

    fun setupPeriodicNewsWorkRequest() = newsRepository.setupPeriodicNewsWorkRequest()

    fun cancelPeriodicNewsWorkRequest() = newsRepository.cancelPeriodicNewsWorkRequest()


}