@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnewsadmin.main.news.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.core.ui.common.OutlinedTextFieldColors
import com.android.studentnewsadmin.main.navigation.Destination
import com.android.studentnewsadmin.main.news.domain.model.CategoryModel
import com.android.studentnewsadmin.main.news.ui.viewmodel.NewsViewModel
import androidx.media3.ui.PlayerView
import com.android.studentnews.core.domain.common.isInternetAvailable
import com.android.studentnewsadmin.core.domain.constants.FontSize
import com.android.studentnewsadmin.core.ui.common.LoadingDialog
import com.android.studentnewsadmin.ui.theme.Green
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@UnstableApi
@SuppressLint("RememberReturnType")
@Composable
fun UploadNewsScreen(
    navHostController: NavHostController,
    newsViewModel: NewsViewModel,
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    val categoryList by newsViewModel.categoryList.collectAsStateWithLifecycle()


    var isImageOrVideoPickDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }
    var isMoreMenuVisible by remember { mutableStateOf(false) }
    var isLinkSheetVisible by remember { mutableStateOf(false) }
    var newsAddingStatus by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var categoryImage by rememberSaveable { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var linkTitle by remember { mutableStateOf("") }


    val uriList = remember { mutableStateListOf<Uri?>() }

    val pagerState = rememberPagerState(
        pageCount = {
            uriList.size
        }
    )


    // Gallery Related
    var galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                uriList.add(it)
            }
        }
    )

    LaunchedEffect(link, linkTitle) {
        if (link.isNotEmpty() && linkTitle.isNotEmpty()) {
            scope.launch {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }


    BackHandler(uriList.isNotEmpty()) {
        uriList.removeAll(uriList)
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Upload News")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uriList.isNotEmpty()) {
                            uriList.removeAll(uriList)
                        } else {
                            navHostController.navigateUp()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon for Navigate Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isMoreMenuVisible = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Icon for Optional Things"
                        )
                    }

                    if (isMoreMenuVisible) {
                        OptionalDropDownMenu(
                            expanded = isMoreMenuVisible,
                            onItemCLick = { index ->
                                if (index == 0) {
                                    scope.launch {
                                        sheetState.show()
                                    }.invokeOnCompletion {
                                        isLinkSheetVisible = true
                                    }
                                }
                            },
                            onDismiss = {
                                isMoreMenuVisible = false
                            }
                        )
                    }

                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        newsAddingStatus = Status.Loading
                        val stringArray =
                            uriList.map { it.toString() }.toTypedArray()
                        newsViewModel
                            .startNewsAddingWorker(
                                title = title,
                                description = description,
                                stringArray = stringArray,
                                category = category,
                                link = link,
                                linkTitle = linkTitle
                            )
                        delay(2000L)
                        newsAddingStatus = Status.Success
                        navHostController.navigate(Destination.MAIN_SCREEN) {
                            popUpTo(Destination.UPLOAD_NEWS_SCREEN) {
                                inclusive = true
                            }
                        }
                    }
                },
                enabled = title.isNotEmpty() && description.isNotEmpty()
                        && uriList.isNotEmpty() &&
                        category.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                    .navigationBarsPadding()
                    .background(color = Color.Transparent),
            ) {
                Text(text = "Upload", color = Color.White)
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(all = 20.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable(uriList.isEmpty()) {

                            isImageOrVideoPickDialogOpen = true

                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                        if (uriList.isNotEmpty()) {
                            HorizontalPager(
                                state = pagerState,
                            ) { page ->
                                val item = uriList[page]

                                val mimeType =
                                    item?.let { url -> context.contentResolver.getType(url) }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {

                                    // Image
                                    if (mimeType.toString().startsWith("image")) {

                                        AsyncImage(
                                            model = item,
                                            contentDescription = "Image For News",
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        )
                                    }

                                    // Video
                                    if (mimeType.toString().startsWith("video")) {

                                        val mediaItem = MediaItem.Builder()
                                            .setUri(item)
                                            .build()

                                        val exoplayer = remember(context, mediaItem) {
                                            ExoPlayer.Builder(context)
                                                .build()
                                                .apply {
                                                    setMediaItem(mediaItem)
                                                    prepare()
                                                    // playWhenReady = true
                                                }
                                        }

                                        DisposableEffect(
                                            AndroidView(
                                                factory = {
                                                    PlayerView(it).apply {
                                                        player = exoplayer
                                                        useController = true
                                                        imageDisplayMode =
                                                            PlayerView.IMAGE_DISPLAY_MODE_FILL
                                                        setShowFastForwardButton(false)
                                                        setShowRewindButton(false)
                                                        setShowNextButton(false)
                                                        setShowPreviousButton(false)
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxSize()
                                            )
                                        ) {
                                            onDispose {
                                                exoplayer.release()
                                            }
                                        }
                                    }

                                    // Icon for Remove Uri
                                    IconButton(
                                        onClick = {
                                            uriList.removeAt(page)
                                        },
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(20.dp)
                                            .align(Alignment.TopEnd)
                                            .padding(end = 5.dp, top = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Icon for remove uri",
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .background(color = Color.White)
                                        )
                                    }
                                }
                            }
                        }

                        if (uriList.isEmpty()) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Icon for Image Picker",
                                modifier = Modifier
                                    .align(alignment = Alignment.Center)
                            )
                        }
                    }
                }

                if (uriList.isNotEmpty()) {

                    FloatingActionButton(
                        onClick = {
                            isImageOrVideoPickDialogOpen = true
                        },
                        containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
                        contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Icon for Add Image or Video",
                        )
                    }

                }

            }

            Spacer(modifier = Modifier.height(5.dp))

            // Pager Indicator
            Row(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                repeat(uriList.size) { index ->
                    Card(
                        modifier = Modifier
                            .width(if (pagerState.currentPage == index) 18.dp else 15.dp)
                            .height(if (pagerState.currentPage == index) 18.dp else 15.dp)
                            .clip(shape = CircleShape)
                            .padding(all = 5.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (pagerState.currentPage == index) {
                                if (isSystemInDarkTheme()) Color.LightGray else Color.Black
                            } else {
                                if (isSystemInDarkTheme()) Color.Black else Color.LightGray
                            }
                        )
                    ) {

                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Category",
                modifier = Modifier
                    .padding(start = 20.dp)
            )

            CategoryDropDownMenu(
                category = category,
                expanded = isCategoryMenuExpanded,
                onExpandedChange = {
                    isCategoryMenuExpanded = !isCategoryMenuExpanded
                },
                categoryList = categoryList,
                onItemCLick = { name, imageUrl ->
                    category = name
                    categoryImage = imageUrl
                },
                onDismiss = {
                    isCategoryMenuExpanded = false
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text(text = "Title")
                },
                colors = OutlinedTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
                    .focusRequester(focusRequester)
            )

            TextField(
                value = description,
                onValueChange = {
                    description = it
                },
                label = {
                    Text(text = "Description")
                },
                colors = OutlinedTextFieldColors(),
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp)
                    .padding(all = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (link.isNotEmpty() && linkTitle.isNotEmpty()) {

                // Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Link:-",
                        style = TextStyle(
                            fontSize = FontSize.SMALL.sp,
                            color = Color.Gray
                        ),
                    )

                    Text(
                        text = link,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (link.startsWith("https://www") && link.endsWith(".com") ||
                                link.endsWith(".com/") && link.isNotEmpty()
                            ) {

                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                context.startActivity(intent)
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Icon for Link"
                        )
                    }
                }

                // Link Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Link Title:-",
                        style = TextStyle(
                            fontSize = FontSize.SMALL.sp,
                            color = Color.Gray
                        ),
                    )
                    Text(text = linkTitle)
                }
            }

        }

        if (isLinkSheetVisible) {
            LinkBottomSheet(
                sheetState = sheetState,
                link = link,
                linkTitle = linkTitle,
                onSave = { thisLink, thisLinkTitle ->
                    link = thisLink
                    linkTitle = thisLinkTitle
                },
                context = context,
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            isLinkSheetVisible = false
                        }
                    }
                }
            )
        }


        if (isImageOrVideoPickDialogOpen) {
            ImageOrVideoPickDialog(
                onImageClick = {
                    galleryLauncher.launch("image/*")
                },
                onVideoClick = {
                    galleryLauncher.launch("video/*")
                },
                onDismiss = {
                    isImageOrVideoPickDialogOpen = false
                }
            )
        }

        if (newsAddingStatus == Status.Loading) {
            LoadingDialog()
        }

    }

}

