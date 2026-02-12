package dev.httpmarco.polocloud.i18n.loader.resource

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI

class HttpTranslationResourceProvider(private val baseUrl: String) : TranslationResourceProvider {

    private val logger: Logger = LogManager.getLogger(HttpTranslationResourceProvider::class.java)

    override fun openMeta(pack: String): InputStream {
        return open("$pack/pack.json")
    }

    override fun openLanguageFile(pack: String, language: String): InputStream {
        return open("$pack/$language.properties")
    }

    private fun open(path: String): InputStream {
        val uri = URI("$baseUrl/$path")
        val connection = uri.toURL().openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        if (connection.responseCode != 200) {
            logger.error("Failed to download translation resource: {} (HTTP {})", uri, connection.responseCode)
        }

        return connection.inputStream
    }
}