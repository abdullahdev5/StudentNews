package com.android.studentnews.main.referral_bonus.ui.viewModel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.referral_bonus.domain.model.EarnedPointsModel
import com.android.studentnews.main.referral_bonus.domain.model.OffersModel
import com.android.studentnews.main.referral_bonus.domain.repository.ReferralBonusRepository
import com.android.studentnews.main.referral_bonus.domain.resource.ReferralBonusState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReferralBonusViewModel(
    private val referralBonusRepository: ReferralBonusRepository,
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _offersList = MutableStateFlow<List<OffersModel>>(emptyList())
    val offersList = _offersList.asStateFlow()

    var offersListStatus by mutableStateOf("")
        private set


    init {
        getCurrentUserWithAwait()
    }



    fun getOffers() {
        viewModelScope.launch {
            referralBonusRepository
                .getOffers()
                .collectLatest { result ->
                    when (result) {
                        is ReferralBonusState.Failed -> {
                            offersListStatus = Status.FAILED
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: ""
                                    )
                                )
                        }

                        ReferralBonusState.Loading -> {
                            offersListStatus = Status.Loading
                        }

                        is ReferralBonusState.Success -> {
                            _offersList.value = result.data
                            offersListStatus = Status.SUCCESS
                        }
                    }
                }
        }
    }

    fun onReferralPointsCollect(
        earnedPointsModel: EarnedPointsModel,
    ) = referralBonusRepository.onReferralPointsCollect(earnedPointsModel)

    fun onOfferCollect(
        offerId: String,
    ) {
        _offersList.update {
            it.filter {
                offerId != it.offerId
            }
        }
        viewModelScope.launch {
            referralBonusRepository
                .onOfferCollect(offerId = offerId)
                .collectLatest { result ->
                    when (result) {
                        is ReferralBonusState.Failed -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: ""
                                    )
                                )
                        }
                        is ReferralBonusState.Success -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.data
                                    )
                                )
                        }

                        else -> {}
                    }
                }
        }
    }

    fun getCurrentUserWithAwait() {
        viewModelScope.launch {
            referralBonusRepository
                .getCurrentUserWithAwait()
                .collectLatest { result ->
                    when (result) {
                        is ReferralBonusState.Success -> {
                            _currentUser.value = result.data
                            println("Current User: In ViewModel: ${_currentUser.value}")
                        }
                        else -> {}
                    }
                }
        }
    }


}