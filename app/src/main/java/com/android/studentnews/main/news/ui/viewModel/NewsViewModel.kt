package com.android.studentnews.news.ui.viewModel

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.news.data.repository.NEWS_CATEGORY_LIST_PAGE_SIZE
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.account.domain.repository.AccountRepository
import com.android.studentnews.main.account.domain.resource.AccountState
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _newsList = MutableStateFlow<PagingData<NewsModel>>(PagingData.empty())
    val newsList: StateFlow<PagingData<NewsModel>> = _newsList


    val categoriesList = newsRepository.getCategoriesList(NEWS_CATEGORY_LIST_PAGE_SIZE)
        .cachedIn(viewModelScope)

    var isRefreshing by mutableStateOf(false)

    init {
        getNewsList(null)
        setupPeriodicNewsWorkRequest()
//        cancelPeriodicNewsWorkRequest()
    }

    fun getNewsList(category: String?) {
        viewModelScope.launch {
            newsRepository
                .getNewsList(category)
                .cachedIn(this)
                .collectLatest { pagingData ->
                    _newsList.value = pagingData
                }
        }
    }

    fun signOut() = authRepository.signOut()

    fun setupPeriodicNewsWorkRequest() = newsRepository.setupPeriodicNewsWorkRequest()

    fun cancelPeriodicNewsWorkRequest() = newsRepository.cancelPeriodicNewsWorkRequest()

}