package dev.httpmarco.polocloud.i18n.core

import dev.httpmarco.polocloud.i18n.core.format.PlaceholderFormatter
import dev.httpmarco.polocloud.i18n.loader.TranslationLoader
import dev.httpmarco.polocloud.i18n.model.Language
import dev.httpmarco.polocloud.i18n.model.TranslationPack
import dev.httpmarco.polocloud.i18n.model.TranslationPackMeta
import java.util.concurrent.ConcurrentHashMap

class TranslationManager(private val loader: TranslationLoader) {

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

        return translationPack.get(key) ?: fallbackOrThrow(translationPack, key)
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

    private fun fallbackOrThrow(pack: TranslationPack, key: String): String {
        val defaultLanguage = pack.meta.defaultLanguage

        if (pack.language == defaultLanguage) {
            error(
                "Missing translation key '$key' in pack '${pack.meta.name}' for language '${pack.language.code}'"
            )
        }

        val fallbackPack = pack(pack.meta.name, defaultLanguage)

        return fallbackPack.get(key) ?: error(
            "Missing translation key '$key' in pack '${pack.meta.name}' (including fallback '${defaultLanguage.code}')"
        )
    }

    private fun cacheKey(pack: String, language: Language): String = "$pack:${language.code}"
}