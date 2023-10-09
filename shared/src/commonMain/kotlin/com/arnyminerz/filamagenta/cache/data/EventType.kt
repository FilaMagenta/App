package com.arnyminerz.filamagenta.cache.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BreakfastDining
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.ui.graphics.vector.ImageVector
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.StringResource

enum class EventType(
    val icon: ImageVector,
    val label: StringResource
) {
    Unknown(Icons.Outlined.Block, MR.strings.event_type_unknown),
    Breakfast(Icons.Outlined.BreakfastDining, MR.strings.event_type_breakfast),
    Lunch(Icons.Outlined.LunchDining, MR.strings.event_type_lunch),
    Dinner(Icons.Outlined.DinnerDining, MR.strings.event_type_dinner),
    Assembly(Icons.Outlined.Groups, MR.strings.event_type_assembly),
    Workshop(Icons.Outlined.Handyman, MR.strings.event_type_workshop)
}
