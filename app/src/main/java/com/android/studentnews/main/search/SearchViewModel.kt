package com.android.studentnews.main.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val newsRepository: NewsRepository
): ViewModel() {


    private val _searchNewsList = MutableStateFlow<List<NewsModel>>(emptyList())
    val searchNewsList = _searchNewsList.asStateFlow()

    private val _categoriesList = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categoriesList = _categoriesList.asStateFlow()

    var searchingStatus by mutableStateOf("")
        private set

    var errorMsg by mutableStateOf("")
        private set


    init {
        getCategoriesList()
    }


    fun onSearch(query: String, currentSelectedCategory: String?) {
        searchingStatus = Status.Loading
        viewModelScope.launch {
            delay(1000L)
            newsRepository
                .onSearch(query, currentSelectedCategory)
                .collect { result ->
                    when (result) {
                        is NewsState.Success<*> -> {
                            _searchNewsList.value = result.data as List<NewsModel>
                            searchingStatus = Status.SUCCESS
                        }

                        is NewsState.Loading -> {
                            searchingStatus = Status.Loading
                        }

                        is NewsState.Failed -> {
                            searchingStatus = Status.FAILED
                            errorMsg = result.error.localizedMessage ?: ""
                        }
                        else -> {}
                    }
                }
        }
    }

    fun getNewsListByCategory(category: String) {
        searchingStatus = Status.Loading
        viewModelScope.launch {
            delay(1000L)
            newsRepository
                .getNewsListByCategory(category)
                .collect { result ->
//                    when (result) {
//                        is NewsState.Failed -> {
//                            searchingStatus = Status.FAILED
//                            errorMsg = result.error.localizedMessage ?: ""
//                        }
//
//                        NewsState.Loading -> {
//                            searchingStatus = Status.Loading
//                        }
//
//                        is NewsState.Success -> {
//                            _searchNewsList.value = result.data
//                            searchingStatus = Status.SUCCESS
//                        }
//                        else -> {}
//                    }
                }
        }
    }

    fun getCategoriesList() {
        viewModelScope.launch {
            newsRepository
                .getCategoriesList()
                .collect { result ->
                    when (result) {
                        is NewsState.Success<*> -> {
                            _categoriesList.value = result.data as List<CategoryModel>
                        }

                        else -> {}
                    }
                }
        }
    }


}