package com.arnyminerz.filamagenta.ui.screen

import QrScannerScreen
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.data.cleanName
import com.arnyminerz.filamagenta.cache.data.hasTicket
import com.arnyminerz.filamagenta.network.server.exception.WordpressException
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.getBooleanState
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.dialog.ScanResultDialog
import com.arnyminerz.filamagenta.ui.logic.BackHandler
import com.arnyminerz.filamagenta.ui.screen.model.IntroScreen
import com.arnyminerz.filamagenta.ui.screen.model.IntroScreenPage
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

/**
 * The main composable that then renders all the app. Has some useful inputs to control what is displayed when.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    isAddingNewAccount: Boolean = false,
    viewModel: MainViewModel = MainViewModel(),
    onApplicationEndRequested: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    val isRequestingToken by viewModel.isRequestingToken.collectAsState(initial = false)

    val accountsList by accounts.getAccountsLive().collectAsState()

    var addNewAccountRequested by remember { mutableStateOf(isAddingNewAccount) }

    /** If true, the login screen is shown */
    val addingNewAccount = accountsList?.isEmpty() == true || addNewAccountRequested

    val account by viewModel.account.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState(false)

    val error by viewModel.error.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val viewingEvent by viewModel.viewingEvent.collectAsState()
    val editingField by viewModel.editingField.collectAsState()

    val isScanningQr by viewModel.scanningQr.collectAsState(false)
    val scanResult by viewModel.scanResult.collectAsState(null)

    var shownAdmin by settings.getBooleanState(SettingsKeys.SYS_SHOWN_ADMIN, false)

    LaunchedEffect(Unit) {
        // Initialize logging library
        Napier.base(DebugAntilog())

        // Initialize libsodium
        LibsodiumInitializer.initialize()

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

    error?.let { it as? WordpressException }?.let { wordpressException ->
        val errorString = buildAnnotatedString {
            appendLine(stringResource(MR.strings.error_dialog_message))

            append(stringResource(MR.strings.error_dialog_path))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)) {
                appendLine(wordpressException.path)
            }
            appendLine()

            append(stringResource(MR.strings.error_dialog_status))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)) {
                appendLine(wordpressException.error.data.status.toString())
            }
            appendLine()

            append(stringResource(MR.strings.error_dialog_code))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)) {
                appendLine(wordpressException.error.code)
            }
            appendLine()

            appendLine(stringResource(MR.strings.error_dialog_msg))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)) {
                appendLine(wordpressException.error.message)
            }
            appendLine()

            appendLine(stringResource(MR.strings.error_dialog_trace))
            withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                appendLine(wordpressException.stackTraceToString())
            }
            appendLine()
        }

        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text(stringResource(MR.strings.error_dialog_title)) },
            text = {
                SelectionContainer(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    Text(errorString)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { uriHandler.openUri("https://status.arnyminerz.com/status/filamagenta") }
                ) {
                    Text(stringResource(MR.strings.server_status))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { clipboardManager.setText(errorString) }
                ) {
                    Text(stringResource(MR.strings.copy))
                }
            }
        )
    }

    val isLoading = isRequestingToken || accountsList == null || !addingNewAccount && account == null

    BackHandler {
        if (isLoading) {
            // ignore
        } else if (addingNewAccount) {
            if (accountsList.isNullOrEmpty()) {
                // If there aren't any accounts, close the app
                onApplicationEndRequested()
            } else {
                // If there's at least one account, hide the account adder
                addNewAccountRequested = false
            }
        } else if (isScanningQr) {
            // If it is scanning, and back is pressed, stop scanning
            viewModel.stopScanner()
        } else if (viewingEvent != null) {
            // If there's an event being viewed
            viewModel.stopViewingEvent()
        } else {
            onApplicationEndRequested()
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        addingNewAccount -> {
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
            scanResult?.let { result ->
                ScanResultDialog(result, viewModel::dismissScanResult)
            }

            QrScannerScreen(
                modifier = Modifier.fillMaxSize()
            ) { if (scanResult == null) viewModel.validateQr(it) }
        }

        viewingEvent != null -> {
            val event = viewingEvent!!

            editingField?.let { field ->
                field.editor.Dialog(
                    title = event.cleanName + " - " + stringResource(field.displayName),
                    onSubmit = { viewModel.performUpdate(event, field) },
                    onDismissRequest = viewModel::cancelEdit
                )
            }

            if (event.hasTicket) {
                DisposableEffect(event) {
                    val job = viewModel.fetchOrders(event.id.toInt())

                    onDispose { job.cancel() }
                }
            }

            EventScreen(event, viewModel)
        }

        else -> {
            AppScreen(mainPagerState, viewModel)
        }
    }
}
