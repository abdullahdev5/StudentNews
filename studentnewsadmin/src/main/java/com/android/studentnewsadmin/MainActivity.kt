package com.android.studentnewsadmin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.android.studentnewsadmin.main.navigation.NavigationGraph
import com.android.studentnewsadmin.main.news.ui.viewmodel.NewsViewModel
import com.android.studentnewsadmin.ui.theme.StudentNewsTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    @UnstableApi
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentNewsTheme {

                val lifecycleOwner = LocalLifecycleOwner.current


                Scaffold(modifier = Modifier.fillMaxSize()) {

                    val navHostController = rememberNavController()

                    NavigationGraph(
                        navHostController = navHostController,
                    )
                }
            }
        }
    }
}