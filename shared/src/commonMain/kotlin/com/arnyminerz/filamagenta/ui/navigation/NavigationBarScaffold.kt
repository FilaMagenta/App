package com.arnyminerz.filamagenta.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.ui.logic.WindowSizeClass
import com.arnyminerz.filamagenta.ui.logic.calculateWindowSizeClass
import kotlinx.coroutines.launch

/**
 * Provides a scaffold with a navigation bar that adapts to the current window size. UI is:
 * - **Small displays**: Bottom navigation bar
 * - **Medium displays**: Left side navigation rail
 * - **Large displays**: Expanded (with labels) navigation rail
 *
 * Since small displays show a bottom navigation bar, it's recommended to keep the icons count between `3` and `5`.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavigationBarScaffold(
    items: List<NavigationBarItem>,
    state: PagerState = rememberPagerState { items.size },
    topBar: @Composable () -> Unit = {},
    content: @Composable BoxScope.(index: Int) -> Unit
) {
    val windowSizeClass = calculateWindowSizeClass()

    when (windowSizeClass) {
        WindowSizeClass.Compact -> {
            val scope = rememberCoroutineScope()

            Scaffold(
                topBar = topBar,
                bottomBar = {
                    NavigationBar {
                        for ((index, item) in items.withIndex()) {
                            NavigationBarItem(
                                selected = state.currentPage == index,
                                icon = { item.Icon() },
                                label = { Text(item.label()) },
                                onClick = {
                                    scope.launch { state.animateScrollToPage(index) }
                                }
                            )
                        }
                    }
                }
            ) { paddingValues ->
                HorizontalPager(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) { page ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        content(page)
                    }
                }
            }
        }

        WindowSizeClass.Medium -> {
            TODO("Design not yet implemented")
        }

        WindowSizeClass.Expanded -> {
            TODO("Design not yet implemented")
        }
    }
}
