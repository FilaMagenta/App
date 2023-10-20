package com.arnyminerz.filamagenta.sound

actual object SoundPlayer {
    /**
     * Plays the sound stored in a file in resources at [path]
     */
    actual suspend fun playFromResources(path: String) {
        // TODO - currently not able to play sounds in iOS
    }
}
