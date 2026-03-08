package de.polocloud.i18n.loader

import de.polocloud.i18n.model.TranslationPack
import de.polocloud.i18n.model.TranslationPackMeta
import java.util.Locale

interface TranslationLoader {

    fun loadMeta(pack: String): TranslationPackMeta

    fun loadPack(meta: TranslationPackMeta, language: Locale): TranslationPack
}
