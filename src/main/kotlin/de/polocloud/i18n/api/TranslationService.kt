package de.polocloud.i18n.api

import de.polocloud.i18n.core.TranslationManager
import de.polocloud.i18n.loader.DefaultTranslationLoader
import de.polocloud.i18n.loader.resource.CachingTranslationResourceProvider
import de.polocloud.i18n.loader.resource.HttpTranslationResourceProvider
import de.polocloud.i18n.model.Language
import java.io.File
import java.util.Locale
import java.util.concurrent.Executors

object TranslationService {

    private lateinit var manager: de.polocloud.i18n.core.TranslationManager
    private var defaultLanguage: Locale = Locale.ENGLISH
    private val preloadExecutor = Executors.newFixedThreadPool(2).also { executor ->
        Runtime.getRuntime().addShutdownHook(
            Thread { executor.shutdown() }
        )
    }

    fun init(baseUrl: String = "https://raw.githubusercontent.com/thePolocloud/polocloud-translations/refs/heads/main", cacheDir: File = File(".translations")) {
        val httpProvider = _root_ide_package_.de.polocloud.i18n.loader.resource.HttpTranslationResourceProvider(baseUrl)
        val cachingProvider = _root_ide_package_.de.polocloud.i18n.loader.resource.CachingTranslationResourceProvider(
            cacheDir,
            httpProvider
        )

        val loader = _root_ide_package_.de.polocloud.i18n.loader.DefaultTranslationLoader(cachingProvider)

        manager = _root_ide_package_.de.polocloud.i18n.core.TranslationManager(loader)
    }

    fun tr(pack: String, language: String, key: String, vararg placeholders: Pair<String, Any?>): String {
        checkInitialized()

        return manager.translate(
            pack = pack,
            language = _root_ide_package_.de.polocloud.i18n.model.Language.of(language),
            key = key,
            placeholders = placeholders
        )
    }

    fun tr(pack: String, language: Locale, key: String, vararg placeholders: Pair<String, Any?>): String {
        checkInitialized()

        return manager.translate(
            pack = pack,
            language = language,
            key = key,
            placeholders = placeholders
        )
    }

    fun tr(pack: String, key: String, vararg placeholders: Pair<String, Any?>): String {
        checkInitialized()

        return manager.translate(
            pack = pack,
            language = defaultLanguage,
            key = key,
            placeholders = placeholders
        )
    }

    fun preload(pack: String, language: String) {
        checkInitialized()
        manager.pack(pack, _root_ide_package_.de.polocloud.i18n.model.Language.of(language))
    }

    fun preload(pack: String, language: Locale = defaultLanguage) {
        checkInitialized()
        manager.pack(pack, language)
    }

    fun preloadAsync(pack: String, language: String) {
        preloadExecutor.submit {
            try {
                preload(pack, language)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun preloadAsync(pack: String, language: Locale = defaultLanguage) {
        preloadExecutor.submit {
            try {
                preload(pack, language)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun pack(pack: String): PackAccessor {
        checkInitialized()
        return PackAccessor(pack)
    }

    fun defaultLanguage(language: String) {
        defaultLanguage = _root_ide_package_.de.polocloud.i18n.model.Language.of(language)
    }

    fun defaultLanguage(language: Locale) {
        defaultLanguage = language
    }

    fun availableLanguages(pack: String): Set<String> {
        checkInitialized()

        return manager
            .availableLanguages(pack)
            .map { _root_ide_package_.de.polocloud.i18n.model.Language.code(it) }
            .toSet()
    }

    private fun checkInitialized() {
        check(::manager.isInitialized) {
            "TranslationService is not initialized. Call init() first."
        }
    }

    class PackAccessor internal constructor(private val pack: String) {
        fun language(language: String): LanguageAccessor {
            return LanguageAccessor(pack, _root_ide_package_.de.polocloud.i18n.model.Language.of(language))
        }

        fun language(language: Locale): LanguageAccessor {
            return LanguageAccessor(pack, language)
        }
    }

    class LanguageAccessor(private val pack: String, private val language: Locale) {
        fun get(key: String): String {
            return manager.translate(pack, language, key)
        }

        fun format(key: String, vararg placeholders: Pair<String, Any?>): String {
            return manager.translate(pack, language, key, *placeholders)
        }
    }
}
