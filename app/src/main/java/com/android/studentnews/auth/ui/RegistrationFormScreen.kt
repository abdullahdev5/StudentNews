@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.auth.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import com.android.studentnews.auth.domain.RegistrationDropDownMenuLists
import com.android.studentnews.auth.domain.destination.AuthDestination
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.domain.resource.AuthenticationStatus
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.ui.common.OutlinedTextFieldColors
import com.android.studentnews.core.ui.components.TextFieldComponent
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.Red
import com.android.studentnews.ui.theme.White
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegistrationFormScreen(
    navHostController: NavHostController
) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }

    // degree
    var degree by rememberSaveable { mutableStateOf("") }
    var isDegreeDropDownMenuOpen by rememberSaveable { mutableStateOf(false) }
    val degreeList = RegistrationDropDownMenuLists.degreeList

    // degree title
    var degreeTitle by rememberSaveable { mutableStateOf("") }
    var isDegreeTitleDropDownMenuOpen by rememberSaveable { mutableStateOf(false) }
    val degreeTitleList = RegistrationDropDownMenuLists.degreeTitleList

    // Semester
    var semester by rememberSaveable { mutableStateOf("") }

    // Phone Number
    var countryCode by rememberSaveable { mutableStateOf("+92") }
    var number by rememberSaveable { mutableStateOf("") }
    val phoneNumber = countryCode + number

    // city
    var city by rememberSaveable { mutableStateOf("") }
    var isCityDropDownMenuOpen by rememberSaveable { mutableStateOf(false) }
    val cityList = RegistrationDropDownMenuLists.cityList

    // Address
    var address by rememberSaveable { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Registration")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (
                        name.isEmpty() || degree.isEmpty() ||
                        degreeTitle.isEmpty() || semester.isEmpty() ||
                        countryCode.isEmpty() || number.isEmpty() ||
                        city.isEmpty() || address.isEmpty()
                    ) {
                        scope.launch {
                            SnackBarController.sendEvent(
                                SnackBarEvents(
                                    message = "Information can't be Empty",
                                    duration = SnackbarDuration.Long,
                                )
                            )
                        }
                    } else if (!semester.isDigitsOnly() || semester.length > 1) {
                        // Nothing
                    } else if (number.length < 10 || !number.isDigitsOnly()) {
                        // Nothing
                    } else if (countryCode.length < 2) {
                        // Nothing
                    } else {
                        navHostController.navigate(
                            AuthDestination.AUTHENTICATION_SCREEN(
                                comeFor = AuthenticationStatus.forCreation,
                                registrationData = RegistrationData(
                                    name = name,
                                    degree = degree,
                                    degreeTitle = degreeTitle,
                                    semester = semester,
                                    countryCode = countryCode,
                                    number = number,
                                    phoneNumber = phoneNumber,
                                    city = city,
                                    address = address
                                )
                            )
                        ) {
                            restoreState = true
                        }
                    }
                },
                containerColor = Green,
                contentColor = White,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = "Navigate Next Icon"
                )
            }
        },

        ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
        ) {
            // Name
            Text(
                text = "Name",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp)
            )
            TextFieldComponent(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text(text = "Name")
                },
                colors = OutlinedTextFieldColors(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Icon for Person Name"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                        isDegreeDropDownMenuOpen = true
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                        top = 10.dp
                    )
                    .focusRequester(focusRequester),
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Degree
            Text(
                text = "Degree",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 90.dp)
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                        top = 10.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Degree
                DegreeDropDownMenu(
                    degree = degree,
                    degreeList = degreeList,
                    expanded = isDegreeDropDownMenuOpen,
                    onExpandedChange = {
                        isDegreeDropDownMenuOpen = !isDegreeDropDownMenuOpen
                    },
                    onItemCLick = {
                        degree = it
                        focusManager.moveFocus(FocusDirection.Right)
                        isDegreeTitleDropDownMenuOpen = true
                    },
                    onDismiss = {
                        isDegreeDropDownMenuOpen = false
                    },
                    modifier = Modifier
                        .weight(1f),
                )

                // Degree Title
                DegreeTitleDropDownMenu(
                    degreeTitle = degreeTitle,
                    degreeTitleList = degreeTitleList,
                    expanded = isDegreeTitleDropDownMenuOpen,
                    onExpandedChange = {
                        isDegreeTitleDropDownMenuOpen = !isDegreeTitleDropDownMenuOpen
                    },
                    onItemCLick = {
                        degreeTitle = it
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    onDismiss = {
                        isDegreeTitleDropDownMenuOpen = false
                    },
                    modifier = Modifier
                        .weight(1f),
                )

            }
            Spacer(modifier = Modifier.height(10.dp))
            // Semester
            Text(
                text = "Semester",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            TextFieldComponent(
                value = semester,
                onValueChange = {
                    semester = it
                },
                label = {
                    Text(text = "Semester")
                },
                colors = OutlinedTextFieldColors(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CastForEducation,
                        contentDescription = "Icon for Semester"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true,
                isError = if (!semester.isDigitsOnly() || semester.length > 1) true else false,
                supportingText = {
                    Text(
                        text = if (!semester.isDigitsOnly())
                            "Please Enter Valid Semester"
                        else if (semester.length > 1)
                            "Please Enter only 1 digit"
                        else "",
                        color = Red
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                        top = 10.dp
                    ),
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Phone Number
            Text(
                text = "Phone Number",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code
                    TextFieldComponent(
                        value = countryCode,
                        onValueChange = {
                            countryCode = if (countryCode.length < 7) {
                                it
                            } else {
                                if (it.length < countryCode.length) {
                                    it
                                } else {
                                    countryCode
                                }
                            }
                        },
                        placeholder = {
                            Text(text = "Code")
                        },
                        colors = OutlinedTextFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Phone
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Right)
                            }
                        ),
                        singleLine = true,
                        isError = if (!countryCode.startsWith("+") || countryCode.length < 2) true else false,
                        modifier = Modifier
                            .width(100.dp)
                            .height(53.dp)
                            .padding(start = 20.dp, end = 5.dp)
                    )

                    // Number
                    TextFieldComponent(
                        value = number,
                        onValueChange = {
                            number = if (number.length < 15) {
                                it
                            } else {
                                if (it.length < number.length) {
                                    it
                                } else {
                                    number
                                }
                            }
                        },
                        label = {
                            Text(text = "Phone Number")
                        },
                        colors = OutlinedTextFieldColors(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Icon for Phone Number"
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                                isCityDropDownMenuOpen = true
                            }
                        ),
                        singleLine = true,
                        isError = if (number.isNotEmpty() && number.length < 10 || !number.isDigitsOnly()) true else false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 20.dp)
                    )

                }
                Text(
                    text = if (!countryCode.startsWith("+"))
                        "Code must be start with '+' like '+92' "
                    else if (countryCode.length < 2)
                        "Code must be at least 2 character long"
                    else if (number.isNotEmpty() && number.length < 10) "Phone Number is Too Short"
                    else if (!number.isDigitsOnly()) "Please Enter only Digits in Phone Number"
                    else "",
                    color = Red,
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        .align(alignment = Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "City",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            // City
            DegreeTitleDropDownMenu(
                degreeTitle = city,
                degreeTitleList = cityList,
                expanded = isCityDropDownMenuOpen,
                onExpandedChange = {
                    isCityDropDownMenuOpen = !isCityDropDownMenuOpen
                },
                onItemCLick = {
                    city = it
                    focusManager.moveFocus(FocusDirection.Down)
                },
                onDismiss = {
                    isCityDropDownMenuOpen = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Address
            Text(
                text = "Address",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            TextFieldComponent(
                value = address,
                onValueChange = {
                    address = it
                },
                label = {
                    Text(text = "Address")
                },
                colors = OutlinedTextFieldColors(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.AddLocation,
                        contentDescription = "Icon for address"
                    )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                        top = 10.dp
                    ),
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = {
                navHostController.navigate(
                    AuthDestination.AUTHENTICATION_SCREEN(
                        comeFor = AuthenticationStatus.forAuthentication,
                        registrationData = RegistrationData(
                            name = "",
                            degree = "",
                            degreeTitle = "",
                            semester = "",
                            countryCode = "",
                            number = "",
                            phoneNumber = "",
                            city = city,
                            address = address
                        )
                    )
                ) {
                    restoreState = true
                }
            }) {
                Text(
                    text = "Already have Account?  Verify",
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = if (isSystemInDarkTheme()) White else Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

    }

}

@Composable
fun DegreeDropDownMenu(
    degree: String,
    degreeList: List<String>,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onItemCLick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange.invoke()
        },
        modifier = modifier
    ) {
        TextFieldComponent(
            value = degree,
            onValueChange = {},
            placeholder = {
                Text(text = "Degree")
            },
            colors = OutlinedTextFieldColors(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxSize()
                .menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onDismiss.invoke()
            },
            modifier = Modifier
                .background(color = if (isSystemInDarkTheme()) DarkGray else White)
        ) {
            degreeList.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = {
                        Text(text = text)
                    },
                    onClick = {
                        onItemCLick.invoke(degreeList[index])
                        onDismiss.invoke()
                    }
                )
            }
        }
    }
}

