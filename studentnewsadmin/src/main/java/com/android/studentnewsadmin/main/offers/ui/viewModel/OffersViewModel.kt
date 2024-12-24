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
import com.android.studentnewsadmin.main.offers.domain.repository.OffersRepository
import com.android.studentnewsadmin.main.offers.domain.resource.OffersState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OffersViewModel(
    private val offersRepository: OffersRepository,
) : ViewModel() {


    var progress by mutableFloatStateOf(0f)
    var uploadingStatus by mutableStateOf("")



    fun onOfferUpload(
        offerName: String,
        offerDescription: String,
        offerImageUri: Uri,
        pointsWhenAbleToCollect: Double,
        context: Context,
    ) {
        viewModelScope.launch {
            offersRepository.onOfferUpload(
                offerName = offerName,
                offerDescription = offerDescription,
                offerImageUri = offerImageUri,
                pointsWhenAbleToCollect = pointsWhenAbleToCollect
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
                            progress = result.progress
                        }

                        is OffersState.Success -> {
                            uploadingStatus = Status.Success
                            Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

}