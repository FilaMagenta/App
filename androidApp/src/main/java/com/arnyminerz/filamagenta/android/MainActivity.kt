package com.arnyminerz.filamagenta.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.arnyminerz.filamagenta.ui.browser.CustomBrowserUi
import com.arnyminerz.filamagenta.ui.screen.MainScreen
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import com.arnyminerz.filamagenta.ui.theme.AppTheme

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CustomBrowserUi.provideApplicationContext(this)

        setContent {
            AppTheme {
                MainScreen(intent.dataString, viewModel)
            }
        }
    }
}
