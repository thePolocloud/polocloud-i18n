package dev.httpmarco.polocloud.i18n.loader

import dev.httpmarco.polocloud.i18n.model.Language
import dev.httpmarco.polocloud.i18n.model.TranslationPack
import dev.httpmarco.polocloud.i18n.model.TranslationPackMeta

interface TranslationLoader {

    fun loadMeta(pack: String): TranslationPackMeta

    fun loadPack(meta: TranslationPackMeta, language: Language): TranslationPack
}