package dev.httpmarco.polocloud.i18n.loader.parser

import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Properties

internal object PropertiesParser {

    fun parse(inputStream: InputStream): Map<String, String> {
        val properties = Properties()

        inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            properties.load(reader)
        }

        return properties.entries.associate { entry ->
            entry.key.toString() to entry.value.toString()
        }
    }
}