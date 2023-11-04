package com.arnyminerz.filamagenta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.compose.fontFamilyResource

val typography: Typography
    @Composable
    get() = Typography(
        titleLarge = MaterialTheme.typography.displayLarge.copy(
            fontFamily = fontFamilyResource(MR.fonts.Gabarito.regular),
            fontSize = 26.sp
        ),
        titleMedium = MaterialTheme.typography.displayLarge.copy(
            fontFamily = fontFamilyResource(MR.fonts.Gabarito.regular),
            fontSize = 20.sp
        ),
        titleSmall = MaterialTheme.typography.displayLarge.copy(
            fontFamily = fontFamilyResource(MR.fonts.Gabarito.regular),
            fontSize = 16.sp
        )
    )