@Composable
fun ImageOrVideoPickDialog(
    onImageClick: () -> Unit,
    onVideoClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Camera
                    OutlinedButton(
                        onClick = {
                            onImageClick.invoke()
                            onDismiss.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                    ) {
                        Text(text = "Image")
                    }
                    // Gallery
                    OutlinedButton(
                        onClick = {
                            onVideoClick.invoke()
                            onDismiss.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                    ) {
                        Text(text = "Video")
                    }
                }
            }
        }
    )
}

@Composable
fun CategoryDropDownMenu(
    category: String,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    categoryList: List<CategoryModel>,
    onItemCLick: (String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 20.dp),
    ) {
        TextField(
            value = category,
            onValueChange = {},
            colors = OutlinedTextFieldColors(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            placeholder = {
                Text(text = "Select Category")
            },
            modifier = Modifier
                .fillMaxSize()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier
                .background(color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White)
        ) {
            categoryList.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item.name)
                    },
                    onClick = {
                        onItemCLick.invoke(item.name, item.imageUrl)
                        onDismiss.invoke()
                    }
                )
            }
        }
    }
}

@Composable
fun OptionalDropDownMenu(
    expanded: Boolean,
    onItemCLick: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .background(color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White)
    ) {

        DropdownMenuItem(
            text = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = "Link")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Icon for Link"
                        )
                    },
                    onClick = {
                        onItemCLick.invoke(0)
                        onDismiss.invoke()
                    },
                    containerColor = Color.Transparent,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                )
            },
            onClick = {}
        )

    }
}

