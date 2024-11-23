@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.android.studentnews.main.news.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.common.isInternetAvailable
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.main.news.domain.destination.NewsDestination
import com.android.studentnews.main.news.ui.viewModel.SavedNewsViewModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.ui.viewModel.NewsViewModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SharedTransitionScope.SavedNewsScreen(
    navHostController: NavHostController,
    savedNewsViewModel: SavedNewsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val savedNewsList by savedNewsViewModel.savedNewsList.collectAsStateWithLifecycle()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Saved News")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon for Navigate Back"
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            items(
                count = savedNewsList.size,
                key = { index ->
                    savedNewsList[index].newsId ?: ""
                }
            ) { index ->
                val item = savedNewsList[index]
                SavedNewsItem(
                    item = item,
                    context = context,
                    onItemClick = { newsId ->
                        navHostController.navigate(NewsDestination.NEWS_DETAIL_SCREEN(newsId))
                    },
                    animatedVisibilityScope = animatedVisibilityScope,
                    onRemoveFromSavedList = { thisNewsId ->
                        savedNewsViewModel.onNewsRemoveFromSave(thisNewsId)
                    }
                )
            }
        }

    }
}

@Composable
fun SharedTransitionScope.SavedNewsItem(
    item: NewsModel?,
    context: Context,
    onItemClick: (String) -> Unit,
    onRemoveFromSavedList: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    var isRemoveSaveNewsAlertDialogOpen by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 10.dp,
                bottom = 10.dp,
            )
            .clickable {
                onItemClick.invoke(item?.newsId ?: "")
            }
            .sharedElement(
                state = rememberSharedContentState(key = "container/${item?.newsId}"),
                animatedVisibilityScope = animatedVisibilityScope,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Green.copy(0.1f)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    IconButton(
                        onClick = {
                            isRemoveSaveNewsAlertDialogOpen = true
                        },
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Bookmark,
                            contentDescription = "Icon for unSave the News"
                        )
                    }

                    Column(
                        modifier = Modifier
                            .background(
                                color = Black.copy(0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = item?.category ?: "",
                            modifier = Modifier
                                .padding(all = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item?.title ?: "",
                    style = TextStyle(
                        fontSize = (FontSize.MEDIUM - 1).sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(key = "title/${item?.newsId}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                )

                Text(
                    text = item?.description ?: "",
                    style = TextStyle(
                        fontSize = FontSize.SMALL.sp,
                        color = Gray,
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 10.dp)
                )
            }

            val imageRequest = ImageRequest.Builder(context)
                .data(getUrlOfImageNotVideo(item?.urlList ?: emptyList()))
                .crossfade(true)
                .build()

            AsyncImage(
                model = imageRequest,
                contentDescription = "News Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(90.dp)
                    .heightIn(max = 100.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${item?.newsId}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        renderInOverlayDuringTransition = true,
                        clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
                    )
            )
        }
    }

    if (isRemoveSaveNewsAlertDialogOpen) {
        item?.let {
            val imageUrl = getUrlOfImageNotVideo(it.urlList)

            RemoveNewsFromSaveAlertDialog(
                title = it.title ?: "",
                imageUrl = imageUrl,
                onConfirm = {
                    onRemoveFromSavedList.invoke(it.newsId ?: "")
                },
                onDismiss = {
                    isRemoveSaveNewsAlertDialogOpen = false
                }
            )
        }
    }

}

@Composable
fun RemoveNewsFromSaveAlertDialog(
    title: String,
    imageUrl: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "News Image",
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        },
        title = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        },
        text = {
            Text(text = "Are You Sure You want to Remove This News From Saved List?")
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm.invoke()
                onDismiss.invoke()
            }) {
                Text(text = "Remove")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        containerColor = if (isSystemInDarkTheme()) DarkGray else White
    )
}