package com.android.studentnewsadmin.main.offers.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.asDoubleState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.core.ui.common.LoadingDialog
import com.android.studentnewsadmin.core.ui.common.OutlinedTextFieldColors
import com.android.studentnewsadmin.main.navigation.Destination
import com.android.studentnewsadmin.main.offers.ui.viewModel.OffersViewModel
import com.android.studentnewsadmin.ui.theme.Green
import kotlin.let
import kotlin.text.toDouble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadOffersScreen(
    navHostController: NavHostController,
    offersViewModel: OffersViewModel,
) {

    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    var offerName by rememberSaveable { mutableStateOf("Best Offer of tis month, for you") }
    var offerDescription by rememberSaveable { mutableStateOf("collect this offer until ") }
    var pointsWhenAbleToCollectString by rememberSaveable { mutableStateOf("10") }
    var pointsWhenAbleToCollectDouble = remember(pointsWhenAbleToCollectString) {
        derivedStateOf {
            if (pointsWhenAbleToCollectString.isNotEmpty())
                pointsWhenAbleToCollectString.toDouble() else 0.0
        }
    }.asDoubleState().doubleValue

    var offerImageUri by rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                offerImageUri = uri
            }
        }
    )


    val animatedProgress by animateFloatAsState(
        targetValue = offersViewModel.progress,
        label = ""
    )

    LaunchedEffect(offersViewModel.uploadingStatus) {
        if (offersViewModel.uploadingStatus == Status.Success) {
            offerImageUri = Uri.EMPTY
            offerName = ""
            offerDescription = ""
            pointsWhenAbleToCollectString = ""
            pointsWhenAbleToCollectDouble = 0.0

            navHostController.navigate(Destination.MAIN_SCREEN) {
                popUpTo(Destination.UPLOAD_OFFERS_SCREEN) {
                    inclusive = true
                }
            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Upload Offers") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.navigateUp() }) {
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
                    offersViewModel
                        .onOfferUpload(
                            offerName = offerName,
                            offerDescription = offerDescription,
                            offerImageUri = offerImageUri,
                            pointsWhenAbleToCollect = pointsWhenAbleToCollectDouble,
                            context = context,
                        )
                },
                enabled = offerImageUri != Uri.EMPTY
                        && offerName.isNotEmpty() && pointsWhenAbleToCollectString.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                    .navigationBarsPadding()
            ) {
                Text(text = "Upload")
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
        ) {
            if (offersViewModel.uploadingStatus == Status.Loading) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = Green,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(all = 20.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable(
                            enabled = offerImageUri == Uri.EMPTY,
                            role = Role.Image
                        ) {
                            galleryPermissionLauncher.launch("image/*")
                        }
                ) {
                    if (offerImageUri != Uri.EMPTY) {
                        AsyncImage(
                            model = offerImageUri,
                            contentDescription = "Offer Image",
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoAlbum,
                                contentDescription = "Icon for Pick the Photo",
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Offer Name
                    Text(
                        text = "Offer Name",
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )

                    TextField(
                        value = offerName,
                        onValueChange = {
                            offerName = it
                        },
                        label = {
                            Text(text = "Offer Name")
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

                    // Offer Description
                    Text(
                        text = "Offer Description (optional)",
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )

                    TextField(
                        value = offerDescription,
                        onValueChange = {
                            offerDescription = it
                        },
                        label = {
                            Text(text = "Offer Description")
                        },
                        colors = OutlinedTextFieldColors(),
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 250.dp)
                            .padding(all = 20.dp)
                    )

                    // Points When Able to Collect
                    Text(
                        text = "Points When Able to Collect?",
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )

                    TextField(
                        value = pointsWhenAbleToCollectString,
                        onValueChange = {
                            pointsWhenAbleToCollectString = it
                        },
                        label = {
                            Text(text = "Points When Able to Collect")
                        },
                        colors = OutlinedTextFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    )
                }

            }

        }

    }

}