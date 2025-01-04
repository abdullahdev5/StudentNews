package com.android.studentnews.core.ui.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.White

@Composable
fun CustomAlertDialog(
    title: () -> String,
    confirmText: () -> String = { "Confirm" },
    dismissText: () -> String = { "Discard" },
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cardShape: Shape = RectangleShape,
    buttonColors: ButtonColors = ButtonColors(
        containerColor = Color.Transparent,
        contentColor = Green
    )
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) DarkGray else White
            ),
            shape = cardShape,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
            ) {
                Text(text = title())

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = buttonColors
                    ) {
                        Text(text = dismissText())
                    }

                    TextButton(
                        onClick = onConfirm,
                        colors = buttonColors
                    ) {
                        Text(text = confirmText())
                    }
                }
            }
        }
    }
}