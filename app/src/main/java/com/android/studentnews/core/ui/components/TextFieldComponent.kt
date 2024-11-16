package com.android.studentnews.core.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TextFieldComponent(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    singleLine: Boolean = false,
    readOnly: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true,
    supportingText: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    shape: Shape = TextFieldDefaults.shape,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = {
            onValueChange.invoke(it)
        },
        label = label,
        placeholder = placeholder,
        colors = colors,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        isError = isError,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        supportingText = supportingText,
        visualTransformation = visualTransformation,
        prefix = prefix,
        suffix = suffix,
        enabled = enabled,
        shape = shape,
        modifier = modifier
    )
}