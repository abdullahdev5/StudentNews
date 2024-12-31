package com.android.studentnews.main.news.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnews.main.news.ui.viewModel.NewsDetailViewModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.White
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListItemMoreBottomSheet(
    isNewsSaved: Boolean?,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) DarkColor else White,
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
                .border(
                    width = 1.dp,
                    color = Gray,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 100.dp)
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
                    CircularProgressIndicator()
                }
            }

        }

    }

}