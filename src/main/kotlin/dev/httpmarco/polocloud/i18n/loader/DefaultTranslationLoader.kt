package dev.httpmarco.polocloud.i18n.loader

import dev.httpmarco.polocloud.i18n.loader.parser.MetaParser
import dev.httpmarco.polocloud.i18n.loader.parser.PropertiesParser
import dev.httpmarco.polocloud.i18n.loader.resource.TranslationResourceProvider
import dev.httpmarco.polocloud.i18n.model.Language
import dev.httpmarco.polocloud.i18n.model.TranslationPack
import dev.httpmarco.polocloud.i18n.model.TranslationPackMeta
import java.util.Locale

class DefaultTranslationLoader(
    private val resourceProvider: TranslationResourceProvider
) : TranslationLoader {

    override fun loadMeta(pack: String): TranslationPackMeta {
        resourceProvider.openMeta(pack).use { input ->
            val content = input.bufferedReader().readText()
            return MetaParser.parse(content)
        }
    }

    override fun loadPack(meta: TranslationPackMeta, language: Locale): TranslationPack {
        require(meta.languages.contains(language)) {
            "Language '${Language.code(language)}' not supported by pack '${meta.name}'"
        }

        val translations = resourceProvider
            .openLanguageFile(meta.name, Language.code(language))
            .use(PropertiesParser::parse)

        return TranslationPack(
            meta = meta,
            language = language,
            translations = translations
        )
    }
}
