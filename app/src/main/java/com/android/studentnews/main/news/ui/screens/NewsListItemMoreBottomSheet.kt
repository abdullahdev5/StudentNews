package com.android.studentnews.main.news.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListItemMoreBottomSheet(
    isNewsSaved: Boolean?,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
//        sheetState = rememberModalBottomSheetState(
//            skipPartiallyExpanded = true
//        ),
        dragHandle = {},
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
    ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
            ) {
                if (isNewsSaved != null) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSave()
                                onDismiss()
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                        ) {

                            Icon(
                                imageVector = if (isNewsSaved)
                                    Icons.Outlined.BookmarkRemove
                                else Icons.Default.BookmarkBorder,
                                contentDescription = "Icon for Save or UnSave the News"
                            )

                            Text(text = if (isNewsSaved) "UnSave" else "Save")

                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onShare()
                                onDismiss()
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Icon for Share the News"
                            )

                            Text(text = "Share")

                        }
                    }

                } else {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

    }

}