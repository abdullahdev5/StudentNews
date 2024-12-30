package com.android.studentnewsadmin.main.offers.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.studentnewsadmin.core.domain.common.formatDateToString
import com.android.studentnewsadmin.core.domain.constants.FontSize
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.main.navigation.Destination
import com.android.studentnewsadmin.main.offers.domain.constant.OfferTypes
import com.android.studentnewsadmin.main.offers.domain.model.OffersModel
import com.android.studentnewsadmin.main.offers.ui.viewModel.OffersViewModel

@Composable
fun OffersScreen(
    navHostController: NavHostController,
    offersViewModel: OffersViewModel,
) {

    val context = LocalContext.current


    val offerList by offersViewModel.offersList.collectAsStateWithLifecycle()

    Surface {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            items(offerList.size) { index ->
                val item = offerList[index]

                OffersListItem(
                    item = item,
                    context = context,
                    onEdit = { thisOfferId ->
                        navHostController.navigate(Destination.EDIT_OFFER_SCREEN(
                            offerId = thisOfferId
                        ))
                    }
                )
            }

            if (offerList.isEmpty()
                && offersViewModel.offersListStatus != Status.Loading
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(text = "No Offers Yet!")
                    }
                }
            }

            if (offersViewModel.offersListStatus == Status.Loading) {
                item {
                    CircularProgressIndicator()
                }
            }

        }

    }

}

@Composable
fun OffersListItem(
    item: OffersModel,
    context: Context,
    onEdit: (String) -> Unit,
) {

    val annotatedString = buildAnnotatedString {
        withStyle(
            style = ParagraphStyle(
                lineHeight = 20.sp
            )
        ) {
            // Offer Name
            withStyle(
                style = SpanStyle(
                    fontSize = FontSize.LARGE.sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                appendLine(item.offerName)
            }
            // Offer Description
            withStyle(
                style = SpanStyle(
                    fontSize = FontSize.MEDIUM.sp,
                    color = Color.Gray
                )
            ) {
                appendLine(item.offerDescription)
            }
            // Offer Description
            withStyle(
                style = SpanStyle(
                    fontSize = FontSize.SMALL.sp,
                )
            ) {
                // Offer Type
                appendLine("Offer Type is " + item.offerType)
                // created at
                appendLine("created at " + formatDateToString(item.createdAt.toDate().time))
                item.updatedAt?.let { updatedTimestamp ->
                    appendLine("updated at " + formatDateToString(updatedTimestamp.toDate().time))
                }
                if (item.offerType == OfferTypes.DISCOUNT) {
                    appendLine("discount " + item.discountAmount)
                } else if (item.offerType == OfferTypes.EXPIRED) {
                    item.offerExpiryDate?.let { offerExpiryTimestamp ->
                        appendLine("Expired at " + formatDateToString(offerExpiryTimestamp.toDate().time))
                    }
                }
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Box {
                val imageRequest = ImageRequest.Builder(context)
                    .data(item.offerImageUrl)
                    .crossfade(true)
                    .build()

                AsyncImage(
                    model = imageRequest,
                    contentDescription = "Offer Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                // Edit Icon Button
                IconButton(
                    onClick = {
                        onEdit(item.offerId)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(all = 5.dp)
                        .clip(CircleShape)
                        .background(color = Color.Black.copy(0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Icon for Edit the Offer",
                        tint = Color.White
                    )
                }
            }
            // Annotated String
            Text(
                text = annotatedString,
                modifier = Modifier
                    .padding(all = 5.dp)
            )
        }
    }
}