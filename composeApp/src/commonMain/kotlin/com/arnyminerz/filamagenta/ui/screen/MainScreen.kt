package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.network.server.exception.WordpressException
import com.arnyminerz.filamagenta.ui.dialog.GenericErrorDialog
import com.arnyminerz.filamagenta.ui.dialog.ScanResultDialog
import com.arnyminerz.filamagenta.ui.dialog.WordpressErrorDialog
import com.arnyminerz.filamagenta.ui.state.MainScreenModel

/**
 * The main screen that then renders all the app. Has some useful inputs to control what is displayed when.
 */
@OptIn(ExperimentalFoundationApi::class)
object MainScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MainScreenModel() }
        
        val accountsList by accounts.getAccountsLive().collectAsState()

        val error by screenModel.error.collectAsState()

        val scanResult by screenModel.scanResult.collectAsState(null)

        LaunchedEffect(accountsList) {
            screenModel.updateSelectedAccount()
        }

        // TODO - reimplement nfc
        /*LaunchedEffect(nfc) {
            if (nfc == null) return@LaunchedEffect

            screenModel.processNfcTag(nfc)
        }*/

        scanResult?.let { result ->
            ScanResultDialog(result, screenModel::dismissScanResult)
        }

        when {
            error is WordpressException ->
                WordpressErrorDialog(error as WordpressException, screenModel::dismissError)
            error != null ->
                GenericErrorDialog(error as Throwable, screenModel::dismissError)
        }

        when {
            else -> {
                val mainPagerState = rememberPagerState { appScreenItems.size }

                AppScreen(mainPagerState, navigator, screenModel)
            }
        }
    }
}
