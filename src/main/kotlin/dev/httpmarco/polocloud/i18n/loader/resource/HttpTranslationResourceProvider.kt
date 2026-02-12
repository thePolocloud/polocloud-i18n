package dev.httpmarco.polocloud.i18n.loader.resource

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI

class HttpTranslationResourceProvider(private val baseUrl: String) : TranslationResourceProvider {

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
            error("Failed to download translation resource: $uri (HTTP ${connection.responseCode})")
        }

        return connection.inputStream
    }
}