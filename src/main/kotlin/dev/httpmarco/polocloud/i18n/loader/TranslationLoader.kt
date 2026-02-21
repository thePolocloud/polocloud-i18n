package dev.httpmarco.polocloud.i18n.loader

import dev.httpmarco.polocloud.i18n.model.TranslationPack
import dev.httpmarco.polocloud.i18n.model.TranslationPackMeta
import java.util.Locale

interface TranslationLoader {

    fun loadMeta(pack: String): TranslationPackMeta

    fun loadPack(meta: TranslationPackMeta, language: Locale): TranslationPack
}
