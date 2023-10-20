package com.arnyminerz.filamagenta.sound

expect object SoundPlayer {
    /**
     * Plays the sound stored in a file in resources at [path]
     */
    suspend fun playFromResources(path: String)
}
