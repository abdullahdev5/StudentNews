package com.android.studentnews.main.news.ui.viewModel

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.chooser.ChooserAction
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.R
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.MyBroadcastReceiver
import com.android.studentnews.main.news.NEWS_DETAIL_DEEPLINK_URI
import com.android.studentnews.main.news.NEWS_ID
import com.android.studentnews.main.news.domain.repository.NewsDetailRepository
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val SHARE_NEWS_ACTION = "com.android.studentnews.SHARE_NEWS_ACTION"
const val SHARE_NEWS_REQUEST_CODE = 10


class NewsDetailViewModel(
    private val newsDetailRepository: NewsDetailRepository,
) : ViewModel() {

    private val _newsById = MutableStateFlow<NewsModel?>(null)
    val newsById = _newsById.asStateFlow()

    var isNewsSaved by mutableStateOf<Boolean?>(null)

    val newsByIdStatus = mutableStateOf("")

    var newsByIdErrorMsg by mutableStateOf("")


    fun getNewsById(newsId: String) {
        viewModelScope.launch {
            newsDetailRepository
                .getNewsById(newsId)
                .collectLatest { result ->
                    when (result) {
                        NewsState.Loading -> {
                            newsByIdStatus.value = Status.Loading
                        }

                        is NewsState.Success -> {
                            println("Success")
                            _newsById.value = result.data
                            newsByIdStatus.value = Status.SUCCESS
                        }

                        is NewsState.Failed -> {
                            println("Error From Failed")
                            newsByIdStatus.value = Status.FAILED
                            newsByIdErrorMsg = result.error.localizedMessage ?: ""
                        }

                        else -> {}
                    }
                }
        }
    }

    fun getIsNewsSaved(newsId: String) {
        viewModelScope.launch {
            newsDetailRepository
                .getIsNewsSaved(newsId)
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Success -> {
                            isNewsSaved = result.data
                        }

                        is NewsState.Failed -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = "Failed to get News Saved OR Not!"
                                    )
                                )
                        }

                        else -> {}
                    }
                }
        }
    }

    fun onNewsSave(
        news: NewsModel,
        wantToShowSuccessMessage: Boolean = false,
    ) {
        viewModelScope.launch {
            newsDetailRepository
                .onNewsSave(news)
                .collectLatest { result ->
                    when (result) {

                        is NewsState.Success -> {
                            if (wantToShowSuccessMessage == true) {
                                SnackBarController
                                    .sendEvent(
                                        SnackBarEvents(
                                            message = result.data
                                        )
                                    )
                            }
                        }

                        is NewsState.Failed -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.message
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

    fun onNewsRemoveFromSave(
        news: NewsModel,
        wantToShowSuccessMessage: Boolean = false,
    ) {
        viewModelScope.launch {
            newsDetailRepository
                .onNewsRemoveFromSave(news)
                .collectLatest { result ->
                    when (result) {

                        is NewsState.Success -> {
                            if (wantToShowSuccessMessage) {
                                SnackBarController
                                    .sendEvent(
                                        SnackBarEvents(
                                            message = result.data
                                        )
                                    )
                            }
                        }

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

    fun onNewsShare(
        title: String,
        imageUrl: String,
        context: Context,
        newsId: String,
    ) {
        newsDetailRepository.onNewsShare(
            imageUrl,
            context,
            onShare = { fileUri ->
                try {
                    val shareClickedIntent = Intent(
                        context,
                        MyBroadcastReceiver::class.java,
                    ).apply {
                        action = SHARE_NEWS_ACTION
                        putExtra(NEWS_ID, newsId)
                        type = "text/plain"
                    }

                    val sharedPendingIntent = PendingIntent
                        .getBroadcast(
                            context,
                            SHARE_NEWS_REQUEST_CODE,
                            shareClickedIntent,
                            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )

                    val sharedIntent = Intent.createChooser(
                        Intent().apply {
                            action = Intent.ACTION_SEND

                            putExtra(Intent.EXTRA_STREAM, fileUri)

                            putExtra(Intent.EXTRA_TEXT, "$title\n$NEWS_DETAIL_DEEPLINK_URI/$newsId")

                            data = fileUri
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        },
                        null,
                        sharedPendingIntent.intentSender
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

                        val customActions = ChooserAction.Builder(
                            Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
                            "Custom",
                            PendingIntent.getBroadcast(
                                context,
                                1192,
                                Intent(Intent.ACTION_VIEW),
                                PendingIntent.FLAG_IMMUTABLE
                            )
                        ).build()

                        sharedIntent.putExtra(Intent.EXTRA_CHOOSER_CUSTOM_ACTIONS, customActions)
                    }

                    context.startActivity(sharedIntent)

                } catch (e: Exception) {
                    e.printStackTrace()
                    viewModelScope.launch {
                        SnackBarController
                            .sendEvent(
                                SnackBarEvents(
                                    message = e.localizedMessage ?: "",
                                    duration = SnackbarDuration.Long
                                )
                            )
                    }
                }
            }
        )
    }

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

    override fun onCleared() {
        isNewsSaved = null
    }


}