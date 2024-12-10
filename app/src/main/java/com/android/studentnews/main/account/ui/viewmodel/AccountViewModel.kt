package com.android.studentnews.main.account.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.account.domain.repository.AccountRepository
import com.android.studentnews.main.account.domain.resource.AccountState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AccountViewModel(
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    // Current User
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    var saveStatus by mutableStateOf("")

    init {
        getCurrentUser()
    }


    fun getCurrentUser() {
        viewModelScope.launch {
            authRepository
                .getCurrentUser()
                .collectLatest { result ->
                    when (result) {
                        is AccountState.Success -> {
                            _currentUser.value = result.data
                        }

                        else -> {}
                    }
                }
        }
    }

    fun onSave(
        username: String,
        imageBitmap: Bitmap?
    ) {
        viewModelScope.launch {
            accountRepository
                .onSave(username, imageBitmap)
                .collect { result ->
                    when (result) {
                        is AccountState.Failure -> {
                            saveStatus = Status.FAILED
                            SnackBarController.sendEvent(
                                SnackBarEvents(
                                    message = result.error.localizedMessage
                                        ?: "",
                                    duration = SnackbarDuration.Long
                                )
                            )
                        }
                        AccountState.Loading -> {
                            saveStatus = Status.Loading
                        }
                        is AccountState.Success -> {
                            saveStatus = Status.SUCCESS
                            SnackBarController.sendEvent(
                                SnackBarEvents(
                                    message = result.data,
                                    duration = SnackbarDuration.Long
                                )
                            )
                        }
                        else -> {}
                    }
                }
        }
    }

}