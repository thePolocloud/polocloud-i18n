package dev.httpmarco.polocloud.i18n.model

data class TranslationPack(
    val meta: TranslationPackMeta,
    val language: Language,
    val translations: Map<String, String>,
) {
    fun get(key: String): String? = translations[key]

    fun require(key: String): String = translations[key] ?: error(
        "Missing translation key '$key' in pack '${meta.name}' for language '${language.code}'"
    )
}