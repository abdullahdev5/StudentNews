package com.android.studentnews.main.referral_bonus.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.ui.common.ButtonColors
import com.android.studentnews.main.referral_bonus.domain.model.EarnedPointsModel
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.LightGray

@Composable
fun PointsCollectingListItem(
    item: EarnedPointsModel,
    onCollect: (earnedPointsListItem: EarnedPointsModel) -> Unit,
    onDismiss: () -> Unit,
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) DarkGray.copy(0.2f) else LightGray.copy(0.2f)
        ),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Icon for Dismiss"
                )
            }
            Text(
                text = "Collect and Add ${item.earnedPoints} Points to your Referral Wallet",
                style = TextStyle(
                    fontSize = FontSize.MEDIUM.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 5.dp,
                        bottom = 10.dp,
                    )
            )

            Button(
                onClick = {
                    onCollect(item)
                },
                colors = ButtonColors(),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(all = 10.dp)
            ) {
                Text(text = "collect")
            }
        }
    }

}