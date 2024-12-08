package com.android.studentnews.main.account.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.account.domain.repository.AccountRepository
import com.android.studentnews.main.account.domain.resource.AccountState
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AccountViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    // Current User
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    var userImageSavedStatus = mutableStateOf("")


    init {
        getCurrentUser()
    }


    fun getCurrentUser() {
        viewModelScope.launch {
            accountRepository
                .getCurrentUser()
                .collect { result ->
                    when (result) {
                        is AccountState.Success -> {
                            _currentUser.value = result.data
                        }

                        else -> {}
                    }
                }
        }
    }

    fun onUserImageSave(imageBitmap: Bitmap) {
        viewModelScope.launch {
            accountRepository
                .onUserImageSave(imageBitmap)
                .collect { result ->
                    when (result) {
                        is AccountState.Failure -> {
                            userImageSavedStatus.value = Status.FAILED
                            SnackBarController.sendEvent(
                                SnackBarEvents(
                                    message = result.error.localizedMessage
                                        ?: "",
                                    duration = SnackbarDuration.Long
                                )
                            )
                        }
                        AccountState.Loading -> {
                            userImageSavedStatus.value = Status.Loading
                        }
                        is AccountState.Progress -> {}
                        is AccountState.Success -> {
                            userImageSavedStatus.value = Status.SUCCESS
                            SnackBarController.sendEvent(
                                SnackBarEvents(
                                    message = result.data,
                                    duration = SnackbarDuration.Long
                                )
                            )
                        }
                    }
                }
        }
    }


    fun onUsernameSave(username: String) = accountRepository.onUsernameSave(username)


}