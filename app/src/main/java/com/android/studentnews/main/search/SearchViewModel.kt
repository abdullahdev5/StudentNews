package com.android.studentnews.main.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.news.data.repository.NEWS_LIST_PAGE_SIZE
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    private val newsRepository: NewsRepository
): ViewModel() {


    private val _searchNewsList = MutableStateFlow<PagingData<NewsModel>>(PagingData.empty())
    val searchNewsList: StateFlow<PagingData<NewsModel>> = _searchNewsList

    val categoriesList = newsRepository.getCategoriesList(NEWS_LIST_PAGE_SIZE)



    fun onSearch(query: String, currentSelectedCategory: String?) {
        viewModelScope.launch {
            newsRepository
                .onSearch(query, currentSelectedCategory, NEWS_LIST_PAGE_SIZE)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _searchNewsList.value = pagingData
                }
        }
    }

    fun getNewsListByCategory(category: String) {
        viewModelScope.launch {
            newsRepository
                .getNewsList(category)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _searchNewsList.value = pagingData
                }
        }
    }


}