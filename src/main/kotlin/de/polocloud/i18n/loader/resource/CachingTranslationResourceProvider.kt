package de.polocloud.i18n.loader.resource

import com.google.gson.Gson
import de.polocloud.i18n.loader.parser.MetaDto
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.attribute.DosFileAttributeView

class CachingTranslationResourceProvider(
    private val cacheDirectory: File,
    private val remoteProvider: de.polocloud.i18n.loader.resource.TranslationResourceProvider
) : de.polocloud.i18n.loader.resource.TranslationResourceProvider {

    private val gson = Gson()

    init {
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs()
        }

        if (!cacheDirectory.name.startsWith(".")) {
            Files.getFileAttributeView(cacheDirectory.toPath(), DosFileAttributeView::class.java)?.setHidden(true)
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

            if (isNewer(remoteVersion, localVersion)) {
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
        val dto = gson.fromJson(json, _root_ide_package_.de.polocloud.i18n.loader.parser.MetaDto::class.java)
        return dto.version
    }

    private fun isNewer(remoteVersion: String, localVersion: String): Boolean {
        val remote = remoteVersion.split(".").map { it.toIntOrNull() ?: 0 }
        val local = localVersion.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0..2) {
            val diff = (remote.getOrElse(i) { 0 }).compareTo(local.getOrElse(i) { 0 })
            if (diff != 0) return diff > 0
        }
        return false
    }
}