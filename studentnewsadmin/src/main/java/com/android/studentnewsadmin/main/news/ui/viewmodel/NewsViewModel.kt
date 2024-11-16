package com.android.studentnewsadmin.main.news.ui.viewmodel

import android.Manifest
import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnewsadmin.NotificationRelated
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.core.domain.resource.CategoryState
import com.android.studentnewsadmin.core.domain.resource.NewsState
import com.android.studentnewsadmin.main.news.domain.model.CategoryModel
import com.android.studentnewsadmin.main.news.domain.model.NewsModel
import com.android.studentnewsadmin.main.news.domain.repository.NewsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {


    private val _newsList = MutableStateFlow<List<NewsModel>>(emptyList())
    val newsList = _newsList.asStateFlow()

    private val _categoryList = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categoryList = _categoryList.asStateFlow()

    var newsListStatus = mutableStateOf("")

    var errorMsg = mutableStateOf("")


    init {
        getNewsList()
        getCategoryList()
    }


    fun onNewsAdd(
        title: String,
        description: String,
        uriList: List<Uri>,
        category: String,
        link: String,
        linkTitle: String,
        context: Context
    ) = newsRepository.onNewsAdd(title, description, uriList, category, link, linkTitle, context)


    fun getNewsList() {
        _newsList.value = emptyList()
        viewModelScope.launch {
            delay(1000L)
            newsRepository
                .getNewsList()
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Failed -> {
                            newsListStatus.value = Status.Failed
                            errorMsg.value = result.message.toString()
                        }

                        NewsState.Loading -> {
                            newsListStatus.value = Status.Loading
                        }

                        is NewsState.Success -> {
                            _newsList.value = result.data
                            newsListStatus.value = Status.Success
                        }

                        is NewsState.Progress -> {}
                    }
                }
        }
    }

    fun onNewsDelete(newsId: String) = newsRepository.onNewsDelete(newsId)

    fun onCategoryAdd(
        category: String,
        imageBitmap: Bitmap,
    ) = newsRepository.onCategoryAdd(category, imageBitmap)

    fun getCategoryList() {
        viewModelScope.launch {
            newsRepository
                .getCategoryList()
                .collectLatest { result ->
                    when (result) {
                        is CategoryState.Success -> {
                            _categoryList.value = result.data
                        }

                        else -> {}
                    }
                }
        }
    }

    fun showLoadingNotification(context: Context, title: String) {

        val notification = NotificationCompat.Builder(context, NotificationRelated.MEDIA_ADDING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_media_next)
            .setContentTitle("Loading....")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(title)
            )

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(NotificationRelated.MEDIA_ADDING_NOTIFICATION_ID, notification.build())

    }

    fun startNewsAddingWorker(
        title: String,
        description: String,
        stringArray: Array<String>,
        category: String,
        link: String,
        linkTitle: String
    ) = newsRepository.startNewsAddingWorker(title, description, stringArray, category, link, linkTitle)


}