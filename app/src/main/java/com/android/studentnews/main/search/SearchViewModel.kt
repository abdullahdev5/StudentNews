package com.android.studentnews.main.search

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
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

    val searchingStatus = mutableStateOf("")

    val errorMsg = mutableStateOf("")


    init {
        getCategoriesList()
    }


    fun onSearch(query: String, currentSelectedCategory: String?) {
        searchingStatus.value = Status.Loading
        viewModelScope.launch {
            delay(1000L)
            newsRepository
                .onSearch(query, currentSelectedCategory)
                .collect { result ->
                    when (result) {
                        is NewsState.Success<*> -> {
                            _searchNewsList.value = result.data as List<NewsModel>
                            searchingStatus.value = Status.SUCCESS
                        }

                        is NewsState.Loading -> {
                            searchingStatus.value = Status.Loading
                        }

                        is NewsState.Failed -> {
                            searchingStatus.value = Status.FAILED
                            errorMsg.value = result.error.localizedMessage ?: ""
                        }
                    }
                }
        }
    }

    fun getNewsListByCategory(category: String) {
        searchingStatus.value = Status.Loading
        viewModelScope.launch {
            delay(1000L)
            newsRepository
                .getNewsListByCategory(category)
                .collect { result ->
                    when (result) {
                        is NewsState.Failed -> {
                            searchingStatus.value = Status.FAILED
                            errorMsg.value = result.error.localizedMessage ?: ""
                        }

                        NewsState.Loading -> {
                            searchingStatus.value = Status.Loading
                        }

                        is NewsState.Success -> {
                            _searchNewsList.value = result.data
                            searchingStatus.value = Status.SUCCESS
                        }
                    }
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