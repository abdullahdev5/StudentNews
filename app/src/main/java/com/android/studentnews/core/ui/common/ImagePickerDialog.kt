package com.android.studentnews.core.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.White

@Composable
fun ImagePickerDialog(
    modifier: Modifier = Modifier,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) DarkColor else White,
                ),
                modifier = modifier
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                ) {
                    // Camera option
                    ExtendedFloatingActionButton(
                        text = {
                            Text(text = "Camera")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Icon for Camera"
                            )
                        },
                        onClick = onCameraClick,
                        containerColor = Color.Transparent,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Gray,
                                shape = FloatingActionButtonDefaults.extendedFabShape
                            )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Gallery option
                    ExtendedFloatingActionButton(
                        text = {
                            Text(text = "Gallery")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PhotoAlbum,
                                contentDescription = "Icon for Gallery"
                            )
                        },
                        onClick = onGalleryClick,
                        containerColor = Color.Transparent,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Gray,
                                shape = FloatingActionButtonDefaults.extendedFabShape
                            )
                    )

                }
            }
        }
    )
}