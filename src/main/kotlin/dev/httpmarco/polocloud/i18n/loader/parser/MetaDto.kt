package dev.httpmarco.polocloud.i18n.loader.parser

data class MetaDto(
    val name: String,
    val version: String,
    val defaultLanguage: String,
    val languages: List<String>,
)