package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.data.cleanName
import com.arnyminerz.filamagenta.ui.logic.BackHandler
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.http.Url

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
    val isRequestingToken by viewModel.isRequestingToken.collectAsState(initial = false)

    var showingLoginWebpage by remember { mutableStateOf(false) }

    val accountsList by accounts!!.getAccountsLive().collectAsState()

    var addNewAccountRequested by remember { mutableStateOf(isAddingNewAccount) }

    /** If true, the login screen is shown */
    val addingNewAccount = accountsList.isEmpty() || addNewAccountRequested

    LaunchedEffect(Unit) {
        // Initialize logging library
        Napier.base(DebugAntilog())
    }

    val mainPagerState = rememberPagerState { appScreenItems.size }

    if (isRequestingToken) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (showingLoginWebpage) {
        val state = rememberWebViewState(
            url = viewModel.getAuthorizeUrl()
        )
        val navigator = rememberWebViewNavigator()

        // Set custom agent
        LaunchedEffect(state) {
            state.webSettings.customUserAgentString = "Fila-Magenta-App"
        }
        if (state.lastLoadedUrl?.startsWith("app://filamagenta") == true) {
            // Redirection complete, extract code
            val query = Url(state.lastLoadedUrl!!)
                .encodedQuery
                .split("&")
                .associate { it.split("=").let { (k, v) -> k to v } }
            val code = query["code"]
            // todo - notify the user about this error, even though it should never occur
            requireNotNull(code) { "Server redirected without a valid code" }

            showingLoginWebpage = false

            viewModel.requestToken(code)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            AnimatedContent(
                targetState = state.loadingState,
                modifier = Modifier
                    .align(Alignment.Center)
                    // Make sure it's on top
                    .zIndex(999f)
            ) { loadingState ->
                if (loadingState is LoadingState.Initializing) {
                    CircularProgressIndicator()
                } else if (loadingState is LoadingState.Loading) {
                    CircularProgressIndicator(
                        progress = loadingState.progress
                    )
                }
            }

            WebView(
                state = state,
                modifier = Modifier.fillMaxSize(),
                navigator = navigator
            )
        }
    } else if (addingNewAccount) {
        LoginScreen(
            onLoginRequested = { showingLoginWebpage = true },
            onBackRequested = {
                if (accountsList.isEmpty()) {
                    onApplicationEndRequested()
                } else {
                    addNewAccountRequested = false
                }
            }
        )
    } else {
        val isUserAdmin = remember { accounts!!.isAdmin(accountsList.first()) }
        val event by viewModel.viewingEvent.collectAsState()
        val editingField by viewModel.editingField.collectAsState()

        BackHandler {
            if (event == null) {
                onApplicationEndRequested()
            } else {
                viewModel.stopViewingEvent()
            }
        }

        event?.let { ev ->
            editingField?.let { field ->
                field.editor.Dialog(
                    title = ev.cleanName + " - " + stringResource(field.displayName),
                    onSubmit = { /* TODO: submit edit */ },
                    onDismissRequest = viewModel::cancelEdit
                )
            }

            EventScreen(
                ev,
                viewModel::edit.takeIf { isUserAdmin },
                viewModel::stopViewingEvent
            )
        } ?: AppScreen(isUserAdmin, mainPagerState, viewModel::viewEvent)
    }
}
