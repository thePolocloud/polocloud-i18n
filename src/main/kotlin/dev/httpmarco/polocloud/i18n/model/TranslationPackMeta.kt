package dev.httpmarco.polocloud.i18n.model

data class TranslationPackMeta(
    val name: String,
    val version: String,
    val defaultLanguage: Language,
    val languages: Set<Language>,
)