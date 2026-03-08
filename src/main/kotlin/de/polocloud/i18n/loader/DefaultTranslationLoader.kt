package de.polocloud.i18n.loader

import de.polocloud.i18n.loader.parser.MetaParser
import de.polocloud.i18n.loader.parser.PropertiesParser
import de.polocloud.i18n.loader.resource.TranslationResourceProvider
import de.polocloud.i18n.model.Language
import de.polocloud.i18n.model.TranslationPack
import de.polocloud.i18n.model.TranslationPackMeta
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.Locale

class DefaultTranslationLoader(
    private val resourceProvider: TranslationResourceProvider
) : TranslationLoader {

    private val logger: Logger = LogManager.getLogger(DefaultTranslationLoader::class.java)

    override fun loadMeta(pack: String): TranslationPackMeta {
        resourceProvider.openMeta(pack).use { input ->
            val content = input.bufferedReader().readText()
            return MetaParser.parse(content)
        }
    }

    override fun loadPack(meta: TranslationPackMeta, language: Locale): TranslationPack {
        val resolvedLanguage = when {
            meta.languages.contains(language) -> language

            meta.languages.contains(meta.defaultLanguage) -> {
                logger.warn(
                    "Language '{}' not supported by pack '{}'. Falling back to default '{}'.",
                    Language.code(language),
                    meta.name,
                    Language.code(meta.defaultLanguage)
                )
                meta.defaultLanguage
            }

            meta.languages.isNotEmpty() -> {
                val fallback = meta.languages.first()
                logger.warn(
                    "Language '{}' not supported by pack '{}'. Default language unavailable. Falling back to '{}'.",
                    Language.code(language),
                    meta.name,
                    Language.code(fallback)
                )
                fallback
            }

            else -> {
                logger.error(
                    "Translation pack '{}' does not define any supported languages.",
                    meta.name
                )
                throw IllegalStateException(
                    "Translation pack '${meta.name}' contains no supported languages"
                )
            }
        }

        val translations = resourceProvider
            .openLanguageFile(meta.name, Language.code(resolvedLanguage))
            .use(PropertiesParser::parse)

        return TranslationPack(
            meta = meta,
            language = language,
            translations = translations
        )
    }
}
