package org.example

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

class Encrypting {
    companion object {
        fun encrypt(data: String, key: String): String {
            val secretKey = SecretKeySpec(key.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray()))
        }

        fun decrypt(encryptedData: String, key: String): String {
            val secretKey = SecretKeySpec(key.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)))
        }
    }
}