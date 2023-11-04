package com.arnyminerz.filamagenta.ui.screen.model

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.ui.logic.BackHandler
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

data class IntroScreenPage(
    val title: @Composable () -> String,
    val message: @Composable () -> String,
    val image: @Composable (() -> Painter)? = null
) {
    @Composable
    fun Content() {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .padding(top = 96.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 28.sp
            )
            Text(
                text = message(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            if (image != null) {
                Image(
                    painter = image.invoke(),
                    contentDescription = null,
                    modifier = Modifier.size(256.dp).padding(top = 56.dp).align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun IntroScreen(pages: List<IntroScreenPage>, onFinish: () -> Unit, onCancel: () -> Unit) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { pages.size }

    BackHandler {
        if (pagerState.currentPage == 0) {
            onCancel()
        } else {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val page = pagerState.currentPage
                    val nextPage = page + 1

                    if (nextPage >= pages.size) {
                        onFinish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(nextPage) }
                    }
                }
            ) {
                AnimatedContent(pagerState.currentPage) { page ->
                    if (page + 1 >= pages.size) {
                        Icon(Icons.Rounded.Check, stringResource(MR.strings.done))
                    } else {
                        Icon(Icons.Rounded.ChevronRight, stringResource(MR.strings.next))
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                pages[page].Content()
            }
        }
    }
}
