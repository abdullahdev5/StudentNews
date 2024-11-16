@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.android.studentnews.main.account.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CastForEducation
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.common.ConvertUriToBitmap
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.CircularIndicatorWithProgress
import com.android.studentnews.core.ui.common.ImagePickerDialog
import com.android.studentnews.core.ui.common.OutlinedTextFieldColors
import com.android.studentnews.core.ui.components.TextFieldComponent
import com.android.studentnews.main.account.domain.AccountDataLabel
import com.android.studentnews.main.account.domain.AccountList
import com.android.studentnews.main.account.domain.resource.AccountState
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SharedTransitionScope.AccountScreen(
    navHostController: NavHostController,
    accountViewModel: AccountViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val currentUser by accountViewModel.currentUser.collectAsStateWithLifecycle()

    var isImagePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isEditNameSheetVisible by rememberSaveable { mutableStateOf(false) }

    // Gallery
    var galleryImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var galleryImageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    // Launcher
    var galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            galleryImageUri = uri
        }
    )

    // Camera
    var cameraImageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    // Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            cameraImageBitmap = bitmap
        }
    )
    // permission
    val cameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraLauncher.launch()
            }
        }
    )


    LaunchedEffect(galleryImageUri) {
        galleryImageUri?.let { uri ->
            galleryImageBitmap = ConvertUriToBitmap(uri, context)
        }
    }

    LaunchedEffect(accountViewModel.userImageSavedStatus.value) {
        if (accountViewModel.userImageSavedStatus.value == Status.SUCCESS) {
            cameraImageBitmap = null
            galleryImageBitmap = null
            galleryImageUri = null
        }
    }

    BackHandler(galleryImageBitmap != null || cameraImageBitmap != null) {
        galleryImageUri = null
        galleryImageBitmap = null
        cameraImageBitmap = null
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Account")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (galleryImageBitmap != null || cameraImageBitmap != null) {
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
                },
                actions = {
                    if (galleryImageBitmap != null || cameraImageBitmap != null) {

                        TextButton(
                            onClick = {
                                galleryImageUri = null
                                galleryImageBitmap = null
                                cameraImageBitmap = null
                            },
                            enabled = accountViewModel.userImageSavedStatus.value != Status.Loading
                        ) {
                            Text(text = "Discard")
                        }

                        TextButton(
                            onClick = {
                                accountViewModel.onUserImageSave(
                                    galleryImageBitmap?.let {
                                        it
                                    } ?: cameraImageBitmap!!
                                )
                            },
                            enabled = accountViewModel.userImageSavedStatus.value != Status.Loading
                        ) {
                            Text(text = "Save")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .verticalScroll(scrollState)
        ) {

            if (accountViewModel.userImageSavedStatus.value == Status.Loading) {
                LinearProgressIndicator(
                    color = Green,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            // User Image Box
            Column(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {

                    Card(
                        modifier = Modifier
                            .width(170.dp)
                            .height(170.dp)
                            .clip(shape = CircleShape)
                            .border(
                                width = 1.dp,
                                color = Gray,
                                shape = CircleShape
                            )
                            .sharedElement(
                                state = rememberSharedContentState(key = "user_image/${currentUser?.uid}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                clipInOverlayDuringTransition = OverlayClip(CircleShape)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentUser?.profilePic.isNullOrEmpty())
                                Color(currentUser?.profilePicBgColor ?: 0)
                            else
                                Color.LightGray
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                        ) {
                            if (galleryImageBitmap != null || cameraImageBitmap != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(
                                            galleryImageBitmap?.let {
                                                it
                                            } ?: cameraImageBitmap
                                        )
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "User Image Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }

                            if (galleryImageBitmap == null && cameraImageBitmap == null) {
                                if (currentUser?.profilePic.isNullOrEmpty()) {
                                    Text(
                                        text = currentUser?.registrationData?.name?.first()?.toString() ?: "",
                                        color = White,
                                        fontSize = (FontSize.LARGE + 5).sp,
                                        modifier = Modifier
                                            .align(alignment = Alignment.Center)
                                            .shadow(
                                                elevation = 20.dp,
                                                shape = CircleShape,
                                                ambientColor = if (isSystemInDarkTheme()) White else Black,
                                                spotColor = if (isSystemInDarkTheme()) White else Black
                                            )
                                    )
                                } else {
                                    val imageRequest = ImageRequest.Builder(context)
                                        .data(currentUser?.profilePic ?: "")
                                        .crossfade(true)
                                        .build()
                                    AsyncImage(
                                        model = imageRequest,
                                        contentDescription = "User Image",
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                            }
                        }
                    }
                    // Image Picker Card
                    SmallFloatingActionButton(
                        onClick = {
                            if (accountViewModel.userImageSavedStatus.value != Status.Loading) {
                                isImagePickerDialogOpen = true
                            }
                        },
                        containerColor = Green,
                        contentColor = White,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        shape = CircleShape,
                        modifier = Modifier
                            .align(alignment = Alignment.BottomEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Icon for Image Picker"
                        )
                    }
                } // End of Use Image Box


                // Current User Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentUser?.registrationData?.name ?: "",
                        style = TextStyle(
                            fontSize = FontSize.LARGE.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = "user_name/${currentUser?.uid}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                renderInOverlayDuringTransition = true
                            )
                    )

                    IconButton(onClick = {
                        scope.launch {
                            sheetState.show()
                        }.invokeOnCompletion {
                            isEditNameSheetVisible = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Icon for Edit Name",
                            tint = Green
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // User Data List

            val accountList = listOf(
                AccountList(
                    label = "Email",
                    value = currentUser?.email ?: ""
                ),
                AccountList(
                    label = "Degree",
                    value = currentUser?.registrationData?.degree ?: ""
                ),
                AccountList(
                    label = "Degree Title",
                    value = currentUser?.registrationData?.degreeTitle ?: ""
                ),
                AccountList(
                    label = "Semester",
                    value = currentUser?.registrationData?.semester ?: ""
                ),
                AccountList(
                    label = "Phone Number",
                    value = currentUser?.registrationData?.phoneNumber ?: ""
                ),
                AccountList(
                    label = "City",
                    value = currentUser?.registrationData?.city ?: ""
                ),
                AccountList(
                    label = "Address",
                    value = currentUser?.registrationData?.address ?: ""
                )
            )

            Column(
                modifier = Modifier
                    .padding(bottom = 20.dp)
            ) {
                accountList.forEachIndexed { index, item ->
                    TextFieldComponent(
                        value = item.value,
                        onValueChange = {},
                        label = {
                            Text(text = item.label)
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Green,
                            unfocusedIndicatorColor = Gray,
                            focusedLabelColor = Green,
                            unfocusedLabelColor = Gray,
                            focusedTrailingIconColor = Green,
                        ),
                        trailingIcon = {
                            if (item.label == AccountDataLabel.EMAIL) {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = "Icon for Email"
                                )
                            }
                            if (item.label == AccountDataLabel.PHONE_NUMBER) {
                                Icon(
                                    imageVector = Icons.Outlined.Call,
                                    contentDescription = "Icon for Phone Number"
                                )
                            }
                            if (item.label == AccountDataLabel.SEMESTER) {
                                Icon(
                                    imageVector = Icons.Outlined.CastForEducation,
                                    contentDescription = "Icon for Semester"
                                )
                            }
                            if (item.label == AccountDataLabel.ADDRESS) {
                                Icon(
                                    imageVector = Icons.Outlined.MyLocation,
                                    contentDescription = "Icon for Address"
                                )
                            }
                        },
                        readOnly = true,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 10.dp,
                            )
                            .focusable(enabled = false)
                    )
                }
            }

        }

        if (isImagePickerDialogOpen) {
            ImagePickerDialog(
                onCameraClick = {
                    isImagePickerDialogOpen = false
                    cameraPermission.launch(android.Manifest.permission.CAMERA)
                },
                onGalleryClick = {
                    isImagePickerDialogOpen = false
                    galleryLauncher.launch("image/*")
                },
                onDismiss = {
                    isImagePickerDialogOpen = false
                }
            )
        }

        if (isEditNameSheetVisible) {
            EditNameBottomSheet(
                sheetState = sheetState,
                name = currentUser?.registrationData?.name ?: "",
                onSave = { username ->
                    scope.launch {
                        accountViewModel.onUsernameSave(username)
                    }
                },
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            isEditNameSheetVisible = false
                        }
                    }
                }
            )
        }

    }


}

@Composable
fun EditNameBottomSheet(
    sheetState: SheetState,
    name: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {

    val focusRequester = remember { FocusRequester() }
    var name by remember { mutableStateOf(name) }


    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {

        Column {

            Text(
                text = "Your Name",
                style = TextStyle(
                    fontSize = FontSize.LARGE.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .padding(all = 10.dp)
            )

            TextFieldComponent(
                value = name,
                onValueChange = {
                    name = it
                },
                colors = OutlinedTextFieldColors(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Icon for Edit Name"
                    )
                },
                singleLine = true,
                supportingText = {
                    Text(text = if (name.isEmpty()) "Name can't be empty" else "")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(all = 10.dp)
                    .align(Alignment.End),
            ) {

                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }

                TextButton(
                    onClick = {
                        onSave.invoke(name)
                        onDismiss.invoke()
                    },
                    enabled = name.isNotEmpty()
                ) {
                    Text(text = "Save")
                }

            }

        }
    }
}