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
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.getBooleanState
import com.arnyminerz.filamagenta.storage.getStringState
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.reusable.settings.SettingsItem
import com.arnyminerz.filamagenta.ui.reusable.settings.SettingsList
import com.arnyminerz.filamagenta.ui.reusable.settings.SettingsSection
import com.arnyminerz.filamagenta.utils.Language
import com.arnyminerz.filamagenta.utils.UriUtils.CROWDIN_PROJECT_URL
import com.arnyminerz.filamagenta.utils.UriUtils.GITHUB_REPO_URL
import com.arnyminerz.filamagenta.utils.UriUtils.getPrivacyPolicyUrl
import com.arnyminerz.filamagenta.utils.prefixSystem
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.Napier

@Composable
fun SettingsPage() {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    var dataCollection by settings.getBooleanState(SettingsKeys.DATA_COLLECTION, true)
    var selectedLanguage by settings.getStringState(SettingsKeys.LANGUAGE, Language.System.langCode)

    LaunchedEffect(selectedLanguage) {
        StringDesc.localeType = Language(selectedLanguage).localeType
    }

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
        Napier.d("Selected language: $selectedLanguage")

        val privacyPolicyLanguage = selectedLanguage.takeIf { it != Language.System.langCode } ?: "en"

        SettingsList(
            default = Language(selectedLanguage),
            options = languages,
            dialogTitle = stringResource(MR.strings.settings_language_title),
            headline = stringResource(MR.strings.settings_language_title),
            toString = { it.displayName },
            icon = Icons.Outlined.Language
        ) { selectedLanguage = it.langCode }


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
        ) { uriHandler.openUri(CROWDIN_PROJECT_URL) }
        SettingsItem(
            headline = stringResource(MR.strings.settings_source_title),
            summary = stringResource(MR.strings.settings_source_summary),
            icon = Icons.Rounded.Code
        ) { uriHandler.openUri(GITHUB_REPO_URL) }
        SettingsItem(
            headline = stringResource(MR.strings.settings_privacy_title),
            summary = stringResource(MR.strings.settings_privacy_summary),
            icon = Icons.Rounded.Policy
        ) { uriHandler.openUri(getPrivacyPolicyUrl(privacyPolicyLanguage)) }

        SettingsItem(
            headline = stringResource(MR.strings.settings_collection_title),
            summary = stringResource(MR.strings.settings_collection_summary),
            icon = Icons.Rounded.ErrorOutline,
            trailingContent = {
                Switch(
                    checked = dataCollection,
                    onCheckedChange = { dataCollection = it },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        ) { dataCollection = !dataCollection }
    }
}
