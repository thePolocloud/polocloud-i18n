package de.polocloud.i18n.loader.parser

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import de.polocloud.i18n.model.Language
import de.polocloud.i18n.model.TranslationPackMeta

internal object MetaParser {

    private val gson = Gson()

    fun parse(content: String): de.polocloud.i18n.model.TranslationPackMeta {
        try {
            val dto = gson.fromJson(content, _root_ide_package_.de.polocloud.i18n.loader.parser.MetaDto::class.java)

            require(dto.name.isNotBlank()) { "Pack meta name is blank" }
            require(dto.version.isNotBlank()) { "Pack meta version is blank" }
            require(dto.defaultLanguage.isNotBlank()) { "Pack meta defaultLanguage is blank" }
            require(dto.languages.isNotEmpty()) { "Pack meta languages list is empty" }

            return _root_ide_package_.de.polocloud.i18n.model.TranslationPackMeta(
                name = dto.name,
                version = dto.version,
                defaultLanguage = _root_ide_package_.de.polocloud.i18n.model.Language.of(dto.defaultLanguage),
                languages = dto.languages
                    .map { _root_ide_package_.de.polocloud.i18n.model.Language.of(it) }
                    .toSet()
            )
        } catch (exception: JsonSyntaxException) {
            throw IllegalStateException("Invalid pack.json format", exception)
        }
    }
}
