package com.arnyminerz.filamagenta.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.arnyminerz.filamagenta.ui.screen.MainScreen
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import com.arnyminerz.filamagenta.ui.theme.AppTheme

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NEW_ACCOUNT = "new_account"
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainScreen(
                    intent.getBooleanExtra(EXTRA_NEW_ACCOUNT, false),
                    viewModel
                ) {
                    finish()
                }
            }
        }
    }
}
