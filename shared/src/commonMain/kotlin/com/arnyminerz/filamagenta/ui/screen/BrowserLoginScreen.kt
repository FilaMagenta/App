package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.ui.logic.BackHandler
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import dev.icerock.moko.resources.compose.stringResource
import io.ktor.http.Url

/**
 * The zIndex of the progress indicator so that it shows on top of the webview.
 */
private const val PROGRESS_INDICATOR_Z_INDEX = 999f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserLoginScreen(
    authorizeUrl: String,
    onDismissRequested: () -> Unit,
    onCodeObtained: (code: String) -> Unit
) {
    val state = rememberWebViewState(
        url = authorizeUrl
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

        onCodeObtained(code)
    }

    BackHandler(onBack = onDismissRequested)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(MR.strings.login_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismissRequested
                    ) {
                        Icon(Icons.Rounded.ChevronLeft, stringResource(MR.strings.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            AnimatedContent(
                targetState = state.loadingState,
                modifier = Modifier
                    .align(Alignment.Center)
                    // Make sure it's on top
                    .zIndex(PROGRESS_INDICATOR_Z_INDEX)
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
    }
}
