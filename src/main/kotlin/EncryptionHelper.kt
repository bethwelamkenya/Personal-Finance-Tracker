package org.example

import java.io.File
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import javax.crypto.SecretKey

class EncryptionHelper {

    // Generates a new AES key (128 bits by default)
//    fun generateAESKey(): Key {
//        val keyGenerator = KeyGenerator.getInstance("AES")
//        keyGenerator.init(128) // You can choose 128, 192, or 256 bits
//        return keyGenerator.generateKey()
//    }

    // Encrypts a plain text using AES encryption
    fun encryptText(text: String, key: Key): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    // Decrypts an encrypted text using AES decryption
    fun decryptText(encryptedText: String, key: Key): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decodedBytes = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }

    // AES key as a String (usually stored in secure places like environment variables)
//    fun getKeyFromString(keyString: String): Key {
//        return SecretKeySpec(keyString.toByteArray(), "AES")
//    }


    fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128) // 128-bit AES key
        return keyGen.generateKey()
    }

    fun getKeyFromString(keyString: String): SecretKey {
        val decodedKey = Base64.getDecoder().decode(keyString)
        return SecretKeySpec(decodedKey, "AES")
    }

    fun saveKeyToFile(key: Key, filePath: String) {
        val encodedKey = Base64.getEncoder().encodeToString(key.encoded)
        File(filePath).writeText(encodedKey)
    }


    fun loadKeyFromFile(filePath: String): Key? {
        val keyData = File(filePath).readText()
        return try {
            val decodedKey = Base64.getDecoder().decode(keyData)
            SecretKeySpec(decodedKey, "AES")
        } catch (e: Exception) {
            println("Error loading key: ${e.message}")
            null
        }
    }
}
