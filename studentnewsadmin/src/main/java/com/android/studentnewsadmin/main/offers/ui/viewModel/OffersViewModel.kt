package com.android.studentnewsadmin.main.offers.ui.viewModel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.main.offers.domain.model.OffersModel
import com.android.studentnewsadmin.main.offers.domain.repository.OffersRepository
import com.android.studentnewsadmin.main.offers.domain.resource.OffersState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OffersViewModel(
    private val offersRepository: OffersRepository,
) : ViewModel() {


    private val _offersList = MutableStateFlow<List<OffersModel>>(emptyList())
    val offersList = _offersList.asStateFlow()

    private val _offerById = MutableStateFlow<OffersModel?>(null)
    val offerById = _offerById.asStateFlow()

    var offersListStatus by mutableStateOf("")
        private set

    var offerByIdStatus by mutableStateOf("")
        private set

    var uploadingProgress by mutableFloatStateOf(0f)
    var uploadingStatus by mutableStateOf("")

    var updatingProgress by mutableFloatStateOf(0f)
    var updatingStatus by mutableStateOf("")



    init {
        getOffersList()
    }


    fun onOfferUpload(
        offerName: String,
        offerImageUri: Uri,
        offerDescription: String,
        pointsRequired: Double,
        offerType: String,
        discountAmount: Double,
        offerTermsAndCondition: String,
        context: Context,
    ) {
        viewModelScope.launch {
            offersRepository.onOfferUpload(
                offerName = offerName,
                offerImageUri = offerImageUri,
                offerDescription = offerDescription,
                pointsRequired = pointsRequired,
                offerType = offerType,
                discountAmount = discountAmount,
                offerTermsAndCondition = offerTermsAndCondition
            )
                .collectLatest { result ->
                    when (result) {
                        is OffersState.Failed -> {
                            uploadingStatus = Status.Failed

                            Toast.makeText(
                                context,
                                result.error.localizedMessage ?: "",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        OffersState.Loading -> {
                            uploadingStatus = Status.Loading
                        }
                        is OffersState.Progress -> {
                            uploadingProgress = result.progress
                        }

                        is OffersState.Success -> {
                            uploadingStatus = Status.Success
                            Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    fun getOffersList() {
        viewModelScope.launch {
            offersRepository
                .getOffersList()
                .collectLatest { result ->
                    when (result) {
                        is OffersState.Failed -> {
                            offersListStatus = Status.Failed
                        }
                        OffersState.Loading -> {
                            offersListStatus = Status.Loading
                        }
                        is OffersState.Success -> {
                            offersListStatus = Status.Success
                            _offersList.value = result.data
                        }
                        else -> {}
                    }
                }
        }
    }

    fun getOfferById(offerId: String) {
        viewModelScope.launch {
            offersRepository
                .getOfferById(offerId)
                .collectLatest { result ->
                    when (result) {
                        is OffersState.Failed -> {
                            offersListStatus = Status.Failed
                        }
                        OffersState.Loading -> {
                            offersListStatus = Status.Loading
                        }
                        is OffersState.Success -> {
                            offersListStatus = Status.Success
                            _offerById.value = result.data
                        }
                        else -> {}
                    }
                }
        }
    }

    fun onOfferUpdate(
        offerId: String,
        offerName: String,
        prevImageUri: Uri,
        offerImageUri: Uri,
        offerDescription: String,
        pointsRequired: Double,
        offerType: String,
        discountAmount: Double,
        offerTermsAndCondition: String,
        context: Context,
    ) {
        viewModelScope.launch {
            offersRepository.onOfferUpdate(
                offerId = offerId,
                offerName = offerName,
                prevImageUri = prevImageUri,
                newOfferImageUri = offerImageUri,
                offerDescription = offerDescription,
                pointsRequired = pointsRequired,
                offerType = offerType,
                discountAmount = discountAmount,
                offerTermsAndCondition = offerTermsAndCondition
            )
                .collectLatest { result ->
                    when (result) {
                        is OffersState.Failed -> {
                            updatingStatus = Status.Failed

                            Toast.makeText(
                                context,
                                result.error.localizedMessage ?: "",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        OffersState.Loading -> {
                            updatingStatus = Status.Loading
                        }
                        is OffersState.Progress -> {
                            updatingProgress = result.progress
                        }

                        is OffersState.Success -> {
                            updatingStatus = Status.Success
                            Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

}