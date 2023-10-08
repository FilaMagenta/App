package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.ui.browser.InAppWebBrowser
import com.arnyminerz.filamagenta.ui.browser.InAppWebBrowserStateHandler
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import io.ktor.http.Url

/**
 * The main composable that then renders all the app. Has some useful inputs to control what is displayed when.
 *
 * @param uri If this has been launched from an url, it can be passed here. For example, for responses of the OAuth
 * workflow.
 */
@Composable
fun MainScreen(uri: String? = null, isAddingNewAccount: Boolean = false, viewModel: MainViewModel = MainViewModel()) {
    /** Displays a loading spinner when this is true */
    var isLoading by remember { mutableStateOf(true) }
    val isRequestingToken by viewModel.isRequestingToken.collectAsState(initial = false)

    /** If true, the login screen is shown */
    var addingNewAccount by remember { mutableStateOf(isAddingNewAccount) }

    val shouldDisplayWebBrowser by InAppWebBrowserStateHandler.shouldDisplay.collectAsState(initial = false)

    // Check if there's any account added
    LaunchedEffect(accounts) {
        val accountsList = accounts!!.getAccounts()

        if (accountsList.isEmpty()) {
            addingNewAccount = true
        }
        isLoading = false
    }

    // app://filamagenta?code=...&iframe=break
    LaunchedEffect(uri) {
        if (uri != null) {
            val url = Url(uri)
            if (url.host == "filamagenta") {
                println("MainScreen :: uri = $uri")
                val query = url.encodedQuery
                    .split('&')
                    .associate {
                        it.split('=').let { (k, v) -> k to v }
                    }
                val code = query["code"]
                if (code != null) {
                    println("Requesting token for code $code")
                    viewModel.requestToken(code)
                }
            }
        }
    }

    if (isLoading || isRequestingToken) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (shouldDisplayWebBrowser) {
        InAppWebBrowser(
            modifier = Modifier.fillMaxSize()
        )
    } else if (addingNewAccount) {
        LoginScreen(
            onLoginRequested = {
                viewModel.launchLoginUrl()
            }
        ) {
            TODO("Not yet implemented")
        }
    } else {
        Text("Hello from Compose!")
    }
}
