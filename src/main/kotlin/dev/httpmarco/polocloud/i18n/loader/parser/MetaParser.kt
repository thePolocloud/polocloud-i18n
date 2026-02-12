package dev.httpmarco.polocloud.i18n.loader.parser

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dev.httpmarco.polocloud.i18n.model.Language
import dev.httpmarco.polocloud.i18n.model.TranslationPackMeta

internal object MetaParser {

    private val gson = Gson()

    fun parse(content: String): TranslationPackMeta {
        try {
            val dto = gson.fromJson(content, MetaDto::class.java)

            require(dto.name.isNotBlank()) { "Pack meta name is blank" }
            require(dto.version.isNotBlank()) { "Pack meta version is blank" }
            require(dto.defaultLanguage.isNotBlank()) { "Pack meta defaultLanguage is blank" }
            require(dto.languages.isNotEmpty()) { "Pack meta languages list is empty" }

            return TranslationPackMeta(
                name = dto.name,
                version = dto.version,
                defaultLanguage = Language(dto.defaultLanguage),
                languages = dto.languages
                    .map(::Language)
                    .toSet()
            )
        } catch (exception: JsonSyntaxException) {
            throw IllegalStateException("Invalid pack.json format", exception)
        }
    }
}