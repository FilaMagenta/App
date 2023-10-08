package com.arnyminerz.filamagenta.ui.browser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun InAppWebBrowser(modifier: Modifier = Modifier) {
    val url by InAppWebBrowserStateHandler.url.collectAsState()

    if (url == null) return

    val state = rememberWebViewState(
        url = url!!
    )
    val navigator = rememberWebViewNavigator()
    WebView(state = state, modifier = modifier, navigator = navigator)
}
