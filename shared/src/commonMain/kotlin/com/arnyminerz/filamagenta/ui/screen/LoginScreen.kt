package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.ui.logic.BackHandler
import com.arnyminerz.filamagenta.ui.reusable.form.FormField
import com.arnyminerz.filamagenta.utils.isValidDni
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Job

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    isError: Boolean,
    onDismissErrorRequested: () -> Unit,
    onLoginRequested: (username: String, password: String) -> Job,
    onBackRequested: () -> Unit
) {
    var isLoggingIn by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf<String?>(null) }

    val isDniValid = username?.isValidDni == true

    fun login() {
        if (isDniValid && !isError) {
            isLoggingIn = true
            onLoginRequested(username ?: "", password ?: "").invokeOnCompletion {
                isLoggingIn = false
            }
        }
    }

    BackHandler(onBack = onBackRequested)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(MR.strings.login_title)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackRequested
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = stringResource(MR.strings.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(MR.images.icon),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(128.dp),
                contentScale = ContentScale.Inside
            )
            Text(
                text = stringResource(MR.strings.login_message),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 12.dp, bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            val passwordFocusRequester = remember { FocusRequester() }

            FormField(
                value = username,
                onValueChange = { username = it; onDismissErrorRequested() },
                label = stringResource(MR.strings.login_username),
                modifier = Modifier.padding(horizontal = 16.dp),
                enabled = !isLoggingIn,
                error = stringResource(MR.strings.login_error_dni).takeUnless { isDniValid },
                allCaps = true,
                autofillType = AutofillType.Username,
                nextFocusRequester = passwordFocusRequester
            )
            FormField(
                value = password,
                onValueChange = { password = it; onDismissErrorRequested() },
                label = stringResource(MR.strings.login_password),
                modifier = Modifier.padding(horizontal = 16.dp),
                enabled = !isLoggingIn,
                error = stringResource(MR.strings.login_error).takeIf { isError },
                isPassword = true,
                autofillType = AutofillType.Password,
                thisFocusRequester = passwordFocusRequester,
                onGo = ::login
            )

            OutlinedButton(
                onClick = ::login,
                enabled = isDniValid && !isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 48.dp)
            ) {
                Text(stringResource(MR.strings.login_action))
            }
        }
    }
}
