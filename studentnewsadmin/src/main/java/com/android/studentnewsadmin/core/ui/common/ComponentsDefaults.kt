package com.android.studentnewsadmin.core.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.android.studentnewsadmin.ui.theme.Green

@Composable
fun OutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.Unspecified,
    unfocusedContainerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
    focusedTextColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
    unfocusedTextColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
    cursorColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
    focusedLabelColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
    unfocusedLabelColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
    focusedPlaceholderColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
    unfocusedPlaceholderColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
    focusedBorderColor = Green,
    errorTrailingIconColor = Color.Red,
    errorTextColor = Color.Red,
    errorPlaceholderColor = Color.Red
)