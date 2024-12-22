package com.example.securednotepad

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class EncryptManager(context: Context) {

    companion object{
        private const val ALGORITHM = "AES/CBC/PKCS5Padding"
        private const val PREF_NAME = "password"
        private const val KEY_HASHED_PASSWORD = "hashedPassword"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun encrypt(note: String): String {
        val iv = generateIV()
        val salt = generateSalt()

        val key = obtainSecretKey(salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)

        val encryptedNote = cipher.doFinal(note.toByteArray())

        val ivString = Base64.encodeToString(iv.iv, Base64.DEFAULT)
        val saltString = Base64.encodeToString(salt, Base64.DEFAULT)
        sharedPreferences.edit().putString("iv", ivString).apply()
        sharedPreferences.edit().putString("keySalt", saltString).apply()

        return Base64.encodeToString(encryptedNote, Base64.DEFAULT)
    }

    fun decrypt(note: String): String {
        val ivString = sharedPreferences.getString("iv", "")
        val saltString = sharedPreferences.getString("keySalt", "")
        val iv = IvParameterSpec(Base64.decode(ivString, Base64.DEFAULT))

        val key = obtainSecretKey(Base64.decode(saltString, Base64.DEFAULT))
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)

        val notaBytes = cipher.doFinal(Base64.decode(note, Base64.DEFAULT))

        return String(notaBytes)
    }

    private fun obtainSecretKey(salt : ByteArray): SecretKey {
        val password = sharedPreferences.getString(KEY_HASHED_PASSWORD, "")?.toCharArray()

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password, salt, 1000, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun generateIV(): IvParameterSpec {
        val random = SecureRandom()
        val iv = ByteArray(16)
        random.nextBytes(iv)
        return IvParameterSpec(iv)
    }

    private fun generateSalt() : ByteArray{
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

}