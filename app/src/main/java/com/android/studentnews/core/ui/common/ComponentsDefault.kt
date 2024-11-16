@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.core.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.Red
import com.android.studentnews.ui.theme.White

@Composable
fun OutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.Unspecified,
    unfocusedContainerColor = if (isSystemInDarkTheme()) DarkGray else LightGray,
    focusedTextColor = if (isSystemInDarkTheme()) White else Black,
    unfocusedTextColor = if (isSystemInDarkTheme()) White else Black,
    cursorColor = if (isSystemInDarkTheme()) White else Black,
    focusedLabelColor = if (isSystemInDarkTheme()) White else Black,
    unfocusedLabelColor = if (isSystemInDarkTheme()) White else Black,
    focusedPlaceholderColor = if (isSystemInDarkTheme()) White else Black,
    unfocusedPlaceholderColor = if (isSystemInDarkTheme()) White else Black,
    focusedBorderColor = Green,
    errorTrailingIconColor = Red,
    errorTextColor = Red,
    errorPlaceholderColor = Red
)

@Composable
fun ButtonColors(
    containerColor: Color = Green,
    contentColor: Color = White
) = ButtonDefaults.buttonColors(
    containerColor = containerColor,
    contentColor = contentColor
)