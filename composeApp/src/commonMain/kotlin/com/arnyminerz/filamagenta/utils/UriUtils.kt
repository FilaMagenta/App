package com.arnyminerz.filamagenta.utils

object UriUtils {
    const val CROWDIN_PROJECT_URL = "https://crowdin.com/project/fila-magenta-app"

    const val GITHUB_REPO_URL = "https://github.com/FilaMagenta/App"

    /**
     * Obtains the url for the privacy policy in the given language.
     *
     * @param langCode The two-letter language code to get. Languages only available if synced with Crowdin. Defaults
     * to English.
     */
    fun getPrivacyPolicyUrl(langCode: String = "en") =
        "https://github.com/FilaMagenta/App/blob/master/docs/$langCode/PRIVACY_POLICY.md"
}
