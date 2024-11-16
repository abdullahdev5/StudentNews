package com.android.studentnewsadmin.core.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ImagePickerDialog(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Camera
                    OutlinedButton(
                        onClick = {
                            onCameraClick.invoke()
                            onDismiss.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                    ) {
                        Text(text = "Camera")
                    }
                    // Gallery
                    OutlinedButton(
                        onClick = {
                            onGalleryClick.invoke()
                            onDismiss.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                    ) {
                        Text(text = "Gallery")
                    }
                }
            }
        }
    )
}