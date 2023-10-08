package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.compose.stringResource

/**
 * Once logged in, this is the first screen shown to the user.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(MR.strings.app_name)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}
