@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnewsadmin.main.news.ui.screens


import android.annotation.SuppressLint
import com.android.studentnewsadmin.main.news.domain.model.NewsModel
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.studentnewsadmin.core.domain.constants.FontSize
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.core.domain.resource.NewsState
import com.android.studentnewsadmin.core.ui.common.LoadingDialog
import com.android.studentnewsadmin.main.navigation.Destination
import com.android.studentnewsadmin.main.news.ui.viewmodel.NewsViewModel
import com.android.studentnewsadmin.ui.theme.Green
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsScreen(
    navHostController: NavHostController,
    newsViewModel: NewsViewModel
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var newsDeletedStatus by remember { mutableStateOf("") }


    val newsList = newsViewModel.newsList.collectAsStateWithLifecycle()


    Surface {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {

            items(newsList.value.size) { index ->
                val item = newsList.value[index]

                NewsItem(
                    item = item,
                    context = context,
                    onNewsDelete = { newsId ->
                        scope.launch(Dispatchers.Main) {
                            newsViewModel
                                .onNewsDelete(newsId)
                                .collect { result ->
                                    when (result) {
                                        is NewsState.Failed -> {
                                            newsDeletedStatus = Status.Failed
                                            Toast.makeText(
                                                context,
                                                result.message.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        NewsState.Loading -> {
                                            newsDeletedStatus = Status.Loading
                                        }

                                        is NewsState.Progress -> {}
                                        is NewsState.Success -> {
                                            newsDeletedStatus = Status.Success
                                            Toast.makeText(
                                                context,
                                                result.data,
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                    }
                                }
                        }
                    }
                )
            }

        }

        if (newsViewModel.newsListStatus.value == Status.Loading) {
            LoadingDialog()
        }

        if (newsDeletedStatus == Status.Loading) {
            LoadingDialog()
        }

        if (newsViewModel.newsListStatus.value == Status.Failed) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = newsViewModel.errorMsg.value)
            }
        }

    }

}

@Composable
fun NewsItem(
    item: NewsModel,
    context: Context,
    onNewsDelete: (String) -> Unit,
) {

    var offsetx by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var maxWidth = 150.dp
    var draggedItemId by remember { mutableStateOf("") }


    Row {
        AnimatedVisibility(
            offsetx != 0f,
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .background(color = Color.Red)
                    .clickable {
                        isDragging = false
                        offsetx = 0f
                        onNewsDelete.invoke(draggedItemId)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Icon for Delete News",
                    tint = Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 10.dp,
                )
                .offset {
                    IntOffset(offsetx.roundToInt(), 0)
                }
                .pointerInput(true) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            if (offsetx == maxWidth.toPx() - 100.dp.toPx()) {
                                draggedItemId = item.newsId ?: ""
                                isDragging = false
                                // offsetx = 0f
                            } else {
                                offsetx = 0f
                                isDragging = false
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            val newOffsetX = offsetx + dragAmount
                            offsetx =
                                newOffsetX.coerceIn(
                                    0f,
                                    maxWidth.toPx() - 100.dp.toPx()
                                )
                        },
                    )
                },
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray.copy(
                    alpha = 0.3f
                )
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

                    Column(
                        modifier = Modifier
                            .background(
                                color = Color.Black.copy(0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = item.category ?: "",
                            modifier = Modifier
                                .padding(all = 2.dp)
                        )
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
                    )
                    Text(
                        text = item?.description ?: "",
                        style = TextStyle(
                            fontSize = FontSize.SMALL.sp,
                            color = Color.Gray,
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 10.dp)
                    )
                }

                var index = rememberSaveable { mutableIntStateOf(0) }

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(
                            if (item.urlList.get(index.intValue).contentType.startsWith("image"))
                                item.urlList.get(index.intValue).url
                            else item.urlList.get(index.intValue++).url
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = "News Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(90.dp)
                        .heightIn(max = 100.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

@Composable
fun DrawerContent(
    onUploadNewsClick: () -> Unit,
    onUploadCategoryClick: () -> Unit,
    onUploadEvents: () -> Unit,
) {
    // Add News
    NavigationDrawerItem(
        label = {
            Text(text = "Upload News")
        },
        icon = {
            Icon(
                imageVector = Icons.Default.PostAdd,
                contentDescription = "Icon for News"
            )
        },
        onClick = {
            onUploadNewsClick.invoke()
        },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    // Add Category
    NavigationDrawerItem(
        label = {
            Text(text = "Upload Category")
        },
        onClick = {
            onUploadCategoryClick.invoke()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = "Icon for Category"
            )
        },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    // Add Events
    NavigationDrawerItem(
        label = {
            Text(text = "Upload Events")
        },
        onClick = {
            onUploadEvents.invoke()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "Icon for Events"
            )
        },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )
}