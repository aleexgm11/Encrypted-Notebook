package com.example.securednotepad

import android.content.Context
import android.content.SharedPreferences
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class HashPassword (private val context: Context) {

    companion object {
        private const val PREF_NAME = "password"
        private const val KEY_HASHED_PASSWORD = "hashedPassword"
        private const val KEY_SALT = "salt"
        private const val algorithm = "PBKDF2WithHmacSHA256"
        private const val iterations = 1000
        private const val keyLength = 256
    }

    private fun ByteArray.toHexString(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        val byteIterator = chunkedSequence(2)
            .map { it.toInt(16).toByte() }
            .iterator()

        return ByteArray(length / 2) { byteIterator.next() }
    }

    private fun generateSalt() : ByteArray{
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    fun hashAndSavePassword(password: String) {
        try {
            val salt = generateSalt()
            val hashedPassword = hash(password, salt)
            saveHashedPassword(hashedPassword)
            saveSalt(salt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkPassword(password: String): Boolean {
        val savedHashedPassword = getSavedHashedPassword() ?: return false
        val savedSalt = getSavedSalt() ?: return false

        return try {
            val hashedPassword = hash(password, savedSalt.decodeHex())
            val newPass = hashedPassword.toHexString()
            savedHashedPassword == newPass
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun saveHashedPassword(hashedPassword: ByteArray) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()

        val sb = StringBuilder()
        for (b in hashedPassword) {
            sb.append(String.format("%02x", b))
        }
        editor.putString(KEY_HASHED_PASSWORD, sb.toString())
        editor.apply()
    }

    private fun saveSalt(salt: ByteArray) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()

        val sb = StringBuilder()
        for (b in salt) {
            sb.append(String.format("%02x", b))
        }
        editor.putString(KEY_SALT, sb.toString())
        editor.apply()
    }

    private fun getSavedHashedPassword(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_HASHED_PASSWORD, null)
    }

    private fun getSavedSalt(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_SALT, null)
    }

    private fun hash(password: String, salt : ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        return try {
            val skf = SecretKeyFactory.getInstance(algorithm)
            skf.generateSecret(spec).encoded
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError("Error while hashing a password: " + e.message, e)
        } catch (e: Exception) {
            println(e.message)
            ByteArray(16)
        } finally {
            spec.clearPassword()
        }
    }


}
