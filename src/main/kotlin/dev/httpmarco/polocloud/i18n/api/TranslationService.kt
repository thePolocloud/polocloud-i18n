package dev.httpmarco.polocloud.i18n.api

import dev.httpmarco.polocloud.i18n.core.TranslationManager
import dev.httpmarco.polocloud.i18n.loader.DefaultTranslationLoader
import dev.httpmarco.polocloud.i18n.loader.resource.CachingTranslationResourceProvider
import dev.httpmarco.polocloud.i18n.loader.resource.HttpTranslationResourceProvider
import dev.httpmarco.polocloud.i18n.model.Language
import java.io.File
import java.util.concurrent.Executors

object TranslationService {

    private lateinit var manager: TranslationManager
    private var defaultLanguage: Language = Language("en_US")
    private val preloadExecutor = Executors.newFixedThreadPool(2).also { executor ->
        Runtime.getRuntime().addShutdownHook(
            Thread { executor.shutdown() }
        )
    }

    fun init(baseUrl: String = "https://raw.githubusercontent.com/thePolocloud/polocloud-translations/refs/heads/main", cacheDir: File = File(".translations")) {
        val httpProvider = HttpTranslationResourceProvider(baseUrl)
        val cachingProvider = CachingTranslationResourceProvider(cacheDir, httpProvider)

        val loader = DefaultTranslationLoader(cachingProvider)

        manager = TranslationManager(loader)
    }

    fun tr(pack: String, language: String, key: String, vararg placeholders: Pair<String, Any?>): String {
        checkInitialized()

        return manager.translate(
            pack = pack,
            language = Language(language),
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

    fun preload(pack: String, language: Language = defaultLanguage) {
        checkInitialized()
        manager.pack(pack, language)
    }

    fun preloadAsync(pack: String, language: Language = defaultLanguage) {
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
        defaultLanguage = Language(language)
    }

    fun availableLanguages(pack: String): Set<String> {
        checkInitialized()

        return manager
            .availableLanguages(pack)
            .map { it.code }
            .toSet()
    }

    private fun checkInitialized() {
        check(::manager.isInitialized) {
            "TranslationService is not initialized. Call init() first."
        }
    }

    class PackAccessor internal constructor(private val pack: String) {
        fun language(language: String): LanguageAccessor {
            return LanguageAccessor(pack, Language(language))
        }
    }

    class LanguageAccessor(private val pack: String, private val language: Language) {
        fun get(key: String): String {
            return manager.translate(pack, language, key)
        }

        fun format(key: String, vararg placeholders: Pair<String, Any?>): String {
            return manager.translate(pack, language, key, *placeholders)
        }
    }
}