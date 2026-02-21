package dev.httpmarco.polocloud.i18n.model

import java.util.Locale

/**
 * Helper object for working with [Locale] instances in the i18n system.
 *
 * Provides utilities to parse language codes (e.g. "en_US", "de_DE") into [Locale] objects
 * and to extract language codes from [Locale] instances.
 *
 * @see Locale
 */
object Language {

    /**
     * Creates a [Locale] from a language code string.
     *
     * Supports the following formats:
     * - `"en"` - language only
     * - `"en_US"` - language and country
     * - `"en_US_WIN"` - language, country, and variant
     *
     * @param code The language code string (e.g. "en_US", "de_DE")
     * @return A [Locale] instance representing the given language code
     *
     * @throws IllegalArgumentException if the code format is invalid
     */
    fun of(code: String): Locale {
        val parts = code.split("_")
        return when (parts.size) {
            1 -> Locale.of(parts[0])
            2 -> Locale.of(parts[0], parts[1])
            else -> Locale.of(parts[0], parts[1], parts[2])
        }
    }

    /**
     * Extracts the language code from a [Locale] instance.
     *
     * Converts the locale to a BCP 47 language tag and replaces hyphens with underscores.
     * For example, `Locale.US` becomes `"en_US"`.
     *
     * @param locale The [Locale] to convert
     * @return The language code string (e.g. "en_US", "de_DE")
     *
     * @see Locale.toLanguageTag
     */
    fun code(locale: Locale): String = locale.toLanguageTag().replace("-", "_")
}
