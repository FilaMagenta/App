package com.arnyminerz.filamagenta.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.reusable.settings.SettingsItem
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
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsSection(
            text = stringResource(MR.strings.settings_section_user_interface)
        )

        val languages = BuildKonfig.Languages.split(",").map { Language(it) }.prefixSystem()

        Napier.d("Supported languages: $languages")

        SettingsList(
            default = settings.getStringOrNull(SettingsKeys.LANGUAGE)?.let { Language(it) } ?: Language.System,
            options = languages,
            dialogTitle = stringResource(MR.strings.settings_language_title),
            headline = stringResource(MR.strings.settings_language_title),
            toString = { it.displayName },
            icon = Icons.Outlined.Language
        ) {
            settings[SettingsKeys.LANGUAGE] = it.langCode
            StringDesc.localeType = it.localeType
        }


        SettingsSection(
            text = stringResource(MR.strings.settings_section_application)
        )

        SettingsItem(
            headline = stringResource(MR.strings.settings_release_title),
            summary = BuildKonfig.ReleaseName,
            icon = Icons.Outlined.Info
        ) { clipboardManager.setText(buildAnnotatedString { append(BuildKonfig.ReleaseName) }) }
        SettingsItem(
            headline = stringResource(MR.strings.settings_translations_title),
            summary = stringResource(MR.strings.settings_translations_summary),
            icon = Icons.Rounded.Translate
        ) { uriHandler.openUri("https://crowdin.com/project/fila-magenta-app") }
        SettingsItem(
            headline = stringResource(MR.strings.settings_source_title),
            summary = stringResource(MR.strings.settings_source_summary),
            icon = Icons.Rounded.Code
        ) { uriHandler.openUri("https://github.com/FilaMagenta/App") }
    }
}
