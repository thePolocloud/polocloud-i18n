package de.polocloud.i18n.model

import java.util.Locale

data class TranslationPack(
    val meta: de.polocloud.i18n.model.TranslationPackMeta,
    val language: Locale,
    val translations: Map<String, String>,
) {
    fun get(key: String): String? = translations[key]

    fun require(key: String): String = translations[key] ?: error(
        "Missing translation key '$key' in pack '${meta.name}' for language '${_root_ide_package_.de.polocloud.i18n.model.Language.code(language)}'"
    )
}