@Composable
fun LinkBottomSheet(
    sheetState: SheetState,
    link: String,
    linkTitle: String,
    context: Context,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit,
) {

    val focusRequester = remember { FocusRequester() }

    var link by remember { mutableStateOf(link) }
    var linkTitle by remember { mutableStateOf(linkTitle) }


    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {

            Text(
                text = "Insert Link",
                style = TextStyle(
                    fontSize = FontSize.LARGE.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(all = 10.dp)
            )

            // Link
            OutlinedTextField(
                value = link,
                onValueChange = {
                    link = it
                },
                placeholder = {
                    Text(text = "Link")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = Green,
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (link.startsWith("https://www.") && link.endsWith(".com")
                                || link.endsWith(".com/") && link.isNotEmpty()
                            ) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                context.startActivity(intent)
                            }
                        }, enabled = link.startsWith("https://www.") && link.endsWith(".com")
                                || link.endsWith(".com/")
                    ) {
                        Icon(
                            imageVector = if (link.isEmpty()) Icons.Default.Link else {
                                if (link.startsWith("https://www.") && link.endsWith(".com")
                                    || link.endsWith(".com/")
                                )
                                    Icons.Default.Link else Icons.Default.LinkOff
                            },
                            contentDescription = "Icon for Link"
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
                    .focusRequester(focusRequester)
            )

            // Link Title
            OutlinedTextField(
                value = linkTitle,
                onValueChange = {
                    linkTitle = it
                },
                placeholder = {
                    Text(text = "Link Title")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = Green,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .align(alignment = Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }

                TextButton(onClick = {
                    if (link.isEmpty() || linkTitle.isEmpty()) {
                        Toast.makeText(context, "Please add Info!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (link.startsWith("https://www") && link.endsWith(".com") || link.endsWith(
                                ".com/"
                            )
                        ) {
                            onSave.invoke(link, linkTitle)
                            onDismiss.invoke()
                        } else {
                            Toast.makeText(context, "Invalid Link", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "Save")
                }

            }

        }

    }
}