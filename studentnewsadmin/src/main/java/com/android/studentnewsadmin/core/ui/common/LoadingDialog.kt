package com.android.studentnewsadmin.core.ui.common

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog() {
    Dialog(
        onDismissRequest = {},
        content = {
            CircularProgressIndicator()
        }
    )
}