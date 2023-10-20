package com.arnyminerz.filamagenta.ui.screen

import QrScannerScreen
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.cleanName
import com.arnyminerz.filamagenta.cache.data.hasTicket
import com.arnyminerz.filamagenta.device.Diagnostics
import com.arnyminerz.filamagenta.network.server.exception.WordpressException
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.SettingsKeys.SELECTED_ACCOUNT
import com.arnyminerz.filamagenta.storage.getBooleanState
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.dialog.GenericErrorDialog
import com.arnyminerz.filamagenta.ui.dialog.ScanResultDialog
import com.arnyminerz.filamagenta.ui.dialog.WordpressErrorDialog
import com.arnyminerz.filamagenta.ui.logic.BackHandler
import com.arnyminerz.filamagenta.ui.reusable.LoadingBox
import com.arnyminerz.filamagenta.ui.screen.model.IntroScreen
import com.arnyminerz.filamagenta.ui.screen.model.IntroScreenPage
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier

/**
 * The main composable that then renders all the app. Has some useful inputs to control what is displayed when.
 *
 * @param nfc If the launch reason is an NFC tag, you can pass the contents here.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    isAddingNewAccount: Boolean = false,
    viewModel: MainViewModel = MainViewModel(),
    nfc: String? = null,
    onApplicationEndRequested: () -> Unit
) {
    val isRequestingToken by viewModel.isRequestingToken.collectAsState(initial = false)

    val accountsList by accounts.getAccountsLive().collectAsState()

    var addNewAccountRequested by remember { mutableStateOf(isAddingNewAccount) }

    /** If true, the login screen is shown */
    val addingNewAccount = accountsList?.isEmpty() == true || addNewAccountRequested

    val account by viewModel.account.collectAsState(null)
    val isAdmin by viewModel.isAdmin.collectAsState(false)

    val error by viewModel.error.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val viewingEvent by viewModel.viewingEvent.collectAsState()

    val isScanningQr by viewModel.scanningQr.collectAsState(false)
    val scanResult by viewModel.scanResult.collectAsState(null)

    var shownAdmin by settings.getBooleanState(SettingsKeys.SYS_SHOWN_ADMIN, false)

    LaunchedEffect(accountsList) {
        viewModel.updateSelectedAccount()
    }

    LaunchedEffect(nfc) {
        if (nfc == null) return@LaunchedEffect

        viewModel.processNfcTag(nfc)
    }

    scanResult?.let { result ->
        ScanResultDialog(result, viewModel::dismissScanResult)
    }

    when {
        error is WordpressException -> WordpressErrorDialog(error as WordpressException) { viewModel.dismissError() }
        error != null -> GenericErrorDialog(error as Throwable) { viewModel.dismissError() }
    }

    val isLoading = isRequestingToken || accountsList == null || !addingNewAccount && account == null

    when {
        isLoading -> {
            LaunchedEffect(Unit) {
                Napier.v {
                    "Showing loading indicator. " +
                            "isRequestingToken? $isRequestingToken, " +
                            "accountsList==null? ${accountsList == null}, " +
                            "addingNewAccount? $addingNewAccount, " +
                            "account==null? ${account == null}"
                }
            }

            BackHandler { /* Ignore all back presses */ }

            LoadingBox()
        }

        addingNewAccount -> {
            BackHandler {
                if (accountsList.isNullOrEmpty()) {
                    // If there aren't any accounts, close the app
                    onApplicationEndRequested()
                } else {
                    // If there's at least one account, hide the account adder
                    addNewAccountRequested = false
                }
            }

            LoginScreen(
                isError = loginError,
                onDismissErrorRequested = { viewModel.loginError.value = false },
                onLoginRequested = viewModel::login,
                onBackRequested = {
                    if (accountsList.isNullOrEmpty()) {
                        onApplicationEndRequested()
                    } else {
                        addNewAccountRequested = false
                    }
                }
            )
        }

        isAdmin == true && !shownAdmin -> {
            BackHandler(onBack = onApplicationEndRequested)

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
                onFinish = { shownAdmin = true },
                onCancel = onApplicationEndRequested
            )
        }

        isScanningQr -> {
            BackHandler {
                // If it is scanning, and back is pressed, stop scanning
                viewModel.stopScanner()
            }

            QrScannerScreen(
                modifier = Modifier.fillMaxSize()
            ) { if (scanResult == null) viewModel.validateQr(it) }
        }

        viewingEvent != null -> {
            val event = viewingEvent!!

            val isLoadingOrders by viewModel.isLoadingOrders.collectAsState(initial = false)
            val editingField by viewModel.editingField.collectAsState()

            BackHandler {
                // If there's an event being viewed
                viewModel.stopViewingEvent()
            }

            editingField?.let { field ->
                field.editor.Dialog(
                    title = event.cleanName + " - " + stringResource(field.displayName),
                    onSubmit = { viewModel.performUpdate(event, field) },
                    onDismissRequest = viewModel::cancelEdit
                )
            }

            DisposableEffect(Unit) {
                val ordersForEvent = Cache.ordersForEvent(event.id).executeAsList()
                val job = if (event.hasTicket && !isLoadingOrders && ordersForEvent.isEmpty()) {
                    viewModel.fetchOrders(event.id.toInt())
                } else {
                    null
                }

                onDispose { job?.cancel() }
            }

            EventScreen(event, viewModel)
        }

        else -> {
            val mainPagerState = rememberPagerState { appScreenItems.size }

            BackHandler(onBack = onApplicationEndRequested)

            AppScreen(mainPagerState, viewModel)
        }
    }
}
