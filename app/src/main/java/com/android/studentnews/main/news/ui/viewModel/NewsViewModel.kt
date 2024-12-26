package com.android.studentnews.news.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.main.news.domain.repository.NewsDetailRepository
import com.android.studentnews.main.referral_bonus.domain.model.EarnedPointsModel
import com.android.studentnews.news.data.repository.NEWS_CATEGORY_LIST_PAGE_SIZE
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val authRepository: AuthRepository,
    private val newsDetailRepository: NewsDetailRepository,
) : ViewModel() {

    private val _newsList = MutableStateFlow<PagingData<NewsModel>>(PagingData.empty())
    val newsList: StateFlow<PagingData<NewsModel>> = _newsList

    val categoriesList = newsRepository.getCategoriesList(NEWS_CATEGORY_LIST_PAGE_SIZE)
        .cachedIn(viewModelScope)

    var isRefreshing by mutableStateOf(false)

    var newsIdWhenMoreOptionClick by mutableStateOf<String?>(null) // When More Options Click

    var earnedPointsListItemWhenCollectClick by mutableStateOf<EarnedPointsModel?>(null) // When Collect Click


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

    fun onReferralPointsCollect(
        earnedPointsListItem: EarnedPointsModel
    ) = newsDetailRepository.onReferralPointsCollect(earnedPointsListItem)


    fun signOut() = authRepository.signOut()

    fun setupPeriodicNewsWorkRequest() = newsRepository.setupPeriodicNewsWorkRequest()

    fun cancelPeriodicNewsWorkRequest() = newsRepository.cancelPeriodicNewsWorkRequest()

    override fun onCleared() {
        newsIdWhenMoreOptionClick = null
        earnedPointsListItemWhenCollectClick = null
    }

}