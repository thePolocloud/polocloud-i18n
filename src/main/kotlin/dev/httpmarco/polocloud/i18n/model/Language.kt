package dev.httpmarco.polocloud.i18n.model

@JvmInline
value class Language(val code: String) {
    override fun toString(): String = code
}