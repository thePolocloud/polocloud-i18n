package de.polocloud.i18n.model

import java.util.Locale

data class TranslationPack(
    val meta: TranslationPackMeta,
    val language: Locale,
    val translations: Map<String, String>,
) {
    fun get(key: String): String? = translations[key]

    fun require(key: String): String = translations[key] ?: error(
        "Missing translation key '$key' in pack '${meta.name}' for language '${Language.code(language)}'"
    )
}