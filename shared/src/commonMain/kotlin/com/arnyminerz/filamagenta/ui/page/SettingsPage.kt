package com.arnyminerz.filamagenta.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.reusable.settings.SettingsList
import com.arnyminerz.filamagenta.ui.reusable.settings.SettingsSection
import com.arnyminerz.filamagenta.utils.Language
import com.arnyminerz.filamagenta.utils.prefixSystem
import com.russhwolf.settings.set
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.Napier

@Composable
fun SettingsPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsSection(
            text = stringResource(MR.strings.settings_section_user_interface),
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
        )

        val languages = BuildKonfig.Languages.split(",").map { Language(it) }.prefixSystem()

        Napier.d("Supported languages: $languages")

        SettingsList(
            default = settings.getStringOrNull(SettingsKeys.LANGUAGE)?.let { Language(it) } ?: Language.System,
            options = languages,
            dialogTitle = stringResource(MR.strings.settings_language_title),
            headline = stringResource(MR.strings.settings_language_title),
            toString = { it.displayName },
            modifier = Modifier.widthIn(max = 600.dp),
            icon = Icons.Outlined.Language
        ) {
            settings[SettingsKeys.LANGUAGE] = it.langCode
            StringDesc.localeType = it.localeType
        }
    }
}
