package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.ui.state.MainViewModel

/**
 * The main composable that then renders all the app. Has some useful inputs to control what is displayed when.
 *
 * @param uri If this has been launched from an url, it can be passed here. For example, for responses of the OAuth
 * workflow.
 */
@Composable
fun MainScreen(uri: String? = null, viewModel: MainViewModel = MainViewModel()) {
    /** Displays a loading spinner when this is true */
    var isLoading by remember { mutableStateOf(true) }

    /** If true, the login screen is shown */
    var addingNewAccount by remember { mutableStateOf(false) }

    // Check if there's any account added
    LaunchedEffect(accounts) {
        val accountsList = accounts!!.getAccounts()

        if (accountsList.isEmpty()) {
            addingNewAccount = true
        }
        isLoading = false
    }

    println("MainScreen :: uri = $uri")

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (addingNewAccount) {
        LoginScreen {
            TODO("Not yet implemented")
        }
    } else {
        Text("Hello from Compose!")
    }
}
