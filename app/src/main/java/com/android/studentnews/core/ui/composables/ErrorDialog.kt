package com.android.studentnews.core.ui.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.Red
import com.android.studentnews.ui.theme.White

@Composable
fun ErrorDialog(
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss.invoke()
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = "Icon for Error",
                tint = Red
            )
        },
        title = title,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        containerColor = if (isSystemInDarkTheme()) DarkColor else White,
    )
}