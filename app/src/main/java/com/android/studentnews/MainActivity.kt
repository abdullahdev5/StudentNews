package com.android.studentnews

import android.annotation.SuppressLint
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.android.studentnews.auth.ui.viewModel.AuthViewModel
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.ui.ObserverAsEvents
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.events.EVENT_ID
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.navigation.NavigationGraph
import com.android.studentnews.ui.theme.StudentNewsTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {


    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            StudentNewsTheme {
                val navHostController = rememberNavController()
                val authViewModel = getViewModel<AuthViewModel>()

                val snackBarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                ObserverAsEvents(
                    flow = SnackBarController.events,
                    key1 = snackBarHostState,
                    onEvent = { event ->
                        scope.launch {
                            snackBarHostState.currentSnackbarData?.dismiss()

                            val result = snackBarHostState.showSnackbar(
                                message = event.message,
                                actionLabel = event.action?.label,
                                duration = event.duration,
                            )

                            if (result == SnackbarResult.ActionPerformed) {
                                event.action?.action?.invoke()
                            }
                        }
                    }
                )

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackBarHostState)
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                ) {

                    NavigationGraph(
                        navHostController = navHostController,
                        authViewModel = authViewModel,
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}