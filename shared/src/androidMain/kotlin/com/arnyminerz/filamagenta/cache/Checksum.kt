package com.arnyminerz.filamagenta.cache

import java.math.BigInteger
import java.security.MessageDigest

object Checksum {
    private const val RADIX_HEX = 16
    private const val MD5_HASH_LENGTH = 32

    fun getMD5EncryptedString(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(bytes, 0, bytes.size)
        return BigInteger(1, digest.digest())
            .toString(RADIX_HEX)
            .padStart(MD5_HASH_LENGTH, '0')
    }
}
