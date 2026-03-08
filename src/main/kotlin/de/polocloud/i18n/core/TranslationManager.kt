package de.polocloud.i18n.core

import de.polocloud.i18n.core.format.PlaceholderFormatter
import de.polocloud.i18n.loader.TranslationLoader
import de.polocloud.i18n.model.Language
import de.polocloud.i18n.model.TranslationPack
import de.polocloud.i18n.model.TranslationPackMeta
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class TranslationManager(private val loader: de.polocloud.i18n.loader.TranslationLoader) {

    private val logger: Logger = LogManager.getLogger(TranslationManager::class.java)
    private val metaCache = ConcurrentHashMap<String, de.polocloud.i18n.model.TranslationPackMeta>()
    private val packCache = ConcurrentHashMap<String, de.polocloud.i18n.model.TranslationPack>()

    /**
     * Returns a fully loaded TranslationPack.
     * Lazy loads meta + language file if necessary.
     */
    fun pack(pack: String, language: Locale): de.polocloud.i18n.model.TranslationPack {
        val cacheKey = cacheKey(pack, language)

        return packCache.computeIfAbsent(cacheKey) {
            loadPackInternal(pack, language)
        }
    }

    /**
     * Returns translation directly.
     */
    fun translate(pack: String, language: Locale, key: String): String {
        val translationPack = pack(pack, language)

        return translationPack.get(key) ?: fallback(translationPack, key)
    }

    fun translate(pack: String, language: Locale, key: String, vararg placeholders: Pair<String, Any?>): String {
        val raw = translate(pack, language, key)

        if (placeholders.isEmpty()) {
            return raw
        }

        return _root_ide_package_.de.polocloud.i18n.core.format.PlaceholderFormatter.format(raw, placeholders.toMap())
    }

    fun availableLanguages(pack: String): Set<Locale> {
        val meta = loader.loadMeta(pack)

        metaCache[pack] = meta
        return meta.languages
    }

    private fun loadPackInternal(pack: String, language: Locale): de.polocloud.i18n.model.TranslationPack {
        val meta = loader.loadMeta(pack)
        metaCache[pack] = meta

        return loader.loadPack(meta, language)
    }

    private fun fallback(pack: de.polocloud.i18n.model.TranslationPack, key: String): String {
        val defaultLanguage = pack.meta.defaultLanguage

        if (pack.language == defaultLanguage) {
            logger.warn(
                "Missing translation key '{}' in pack '{}' for language '{}'",
                key,
                pack.meta.name,
                _root_ide_package_.de.polocloud.i18n.model.Language.code(pack.language)
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
            _root_ide_package_.de.polocloud.i18n.model.Language.code(pack.language),
            _root_ide_package_.de.polocloud.i18n.model.Language.code(defaultLanguage)
        )

        return key
    }

    private fun cacheKey(pack: String, language: Locale): String = "$pack:${_root_ide_package_.de.polocloud.i18n.model.Language.code(language)}"
}
