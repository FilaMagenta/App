package com.arnyminerz.filamagenta.ui.reusable.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.ui.modifier.autofill
import dev.icerock.moko.resources.compose.stringResource

@Composable
@ExperimentalComposeUiApi
fun FormField(
    value: String?,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    allCaps: Boolean = false,
    isPassword: Boolean = false,
    error: String? = null,
    autofillType: AutofillType? = null,
    thisFocusRequester: FocusRequester? = null,
    nextFocusRequester: FocusRequester? = null,
    onGo: (() -> Unit)? = null
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    fun sendValueChange(value: String) {
        if (allCaps)
            onValueChange(value.uppercase())
        else
            onValueChange(value)
    }

    var displayAsPassword by remember { mutableStateOf(isPassword) }

    OutlinedTextField(
        value = value ?: "",
        onValueChange = ::sendValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .let { mod ->
                if (thisFocusRequester != null)
                    mod.focusRequester(thisFocusRequester)
                else
                    mod
            }
            .let { mod ->
                if (autofillType != null)
                    mod.autofill(
                        listOf(autofillType),
                        ::sendValueChange
                    )
                else
                    mod
            }
            .then(modifier),
        label = { Text(label) },
        supportingText = if (value != null && error != null) {
            { Text(error) }
        } else null,
        isError = value != null && error != null,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { displayAsPassword = !displayAsPassword }) {
                    Icon(
                        imageVector = if (displayAsPassword)
                            Icons.Outlined.VisibilityOff
                        else
                            Icons.Outlined.Visibility,
                        contentDescription = stringResource(
                            if (displayAsPassword)
                                MR.strings.password_hide
                            else
                                MR.strings.password_show
                        )
                    )
                }
            }
        } else null,
        singleLine = true,
        maxLines = 1,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            capitalization = if (allCaps) KeyboardCapitalization.Characters else KeyboardCapitalization.None,
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = if (nextFocusRequester != null)
                ImeAction.Next
            else if (onGo != null)
                ImeAction.Go
            else
                ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onNext = { nextFocusRequester?.requestFocus() },
            onDone = { softwareKeyboardController?.hide() },
            onGo = { onGo?.invoke() }
        ),
        visualTransformation = if (displayAsPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}
