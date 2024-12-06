package com.android.studentnews.news.ui.viewModel

import android.annotation.SuppressLint
import androidx.annotation.CheckResult
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    var newsList = newsRepository.getNewsList().cachedIn(viewModelScope)

    val categoriesList = newsRepository.getCategoriesList().cachedIn(viewModelScope)

    var isRefreshing by mutableStateOf(false)

    // Current User
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()


    init {
        getCurrentUser()
        setupPeriodicNewsWorkRequest()
//        cancelPeriodicNewsWorkRequest()
    }


    // Category
    fun getNewsListByCategory(category: String) {
        newsList = newsRepository.getNewsListByCategory(category).cachedIn(viewModelScope)
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