package com.example.securednotepad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ChangePassword : AppCompatActivity() {

    companion object{
        private const val NOTE = "note"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val encryptManager = EncryptManager(this)
        val hashPassword = HashPassword(this)

        val sharedPreferencesNote = this.getSharedPreferences(NOTE, Context.MODE_PRIVATE)
        val note = sharedPreferencesNote.getString(NOTE, "").orEmpty()
        val decryptedNote = encryptManager.decrypt(note)

        // check if all the text fields are filled, both has the same password and has the minimum length
        val btnChangePassword = findViewById<Button>(R.id.buttonChangePassword)
        btnChangePassword.setOnClickListener {
            val newPasswordText = findViewById<EditText>(R.id.passwordTextRegister)
            val repeatNewPasswordText = findViewById<EditText>(R.id.passTextRegisterRepeat)

            val newPassword = newPasswordText.text.toString().trim()
            val repeatNewPassword = repeatNewPasswordText.text.toString().trim()

            if(newPassword != repeatNewPassword)
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()

            if(isPasswordAcceptable(newPassword) && isPasswordAcceptable(repeatNewPassword)){

                if(newPassword == repeatNewPassword){

                    hashPassword.hashAndSavePassword(newPassword)
                    val newEncryptedNote = encryptManager.encrypt(decryptedNote)
                    sharedPreferencesNote.edit().putString(NOTE, newEncryptedNote).apply()
                    startActivity(Intent(this, Login::class.java))
                }

            }

            else{
                newPasswordText.background = ContextCompat.getDrawable(this, R.drawable.custom_error)
                newPasswordText.requestFocus()
                newPasswordText.text.clear()
                repeatNewPasswordText.text.clear()
                newPasswordText.hint = "Minimum of 12 characters"
            }
        }
    }

    private fun isPasswordAcceptable(password : String): Boolean {
        return password.length >= 12
    }
}