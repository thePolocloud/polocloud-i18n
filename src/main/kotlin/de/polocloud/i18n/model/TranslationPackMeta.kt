package de.polocloud.i18n.model

import java.util.Locale

data class TranslationPackMeta(
    val name: String,
    val version: String,
    val defaultLanguage: Locale,
    val languages: Set<Locale>,
)