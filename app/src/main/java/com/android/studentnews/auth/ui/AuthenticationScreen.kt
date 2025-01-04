@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.auth.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnews.navigation.SubGraph
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.domain.resource.AuthenticationStatus
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.auth.domain.resource.UserState
import com.android.studentnews.auth.ui.viewModel.AuthViewModel
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.AuthenticationImages
import com.android.studentnews.core.ui.composables.ButtonColors
import com.android.studentnews.core.ui.composables.LoadingDialog
import com.android.studentnews.core.ui.composables.OutlinedTextFieldColors
import com.android.studentnews.core.ui.components.TextFieldComponent
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun AuthenticationScreen(
    navHostController: NavHostController,
    arguments: RegistrationData,
    comeFor: String,
    authViewModel: AuthViewModel,
) {

    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    // Verification
    var creationStatus by rememberSaveable { mutableStateOf("") }

    // Authentication
    var authenticationStatus by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }


    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(
        key1 = creationStatus,
        key2 = authenticationStatus,
    ) {
        if (creationStatus == Status.FAILED || authenticationStatus == Status.FAILED) {
            focusRequester.requestFocus()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Authentication")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIos,
                            contentDescription = "Icon for Navigation Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .statusBarsPadding(),
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        scope.launch {
                            SnackBarController.sendEvent(
                                SnackBarEvents(
                                    message = "email and password can't be empty",
                                    duration = SnackbarDuration.Long,
                                )
                            )
                        }
                    } else {
                        if (password.length < 6) {

                        } else {
                            scope.launch(Dispatchers.Main) {
                                if (comeFor == AuthenticationStatus.forCreation) {
                                    authViewModel
                                        .SignUpUser(
                                            email = email,
                                            password = password,
                                            registrationData = RegistrationData(
                                                name = arguments.name,
                                                degree = arguments.degree,
                                                degreeTitle = arguments.degreeTitle,
                                                semester = arguments.semester,
                                                countryCode = arguments.countryCode,
                                                number = arguments.number,
                                                phoneNumber = arguments.phoneNumber,
                                                city = arguments.city,
                                                address = arguments.address,
                                            ),
                                        ).collect { result ->
                                            when (result) {
                                                is UserState.Failed -> {
                                                    creationStatus = Status.FAILED
                                                    errorMessage =
                                                        result.error.localizedMessage ?: ""
                                                }

                                                UserState.Loading -> {
                                                    creationStatus = Status.Loading
                                                }

                                                is UserState.Created -> {
                                                    creationStatus = Status.SUCCESS
                                                    navHostController.navigate(SubGraph.Main) {
                                                        popUpTo(SubGraph.AUTH) {
                                                            inclusive = true
                                                        }
                                                        launchSingleTop = true
                                                    }
                                                    scope.launch {
                                                        SnackBarController.sendEvent(
                                                            SnackBarEvents(
                                                                message = result.data,
                                                                duration = SnackbarDuration.Long,
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                } else if (comeFor == AuthenticationStatus.forAuthentication) {
                                    authViewModel
                                        .SignInUser(
                                            email = email,
                                            password = password,
                                        ).collect { result ->
                                            when (result) {
                                                is UserState.Failed -> {
                                                    authenticationStatus = Status.FAILED
                                                    errorMessage =
                                                        result.error.localizedMessage ?: ""
                                                }

                                                UserState.Loading -> {
                                                    authenticationStatus = Status.Loading
                                                }

                                                is UserState.Created -> {
                                                    authenticationStatus = Status.SUCCESS
                                                    navHostController.navigate(SubGraph.Main) {
                                                        popUpTo(SubGraph.AUTH) {
                                                            inclusive = true
                                                        }
                                                        launchSingleTop = true
                                                    }
                                                    scope.launch {
                                                        SnackBarController.sendEvent(
                                                            SnackBarEvents(
                                                                message = result.data,
                                                                duration = SnackbarDuration.Long,
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                },
                enabled = if (password.length >= 6) true else false,
                colors = ButtonColors(
                    containerColor = Green,
                    contentColor = White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = if (comeFor == AuthenticationStatus.forCreation)
                        "Create" else "Verify"
                )
            }
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding)
                    .verticalScroll(scrollState),
            ) {
                AsyncImage(
                    model = if (comeFor == AuthenticationStatus.forCreation)
                        AuthenticationImages.SIGN_UP_IMAGE
                    else AuthenticationImages.SIGN_IN_IMAGE,
                    contentDescription = "image for Authentication for Better UI",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .clip(shape = RoundedCornerShape(25.dp))
                )


                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = if (comeFor == AuthenticationStatus.forCreation) "Create Account"
                    else "Verify Account",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(50.dp))
                Column(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                ) {
                    // Email
                    TextFieldComponent(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        label = {
                            Text(text = "Email")
                        },
                        colors = OutlinedTextFieldColors(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Icon for Email"
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        singleLine = true,
                        isError = if (creationStatus == Status.FAILED || authenticationStatus == Status.FAILED
                        ) true else false,
                        supportingText = {
                            Text(
                                text = if (creationStatus == Status.FAILED || authenticationStatus == Status.FAILED
                                ) errorMessage else ""
                            )
                        },
                        suffix = {
                            Text(
                                text = "@domain.com",
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                            .focusRequester(focusRequester)
                    )


                    // Password
                    TextFieldComponent(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        label = {
                            Text(text = "Password")
                        },
                        colors = OutlinedTextFieldColors(),
                        trailingIcon = {
                            if (password.isEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Icon for Password"
                                )
                            } else {
                                IconButton(onClick = {
                                    isPasswordVisible = !isPasswordVisible
                                }) {
                                    if (isPasswordVisible) {
                                        Icon(
                                            imageVector = Icons.Filled.Visibility,
                                            contentDescription = "Icon for Visible Password"
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.VisibilityOff,
                                            contentDescription = "Icon for InVisible Password"
                                        )
                                    }
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        singleLine = true,
                        isError = if (password.isNotEmpty() && password.length < 6) true else false,
                        supportingText = {
                            Text(
                                text = if (password.isNotEmpty() && password.length < 6)
                                    "Password must be at least 6 characters"
                                else ""
                            )
                        },
                        visualTransformation = if (isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    )
                }

            }
        }

        if (creationStatus == Status.Loading) {
            LoadingDialog()
        }

        if (authenticationStatus == Status.Loading) {
            LoadingDialog()
        }


    }


}