@Composable
fun DegreeTitleDropDownMenu(
    degreeTitle: String,
    degreeTitleList: List<String>,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onItemCLick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange.invoke()
        },
        modifier = modifier
    ) {
        TextFieldComponent(
            value = degreeTitle,
            onValueChange = {},
            placeholder = {
                Text(text = "Title")
            },
            colors = OutlinedTextFieldColors(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxSize()
                .menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onDismiss.invoke()
            },
            modifier = Modifier
                .background(color = if (isSystemInDarkTheme()) DarkGray else White)
        ) {
            degreeTitleList.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = {
                        Text(text = text)
                    },
                    onClick = {
                        onItemCLick.invoke(degreeTitleList[index])
                        onDismiss.invoke()
                    }
                )
            }
        }
    }
}

@Composable
fun CityDropDownMenu(
    city: String,
    cityList: List<String>,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onItemCLick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange.invoke()
        },
        modifier = modifier
    ) {
        TextFieldComponent(
            value = city,
            onValueChange = {},
            placeholder = {
                Text(text = "City")
            },
            colors = OutlinedTextFieldColors(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxSize()
                .menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onDismiss.invoke()
            },
            modifier = Modifier
                .background(color = if (isSystemInDarkTheme()) DarkGray else White)
        ) {
            cityList.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = {
                        Text(text = text)
                    },
                    onClick = {
                        onItemCLick.invoke(cityList[index])
                        onDismiss.invoke()
                    }
                )
            }
        }
    }
}