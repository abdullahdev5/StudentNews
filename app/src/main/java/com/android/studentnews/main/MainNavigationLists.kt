package com.android.studentnews.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Main Bottom Bar List
sealed class MainBottomNavigationBarList(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    object Home: MainBottomNavigationBarList(
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )
    object Search: MainBottomNavigationBarList(
        label = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
    )
    object Account: MainBottomNavigationBarList(
        label = "Account",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
    )
}


// Main Drawer List
enum class MainNavigationDrawerList(
    val text: String,
    val icon: ImageVector,
) {

    Account(
        text = "Account",
        icon  = Icons.Outlined.Person,
    ),

    Search(
        text = "Search",
        icon  = Icons.Outlined.Search,
    ),

    Saved(
        text = "Saved",
        icon  = Icons.Default.BookmarkBorder,
    ),

    Liked(
        text = "Liked",
        icon  = Icons.Default.FavoriteBorder,
    ),

    Registered_Events(
        text = "Registered Events",
        icon  = Icons.Outlined.Book,
    ),

    Settings(
        text = "Settings",
        icon  = Icons.Outlined.Settings,
    ),

    Log_out(
        text = "Log out",
        icon  = Icons.Outlined.Logout,
    ),

}

enum class MainTabRowList(
    val text: String,
    val index: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    News(
        text = "News",
        index = 0,
        selectedIcon = Icons.Filled.Newspaper,
        unselectedIcon = Icons.Outlined.Newspaper,
    ),

    Events(
        text = "Events",
        index = 1,
        selectedIcon = Icons.Filled.Event,
        unselectedIcon = Icons.Outlined.Event,
    ),

}