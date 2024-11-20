@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnewsadmin.main.news.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnewsadmin.core.domain.common.ConvertUriToBitmap
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.core.domain.resource.CategoryState
import com.android.studentnewsadmin.core.ui.common.CircularIndicatorWithProgress
import com.android.studentnewsadmin.core.ui.common.ImagePickerDialog
import com.android.studentnewsadmin.core.ui.common.OutlinedTextFieldColors
import com.android.studentnewsadmin.main.navigation.Destination
import com.android.studentnewsadmin.main.news.ui.viewmodel.NewsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UploadCategoryScreen(
    navHostController: NavHostController,
    newsViewModel: NewsViewModel
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()


    var isImagePickerDialogOpen by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var categoryAddingStatus by remember { mutableStateOf("") }


    var category by remember { mutableStateOf("") }

    // Gallery Related
    var galleryImageUri by remember { mutableStateOf<Uri?>(null) }
    var galleryImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            galleryImageUri = uri
        }
    )

    // Camera Related
    var cameraImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            cameraImageBitmap = bitmap
        }
    )
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isPermissionGranted ->
            if (isPermissionGranted) {
                cameraLauncher.launch()
            }
        }
    )

    LaunchedEffect(galleryImageUri) {
        galleryImageBitmap = galleryImageUri?.let {
            ConvertUriToBitmap(it, context)
        }
    }

    BackHandler {
        if (galleryImageUri != null || cameraImageBitmap != null) {
            galleryImageUri = null
            galleryImageBitmap = null
            cameraImageBitmap = null
        } else {
            navHostController.navigateUp()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Category")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (galleryImageUri != null || cameraImageBitmap != null) {
                            galleryImageUri = null
                            galleryImageBitmap = null
                            cameraImageBitmap = null
                        } else {
                            navHostController.navigateUp()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon for Navigate Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch(Dispatchers.Main) {
                        newsViewModel
                            .onCategoryAdd(
                                category = category,
                                imageBitmap = galleryImageBitmap?.let {
                                    it
                                } ?: cameraImageBitmap!!
                            ).collect { result ->
                                when (result) {
                                    is CategoryState.Failed -> {
                                        categoryAddingStatus = Status.Failed
                                        Toast.makeText(
                                            context, result.error.message.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    CategoryState.Loading -> {
                                        categoryAddingStatus = Status.Loading
                                    }

                                    is CategoryState.Progress -> {
                                        progress = result.progress
                                    }

                                    is CategoryState.Success -> {
                                        Toast.makeText(context, result.data, Toast.LENGTH_SHORT)
                                            .show()
                                        galleryImageUri = null
                                        galleryImageBitmap = null
                                        cameraImageBitmap = null
                                        category = ""
                                        navHostController.navigate(Destination.MAIN_SCREEN) {
                                            popUpTo(Destination.UPLOAD_CATEGORY_SCREEN) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                        categoryAddingStatus = Status.Success
                                    }
                                }
                            }
                    }
                },
                enabled = category.isNotEmpty() && cameraImageBitmap != null || galleryImageBitmap != null,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                    .navigationBarsPadding()
                    .background(color = Color.Transparent),
            ) {
                Text(text = "Add Category")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .verticalScroll(scrollState),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Category Image (required)",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .align(alignment = Alignment.Start)
                )

                // Image Picker Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(all = 20.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable {
                            isImagePickerDialogOpen = true
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        if (galleryImageUri != null || cameraImageBitmap != null) {
                            AsyncImage(
                                model = galleryImageBitmap ?: cameraImageBitmap,
                                contentDescription = "Image For News",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }

                        if (galleryImageUri == null && cameraImageBitmap == null) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Icon for Image Picker",
                                modifier = Modifier
                                    .align(alignment = Alignment.Center)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                // Category Text Field
                TextField(
                    value = category,
                    onValueChange = {
                        category = it
                    },
                    label = {
                        Text(text = "Category")
                    },
                    colors = OutlinedTextFieldColors(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Icon for Category"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp)
                )
            }

        }


        if (isImagePickerDialogOpen) {
            ImagePickerDialog(
                onCameraClick = {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                onGalleryClick = {
                    galleryLauncher.launch("image/*")
                },
                onDismiss = {
                    isImagePickerDialogOpen = false
                }
            )
        }

        if (categoryAddingStatus == Status.Loading) {
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
            )
            CircularIndicatorWithProgress(animatedProgress)
        }


    }

}