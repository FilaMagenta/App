package com.arnyminerz.filamagenta.ui.screen

import QrScannerScreen
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Cache.collectListAsState
import com.arnyminerz.filamagenta.cache.data.cleanName
import com.arnyminerz.filamagenta.cache.data.validateProductQr
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.logic.BackHandler
import com.arnyminerz.filamagenta.ui.screen.model.IntroScreen
import com.arnyminerz.filamagenta.ui.screen.model.IntroScreenPage
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import com.russhwolf.settings.set
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * The main composable that then renders all the app. Has some useful inputs to control what is displayed when.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalEncodingApi::class)
@Composable
fun MainScreen(
    isAddingNewAccount: Boolean = false,
    viewModel: MainViewModel = MainViewModel(),
    onApplicationEndRequested: () -> Unit
) {
    val isRequestingToken by viewModel.isRequestingToken.collectAsState(initial = false)

    var showingLoginWebpage by remember { mutableStateOf(false) }

    val accountsList by accounts.getAccountsLive().collectAsState()

    var addNewAccountRequested by remember { mutableStateOf(isAddingNewAccount) }

    /** If true, the login screen is shown */
    val addingNewAccount = accountsList?.isEmpty() == true || addNewAccountRequested

    val account by viewModel.account.collectAsState()

    LaunchedEffect(Unit) {
        // Initialize logging library
        Napier.base(DebugAntilog())

        // Set the locale to display
        StringDesc.localeType = settings.getStringOrNull(SettingsKeys.LANGUAGE)
            ?.let { StringDesc.LocaleType.Custom(it) }
            ?: StringDesc.LocaleType.System
    }

    val mainPagerState = rememberPagerState { appScreenItems.size }

    LaunchedEffect(accountsList) {
        // todo - eventually an account selector should be added
        if (!accountsList.isNullOrEmpty()) {
            viewModel.account.emit(accountsList?.first())
        }
    }

    if (isRequestingToken || accountsList == null || (!addingNewAccount && account == null)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (showingLoginWebpage) {
        BrowserLoginScreen(
            authorizeUrl = viewModel.getAuthorizeUrl(),
            onDismissRequested = {
                if (accountsList.isNullOrEmpty()) {
                    onApplicationEndRequested()
                } else {
                    showingLoginWebpage = false
                }
            },
            onCodeObtained = { code ->
                showingLoginWebpage = false

                viewModel.requestToken(code)
            }
        )
    } else if (addingNewAccount) {
        LoginScreen(
            onLoginRequested = { showingLoginWebpage = true },
            onBackRequested = {
                if (accountsList.isNullOrEmpty()) {
                    onApplicationEndRequested()
                } else {
                    addNewAccountRequested = false
                }
            }
        )
    } else {
        val isAdmin by viewModel.isAdmin.collectAsState(false)
        val event by viewModel.viewingEvent.collectAsState()
        val editingField by viewModel.editingField.collectAsState()
        val isScanningQr by viewModel.scanningQr.collectAsState(false)
        val tickets by Cache.pendingTicketsToUpload.collectListAsState()

        BackHandler {
            if (isScanningQr) {
                viewModel.stopScanner()
            } else if (event == null) {
                onApplicationEndRequested()
            } else {
                viewModel.stopViewingEvent()
            }
        }

        var shownAdmin by remember { mutableStateOf(settings.getBoolean(SettingsKeys.SYS_SHOWN_ADMIN, false)) }

        if (isAdmin == true && !shownAdmin) {
            IntroScreen(
                pages = listOf(
                    IntroScreenPage(
                        title = { stringResource(MR.strings.intro_admin_welcome_title) },
                        message = { stringResource(MR.strings.intro_admin_welcome_message) },
                        image = { painterResource(MR.images.undraw_welcoming) }
                    ),
                    IntroScreenPage(
                        title = { stringResource(MR.strings.intro_admin_scanner_title) },
                        message = { stringResource(MR.strings.intro_admin_scanner_message) },
                        image = { painterResource(MR.images.undraw_security) }
                    )
                ),
                onFinish = {
                    settings[SettingsKeys.SYS_SHOWN_ADMIN] = true
                    shownAdmin = true
                },
                onCancel = {
                    shownAdmin = false
                }
            )
        } else if (isScanningQr) {
            QrScannerScreen(
                modifier = Modifier.fillMaxSize()
            ) { data ->
                val decoded = Base64.decode(data).decodeToString()
                Napier.i { "Ticket: $decoded" }
                Napier.i { "Is ticket valid? ${validateProductQr(decoded)}" }
                viewModel.stopScanner()
            }
        } else {
            event?.let { ev ->
                editingField?.let { field ->
                    field.editor.Dialog(
                        title = ev.cleanName + " - " + stringResource(field.displayName),
                        onSubmit = { viewModel.performUpdate(ev, field) },
                        onDismissRequest = viewModel::cancelEdit
                    )
                }

                DisposableEffect(ev) {
                    val job = viewModel.fetchOrders(ev.id.toInt())

                    onDispose { job.cancel() }
                }

                EventScreen(ev, viewModel)
            } ?: AppScreen(mainPagerState, viewModel)
        }
    }
}
