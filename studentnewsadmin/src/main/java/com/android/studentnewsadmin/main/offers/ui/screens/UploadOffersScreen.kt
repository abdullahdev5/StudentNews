package com.android.studentnewsadmin.main.offers.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.core.ui.common.OutlinedTextFieldColors
import com.android.studentnewsadmin.main.navigation.Destination
import com.android.studentnewsadmin.main.offers.domain.constant.OfferTypes
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


    var offerName by rememberSaveable { mutableStateOf("10% Off Your First Purchase") }
    var offerDescription by rememberSaveable { mutableStateOf("Enjoy a 10% discount on your first order with us.") }
    var pointsRequiredString by rememberSaveable { mutableStateOf("15") }

    val offerTypesList = listOf(
        OfferTypes.ACTIVE,
        OfferTypes.DISCOUNT,
        OfferTypes.INACTIVE,
        OfferTypes.EXPIRED,
    )
    var selectedOfferType by rememberSaveable { mutableStateOf(OfferTypes.ACTIVE) }
    var isOfferTypesDropDownMenuVisible by rememberSaveable { mutableStateOf(false) }

    var discountAmountString by rememberSaveable { mutableStateOf("10") }

    var offerTermsAndCondition by rememberSaveable { mutableStateOf("This offer is valid for first-time customers only.") }

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
        targetValue = offersViewModel.uploadingProgress,
        label = ""
    )

    LaunchedEffect(offersViewModel.uploadingStatus) {
        if (offersViewModel.uploadingStatus == Status.Success) {
            offerImageUri = Uri.EMPTY
            offerName = ""
            offerDescription = ""
            pointsRequiredString = ""
            selectedOfferType = ""
            discountAmountString = ""
            offerTermsAndCondition = ""

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
                            offerImageUri = offerImageUri,
                            offerDescription = offerDescription,
                            pointsRequired = pointsRequiredString.toDouble(),
                            offerType = selectedOfferType,
                            discountAmount = discountAmountString.ifEmpty { null }?.toDouble(),
                            offerTermsAndCondition = offerTermsAndCondition,
                            context = context,
                        )
                },
                enabled = offerImageUri != Uri.EMPTY
                        && offerName.isNotEmpty() && offerDescription.isNotEmpty()
                        && pointsRequiredString.isNotEmpty() && if (selectedOfferType == OfferTypes.DISCOUNT)
                    discountAmountString.isNotEmpty() else true,
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
                            .padding(all = 10.dp)
                            .focusRequester(focusRequester)
                    )

                    // Offer Description
                    Text(
                        text = "Offer Description",
                        modifier = Modifier
                            .padding(all = 10.dp)
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

                    // Points Required
                    Text(
                        text = "Points Required",
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )

                    TextField(
                        value = pointsRequiredString,
                        onValueChange = {
                            pointsRequiredString = it
                        },
                        label = {
                            Text(text = "Points Required")
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

                    // Offer Type
                    Text(
                        text = "Offer Type",
                        modifier = Modifier
                            .padding(all = 10.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = isOfferTypesDropDownMenuVisible,
                        onExpandedChange = {
                            isOfferTypesDropDownMenuVisible = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    ) {
                        TextField(
                            value = selectedOfferType,
                            onValueChange = {},
                            placeholder = {
                                Text(text = "Offer Type")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    isOfferTypesDropDownMenuVisible
                                )
                            },
                            colors = OutlinedTextFieldColors(),
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isOfferTypesDropDownMenuVisible,
                            onDismissRequest = {
                                isOfferTypesDropDownMenuVisible = false
                            },
                            modifier = Modifier
                                .background(color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White)
                        ) {
                            for (offerType in offerTypesList) {
                                DropdownMenuItem(
                                    text = { Text(text = offerType) },
                                    onClick = {
                                        selectedOfferType = offerType
                                        isOfferTypesDropDownMenuVisible = false
                                        if (offerType != OfferTypes.DISCOUNT) {
                                            discountAmountString = ""
                                        }
                                    }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(selectedOfferType == OfferTypes.DISCOUNT) {
                        Column {
                            // Discount Amount
                            Text(
                                text = "Discount Amount",
                                modifier = Modifier
                                    .padding(all = 10.dp)
                            )

                            TextField(
                                value = discountAmountString,
                                onValueChange = {
                                    discountAmountString = it
                                },
                                label = {
                                    Text(text = "Discount Amount")
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

                    // Offer Terms and Condition
                    Text(
                        text = "Offer Terms and Condition (optional)",
                        modifier = Modifier
                            .padding(all = 10.dp)
                    )

                    TextField(
                        value = offerTermsAndCondition,
                        onValueChange = {
                            offerTermsAndCondition = it
                        },
                        label = {
                            Text(text = "Terms and Condition")
                        },
                        colors = OutlinedTextFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                        ),
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    )


                }

            }

        }

    }

}