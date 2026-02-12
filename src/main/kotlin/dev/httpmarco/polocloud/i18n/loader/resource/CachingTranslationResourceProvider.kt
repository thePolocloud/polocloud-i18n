package dev.httpmarco.polocloud.i18n.loader.resource

import com.google.gson.Gson
import dev.httpmarco.polocloud.i18n.loader.parser.MetaDto
import java.io.File
import java.io.InputStream

class CachingTranslationResourceProvider(
    private val cacheDirectory: File,
    private val remoteProvider: TranslationResourceProvider
) : TranslationResourceProvider {

    private val gson = Gson()

    init {
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs()
        }
    }

    override fun openMeta(pack: String): InputStream {
        val packDir = File(cacheDirectory, pack)
        val localMetaFile = File(packDir, "pack.json")

        val remoteMetaContent = remoteProvider.openMeta(pack)
            .bufferedReader()
            .use { it.readText() }

        val remoteVersion = extractVersion(remoteMetaContent)

        if (!localMetaFile.exists()) {
            savePackMeta(packDir, localMetaFile, remoteMetaContent)
        } else {
            val localVersion = extractVersion(localMetaFile.readText())

            if (localVersion != remoteVersion) { //TODO better version check
                packDir.deleteRecursively()
                packDir.mkdirs()
                savePackMeta(packDir, localMetaFile, remoteMetaContent)
            }
        }

        return localMetaFile.inputStream()
    }

    override fun openLanguageFile(pack: String, language: String): InputStream {
        val packDir = File(cacheDirectory, pack)
        val file = File(packDir, "$language.properties")

        if (!file.exists()) {
            remoteProvider
                .openLanguageFile(pack, language)
                .use { input ->
                    packDir.mkdirs()
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
        }

        return file.inputStream()
    }

    private fun savePackMeta(packDir: File, metaFile: File, content: String) {
        packDir.mkdirs()
        metaFile.writeText(content)
    }

    private fun extractVersion(json: String): String {
        val dto = gson.fromJson(json, MetaDto::class.java)
        return dto.version
    }
}