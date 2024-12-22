package com.example.securednotepad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val hashPassword = HashPassword(this)

        // check if all the text fields are filled, both has the same password and has the minimum length
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            val newPasswordText = findViewById<EditText>(R.id.passwordTextRegister)
            val repeatNewPasswordText = findViewById<EditText>(R.id.passTextRegisterRepeat)

            val newPassword = newPasswordText.text.toString().trim()
            val repeatNewPassword = repeatNewPasswordText.text.toString().trim()

            if(newPassword != repeatNewPassword)
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()

            if(isPasswordAcceptable(newPassword) && isPasswordAcceptable(repeatNewPassword)){

                if(newPassword == repeatNewPassword){
                    hashPassword.hashAndSavePassword(newPassword)
                    val sharedPreferences = this.getSharedPreferences("note", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("note")
                    editor.apply()
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
