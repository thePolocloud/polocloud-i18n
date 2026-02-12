package dev.httpmarco.polocloud.i18n.core

import dev.httpmarco.polocloud.i18n.core.format.PlaceholderFormatter
import dev.httpmarco.polocloud.i18n.loader.TranslationLoader
import dev.httpmarco.polocloud.i18n.model.Language
import dev.httpmarco.polocloud.i18n.model.TranslationPack
import dev.httpmarco.polocloud.i18n.model.TranslationPackMeta
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.ConcurrentHashMap

class TranslationManager(private val loader: TranslationLoader) {

    private val logger: Logger = LogManager.getLogger(TranslationManager::class.java)
    private val metaCache = ConcurrentHashMap<String, TranslationPackMeta>()
    private val packCache = ConcurrentHashMap<String, TranslationPack>()

    /**
     * Returns a fully loaded TranslationPack.
     * Lazy loads meta + language file if necessary.
     */
    fun pack(pack: String, language: Language): TranslationPack {
        val cacheKey = cacheKey(pack, language)

        return packCache.computeIfAbsent(cacheKey) {
            loadPackInternal(pack, language)
        }
    }

    /**
     * Returns translation directly.
     */
    fun translate(pack: String, language: Language, key: String): String {
        val translationPack = pack(pack, language)

        return translationPack.get(key) ?: fallback(translationPack, key)
    }

    fun translate(pack: String, language: Language, key: String, vararg placeholders: Pair<String, Any?>): String {
        val raw = translate(pack, language, key)

        if (placeholders.isEmpty()) {
            return raw
        }

        return PlaceholderFormatter.format(raw, placeholders.toMap())
    }
    fun availableLanguages(pack: String): Set<Language> {
        val meta = loader.loadMeta(pack)

        metaCache[pack] = meta
        return meta.languages
    }

    private fun loadPackInternal(pack: String, language: Language): TranslationPack {
        val meta = loader.loadMeta(pack)
        metaCache[pack] = meta

        return loader.loadPack(meta, language)
    }

    private fun fallback(pack: TranslationPack, key: String): String {
        val defaultLanguage = pack.meta.defaultLanguage

        if (pack.language == defaultLanguage) {
            logger.warn(
                "Missing translation key '{}' in pack '{}' for language '{}'",
                key,
                pack.meta.name,
                pack.language.code
            )
        }

        val fallbackPack = pack(pack.meta.name, defaultLanguage)
        val translation = fallbackPack.get(key)
        if (translation != null) {
            return translation
        }

        logger.warn(
            "Missing translation key '{}' in pack '{}' for language '{}' (including fallback '{}')",
            key,
            pack.meta.name,
            pack.language.code,
            defaultLanguage.code
        )

        return key
    }

    private fun cacheKey(pack: String, language: Language): String = "$pack:${language.code}"
}