package de.polocloud.i18n.loader

import de.polocloud.i18n.model.TranslationPack
import de.polocloud.i18n.model.TranslationPackMeta
import java.util.Locale

interface TranslationLoader {

    fun loadMeta(pack: String): de.polocloud.i18n.model.TranslationPackMeta

    fun loadPack(meta: de.polocloud.i18n.model.TranslationPackMeta, language: Locale): de.polocloud.i18n.model.TranslationPack
}
