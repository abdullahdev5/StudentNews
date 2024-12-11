package com.android.studentnews.news.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.main.MainNavigationDrawerList
import com.android.studentnews.news.data.repository.NEWS_CATEGORY_LIST_PAGE_SIZE
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.qualifier.qualifier

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
        isRefreshing = true
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