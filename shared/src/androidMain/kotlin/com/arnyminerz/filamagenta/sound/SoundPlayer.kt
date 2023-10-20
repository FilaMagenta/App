package com.arnyminerz.filamagenta.sound

import android.media.MediaPlayer
import com.arnyminerz.filamagenta.cache.Checksum
import io.github.aakira.napier.Napier
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

actual object SoundPlayer {
    private const val VOLUME = 100f

    private var cache: File? = null

    /**
     * Sets the directory where all audio files will be stored after being extracted from resources.
     */
    fun setCacheDirectory(file: File) {
        if (!file.exists() || !file.isDirectory) {
            if (!file.mkdirs()) {
                throw IOException("Could not create cache directory.")
            }
        }
        cache = file
    }

    /**
     * Fetches the local cached file if it already exists, or retrieves it from resources and caches it otherwise.
     *
     * @throws IllegalStateException If [setCacheDirectory] has not been called.
     * @throws FileNotFoundException If there isn't any file located at [resourcePath].
     */
    private fun localFileCache(resourcePath: String): File {
        check(cache != null) { "setCacheDirectory has not been called." }

        val sourceBytes = SoundPlayer::class.java.classLoader?.getResourceAsStream(resourcePath)?.use { it.readBytes() }
            ?: throw FileNotFoundException("Could not find a file at $resourcePath in resources.")

        val file = File(cache, resourcePath.replace('/', '_'))
        if (file.exists()) {
            val fileBytes = file.inputStream().use { it.readBytes() }
            val fileMd5 = Checksum.getMD5EncryptedString(fileBytes)
            val sourceMd5 = Checksum.getMD5EncryptedString(sourceBytes)
            if (fileMd5 == sourceMd5) {
                // Checksum match, return the cached file
                return file
            }
            file.delete()
        }

        file.outputStream().use { output -> output.write(sourceBytes) }

        return file
    }

    /**
     * Plays the sound stored in a file in resources at [path]
     */
    actual suspend fun playFromResources(path: String) {
        try {
            val file = localFileCache(path)
            Napier.v("Playing sound from resources: $path")
            MediaPlayer().apply {
                setOnCompletionListener {
                    it.stop()
                    it.reset()
                    it.release()
                }

                setVolume(VOLUME, VOLUME)
                isLooping = false

                setDataSource(file.absolutePath)
                prepare()
                start()
            }
        } catch (e: IllegalArgumentException) {
            Napier.e("Could not play sound from resource ($path)", e)
        } catch (e: SecurityException) {
            Napier.e("Could not play sound from resource ($path)", e)
        } catch (e: IllegalStateException) {
            Napier.e("Could not play sound from resource ($path)", e)
        } catch (e: IOException) {
            Napier.e("Could not play sound from resource ($path)", e)
        }
    }
}
