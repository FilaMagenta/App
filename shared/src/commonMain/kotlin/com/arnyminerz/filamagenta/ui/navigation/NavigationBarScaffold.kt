package com.arnyminerz.filamagenta.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    maxContentWidth: Dp = 600.dp,
    topBar: @Composable () -> Unit = {},
    content: @Composable BoxScope.(index: Int) -> Unit
) {
    val windowSizeClass = calculateWindowSizeClass()

    val scope = rememberCoroutineScope()

    when (windowSizeClass) {
        WindowSizeClass.Compact -> {
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
                        modifier = Modifier.widthIn(max = maxContentWidth).fillMaxSize()
                    ) {
                        content(page)
                    }
                }
            }
        }

        WindowSizeClass.Medium, WindowSizeClass.Expanded -> {
            Scaffold(
                topBar = topBar
            ) { paddingValues ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    NavigationRail {
                        for ((index, item) in items.withIndex()) {
                            NavigationRailItem(
                                selected = state.currentPage == index,
                                icon = { item.Icon() },
                                label = { Text(item.label()) },
                                alwaysShowLabel = windowSizeClass == WindowSizeClass.Expanded,
                                onClick = {
                                    scope.launch { state.animateScrollToPage(index) }
                                }
                            )
                        }
                    }
                    HorizontalPager(
                        state = state,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) { page ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.widthIn(max = maxContentWidth).fillMaxSize()
                            ) {
                                content(page)
                            }
                        }
                    }
                }
            }
        }
    }
}
