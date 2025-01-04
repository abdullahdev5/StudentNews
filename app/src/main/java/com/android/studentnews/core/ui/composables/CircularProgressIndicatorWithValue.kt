package com.android.studentnews.core.ui.composables

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun CircularIndicatorWithProgress(
    progress: Float,
) {
    Dialog(
        onDismissRequest = {},
        content = {
            CircularProgressIndicator(
                progress = progress
            )
        }
    )
}