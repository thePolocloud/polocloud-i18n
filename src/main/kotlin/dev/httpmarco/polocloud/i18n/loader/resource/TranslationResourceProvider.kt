package dev.httpmarco.polocloud.i18n.loader.resource

import java.io.InputStream

interface TranslationResourceProvider {

    fun openMeta(pack: String): InputStream

    fun openLanguageFile(pack: String, language: String): InputStream
}