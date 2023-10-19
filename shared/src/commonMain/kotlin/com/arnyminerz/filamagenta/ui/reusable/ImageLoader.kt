package com.arnyminerz.filamagenta.ui.reusable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun ImageLoader(
    image: ImageBitmap?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = image,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { img ->
            OutlinedCard(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
            ) {
                if (img != null) {
                    Image(
                        bitmap = img,
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
