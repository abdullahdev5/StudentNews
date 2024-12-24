package com.android.studentnews.main.referral_bonus.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.referral_bonus.domain.model.OffersModel
import com.android.studentnews.main.referral_bonus.domain.repository.ReferralBonusRepository
import com.android.studentnews.main.referral_bonus.domain.resource.ReferralBonusState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReferralBonusViewModel(
    private val referralBonusRepository: ReferralBonusRepository
): ViewModel() {


    private val _offersList = MutableStateFlow<List<OffersModel>>(emptyList())
    val offersList = _offersList.asStateFlow()

    var offersListStatus by mutableStateOf("")
        private set



    fun getOffers() {
        viewModelScope.launch {
            referralBonusRepository
                .getOffers()
                .collectLatest { result ->
                    when (result) {
                        is ReferralBonusState.Failed -> {
                            offersListStatus = Status.FAILED
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


}