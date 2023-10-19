package com.arnyminerz.filamagenta.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.account.AccountData
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.qrcode
import com.arnyminerz.filamagenta.image.QRCodeGenerator
import com.arnyminerz.filamagenta.ui.modifier.placeholder.placeholder
import com.arnyminerz.filamagenta.ui.native.toImageBitmap
import com.arnyminerz.filamagenta.ui.reusable.ImageLoader
import com.arnyminerz.filamagenta.ui.reusable.LoadingBox
import com.arnyminerz.filamagenta.ui.reusable.form.FormField
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@OptIn(ExperimentalEncodingApi::class, ExperimentalUnsignedTypes::class)
@Composable
fun ProfilePage(viewModel: MainViewModel) {
    val viewModelAccount by viewModel.account.collectAsState()

    val loadingAccountData by viewModel.isLoadingAccount.collectAsState(false)
    val accountData by viewModel.accountData.collectAsState()

    viewModelAccount?.let { account ->
        val email = accounts.getEmail(account)

        val qr = account.qrcode()
        var qrCode by remember { mutableStateOf<ByteArray?>(null) }

        LaunchedEffect(account) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = qr.encrypt()
                qrCode = Cache.imageCache(data) {
                    QRCodeGenerator.generate(data)
                }
            }
        }

        DisposableEffect(account) {
            val coroutine = if (accountData == null && !loadingAccountData) {
                viewModel.refreshAccount()
            } else {
                null
            }
            coroutine?.invokeOnCompletion {
                if (it != null) Napier.e("Account refresh failed.", throwable = it)
            }

            onDispose { coroutine?.cancel() }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageLoader(
                image = qrCode?.toImageBitmap(),
                contentDescription = account.name,
                modifier = Modifier
                    .size(192.dp)
                    .padding(top = 32.dp)
            )
            Text(
                text = accountData?.fullName ?: "0123456789",
                modifier = Modifier
                    .placeholder(
                        visible = accountData == null
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    lineHeight = MaterialTheme.typography.titleMedium.fontSize
                ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            AnimatedContent(
                targetState = accountData,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) { data ->
                if (data != null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        PersonalData(email, data)
                    }
                }
            }
        }
    } ?: LoadingBox()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PersonalData(email: String, data: AccountData) {
    Text(
        text = stringResource(MR.strings.profile_title_personal_data),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
    FormField(
        value = data.email?.lowercase(),
        onValueChange = {},
        label = stringResource(MR.strings.profile_email),
        readOnly = true,
        supportingText = stringResource(MR.strings.profile_warning_email_match)
            .takeUnless { email.equals(data.email, ignoreCase = true) },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    FormField(
        value = data.address,
        onValueChange = {},
        label = stringResource(MR.strings.profile_address),
        readOnly = true,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Row {
        FormField(
            value = data.city.lowercase().capitalize(Locale.current),
            onValueChange = {},
            label = stringResource(MR.strings.profile_city),
            readOnly = true,
            modifier = Modifier.weight(2f).padding(start = 16.dp, end = 8.dp)
        )
        FormField(
            value = data.postalCode.toString(),
            onValueChange = {},
            label = stringResource(MR.strings.profile_postal_code),
            readOnly = true,
            modifier = Modifier.weight(1f).padding(end = 16.dp)
        )
    }
    FormField(
        value = data.birthday.toString(),
        onValueChange = {},
        label = stringResource(MR.strings.profile_birthday),
        readOnly = true,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
