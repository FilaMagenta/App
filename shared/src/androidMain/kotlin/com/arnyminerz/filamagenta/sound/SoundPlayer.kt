package com.arnyminerz.filamagenta.sound

import android.media.MediaPlayer
import io.github.aakira.napier.Napier
import java.io.IOException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
actual object SoundPlayer {
    private const val VOLUME = 100f

    private val mediaPlayer = MediaPlayer()

    init {
        mediaPlayer.setVolume(VOLUME, VOLUME)
        mediaPlayer.isLooping = false
        mediaPlayer.setOnPreparedListener {
            it.start()
        }
        mediaPlayer.setOnCompletionListener {
            it.stop()
            it.release()
        }
    }

    /**
     * Plays the sound stored in a file in resources at [path]
     */
    actual fun playFromResources(path: String) {
        try {
            val base64 = this::class.java.getResourceAsStream(path)?.use {
                Base64.encode(it.readBytes())
            }
            val url = "data:audio/wav;base64,$base64"
            Napier.v("Playing sound from resources: $path")
            mediaPlayer.apply {
                setDataSource(url)
                prepareAsync()
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
