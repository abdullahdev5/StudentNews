package com.android.studentnews.main.events.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.android.studentnews.auth.domain.RegistrationDropDownMenuLists
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.ui.CityDropDownMenu
import com.android.studentnews.auth.ui.DegreeDropDownMenu
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.components.TextFieldComponent
import com.android.studentnews.core.ui.composables.CustomAlertDialog
import com.android.studentnews.core.ui.composables.LoadingDialog
import com.android.studentnews.core.ui.composables.OutlinedTextFieldColors
import com.android.studentnews.main.events.ui.viewModels.EventsDetailViewModel
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventRegistrationBottomSheet(
    eventId: String,
    currentUser: UserModel?,
    context: Context,
    eventsDetailViewModel: EventsDetailViewModel,
    isEventAlreadyRegistered: Boolean,
    onDismiss: () -> Unit,
) {

    val scrollState = rememberScrollState()

    var userName by rememberSaveable(currentUser) {
        mutableStateOf(currentUser?.registrationData?.name ?: "")
    }
    var userDegree by rememberSaveable(currentUser) {
        mutableStateOf(currentUser?.registrationData?.degree ?: "")
    }
    var userCountryCode by rememberSaveable(currentUser) {
        mutableStateOf(currentUser?.registrationData?.countryCode ?: "")
    }
    var userNumber by rememberSaveable(currentUser) {
        mutableStateOf(currentUser?.registrationData?.number ?: "")
    }
    var userCity by rememberSaveable(currentUser) {
        mutableStateOf(currentUser?.registrationData?.city ?: "")
    }
    var userAddress by remember(currentUser) {
        mutableStateOf(currentUser?.registrationData?.address ?: "")
    }


    var isDegreeDropDownMenuOpen by rememberSaveable { mutableStateOf(false) }
    val degreeList = RegistrationDropDownMenuLists.degreeList
    var isCityDropDownMenuOpen by rememberSaveable { mutableStateOf(false) }
    val cityList = RegistrationDropDownMenuLists.cityList

    var isAlertDialogOpenWhenRegister by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(eventsDetailViewModel.eventRegisteringStatus) {
        if (eventsDetailViewModel.eventRegisteringStatus == Status.SUCCESS) {
            onDismiss()
        }
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RectangleShape,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Green
            )
        }
    ) {
        currentUser?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {

                Text(
                    text = "Register Event",
                    style = TextStyle(
                        fontSize = FontSize.LARGE.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(all = 20.dp)
                )


                // Name
                Text(
                    text = "Your Name",
                    color = Gray,
                    modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp)
                )
                TextFieldComponent(
                    value = userName,
                    onValueChange = {
                        userName = it
                    },
                    label = {
                        Text(text = "Your Name")
                    },
                    colors = OutlinedTextFieldColors(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Icon for Person Name"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Degree
                Text(
                    text = "Your Degree",
                    color = Gray,
                    modifier = Modifier
                        .padding(start = 20.dp)
                )
                // Degree
                DegreeDropDownMenu(
                    degree = userDegree,
                    degreeList = degreeList,
                    expanded = isDegreeDropDownMenuOpen,
                    onExpandedChange = {
                        isDegreeDropDownMenuOpen = !isDegreeDropDownMenuOpen
                    },
                    onItemCLick = {
                        userDegree = it
                    },
                    onDismiss = {
                        isDegreeDropDownMenuOpen = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone Number
                Text(
                    text = "Your Phone Number",
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
                            value = userCountryCode,
                            onValueChange = {
                                userCountryCode = if (userCountryCode.length < 7) {
                                    it
                                } else {
                                    if (it.length < userCountryCode.length) {
                                        it
                                    } else {
                                        userCountryCode
                                    }
                                }
                            },
                            placeholder = {
                                Text(text = "Code")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),
                            colors = OutlinedTextFieldColors(),
                            singleLine = true,
                            isError = if (!userCountryCode.startsWith("+") || userCountryCode.length < 2) true else false,
                            modifier = Modifier
                                .width(100.dp)
                                .height(53.dp)
                                .padding(start = 20.dp, end = 5.dp)
                        )

                        // Number
                        TextFieldComponent(
                            value = userNumber,
                            onValueChange = {
                                userNumber = if (userNumber.length < 15) {
                                    it
                                } else {
                                    if (it.length < userNumber.length) {
                                        it
                                    } else {
                                        userNumber
                                    }
                                }
                            },
                            placeholder = {
                                Text(text = "Phone Number")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            colors = OutlinedTextFieldColors(),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Icon for Phone Number"
                                )
                            },
                            singleLine = true,
                            isError = if (userNumber.isNotEmpty() && userNumber.length < 10 || !userNumber.isDigitsOnly()) true else false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, end = 20.dp)
                        )

                    }
                    Text(
                        text = if (!userCountryCode.startsWith("+"))
                            "Code must be start with '+' like '+92' "
                        else if (userCountryCode.length < 2)
                            "Code must be at least 2 character long"
                        else if (userNumber.isNotEmpty() && userNumber.length < 10) "Phone Number is Too Short"
                        else if (!userNumber.isDigitsOnly()) "Please Enter only Digits in Phone Number"
                        else "",
                        color = Red,
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                            .align(alignment = Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your City",
                    color = Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                )
                // City
                CityDropDownMenu(
                    city = userCity,
                    cityList = cityList,
                    expanded = isCityDropDownMenuOpen,
                    onExpandedChange = {
                        isCityDropDownMenuOpen = !isCityDropDownMenuOpen
                    },
                    onItemCLick = {
                        userCity = it
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
                    text = "Your Address",
                    color = Gray,
                    modifier = Modifier
                        .padding(start = 20.dp)
                )
                TextFieldComponent(
                    value = userAddress,
                    onValueChange = {
                        userAddress = it
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
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(all = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel")
                    }

                    // userName, userDegree, userPhoneNumber, userCity, userAddress
                    TextButton(
                        onClick = {
                            if (isEventAlreadyRegistered) {
                                Toast.makeText(
                                    context,
                                    "Something Wrong!\nMay be This Event is Already Registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                isAlertDialogOpenWhenRegister = true
                            }
                        },
                        enabled = userName.isNotEmpty() && userDegree.isNotEmpty() &&
                                userCountryCode.startsWith("+") && userCountryCode.length >= 2
                                && userNumber.length >= 10 && userNumber.isDigitsOnly()
                                && userCity.isNotEmpty() && userAddress.isNotEmpty()
                    ) {
                        Text(text = "Register")
                    }
                }

            }
        } ?: CircularProgressIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (isAlertDialogOpenWhenRegister) {
            CustomAlertDialog(
                title = {
                    "Are You Sure you want to Register this Event?"
                },
                confirmText = { "Register" },
                dismissText = { "cancel" },
                onConfirm = {
                    val registrationData = RegistrationData(
                        name = userName,
                        degree  = userDegree,
                        degreeTitle = "",
                        semester = "",
                        countryCode = userCountryCode,
                        number = userNumber,
                        phoneNumber = userCountryCode+userNumber,
                        city = userCity,
                        address = userAddress
                    )

                    eventsDetailViewModel
                        .onEventRegister(
                            eventId = eventId,
                            registrationData = registrationData
                        )

                    isAlertDialogOpenWhenRegister = false
                },
                onDismiss = {
                    isAlertDialogOpenWhenRegister = false
                }
            )
        }

        if (eventsDetailViewModel.eventRegisteringStatus == Status.Loading) {
            LoadingDialog()
        }

    }
}