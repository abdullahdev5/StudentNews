@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.studentnews.ui.theme.Gray

@Composable
fun SettingsScreen(
    navHostController: NavHostController
) {

    val scrollState = rememberScrollState()


    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(text = "Settings")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navHostController.navigateUp()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Icon for Navigate Back"
                            )
                        }
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Icon for Settings"
                        )
                    }
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {

                // Saved
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navHostController
                                .navigate(SettingsDestination.SAVED_SCREEN)
                        }
                        .background(color = Color.Transparent)
                ) {
                    Column {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Icon for Saved"
                            )

                            Text(
                                text = "Saved"
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Icon for Go Forward for More",
                                modifier = Modifier
                                    .width(15.dp)
                                    .height(15.dp)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }

                // Registrations
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navHostController
                                .navigate(SettingsDestination.REGISTRATIONS_SCREEN)
                        }
                        .background(color = Color.Transparent)
                ) {
                    Column {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Book,
                                contentDescription = "Icon for Registrations"
                            )

                            Text(
                                text = "Registrations"
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Icon for Go Forward for More",
                                modifier = Modifier
                                    .width(15.dp)
                                    .height(15.dp)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }

                // Liked
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navHostController
                                .navigate(SettingsDestination.LIKED_SCREEN)
                        }
                        .background(color = Color.Transparent)
                ) {
                    Column {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Icon for Liked"
                            )

                            Text(
                                text = "Liked"
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Icon for Go Forward for More",
                                modifier = Modifier
                                    .width(15.dp)
                                    .height(15.dp)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }

        }

    }

}