package com.example.securednotepad

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val hashPassword = HashPassword(this)

        val passwordText = findViewById<EditText>(R.id.passwordTextLogin)
        passwordText.text.clear()

        // check if the introduced password hashed is the same as the stored one
        val buttonLogin = findViewById<Button>(R.id.buttonLogIn)
        buttonLogin.setOnClickListener {
            val actualPassword = passwordText.text.toString().trim()

            if (actualPassword.isEmpty()) {
                Toast.makeText(this, "Field empty", Toast.LENGTH_SHORT).show()
            }

            else{

                if(hashPassword.checkPassword(actualPassword)){
                    startActivity(Intent(this, Notepad::class.java))
                }
                else{
                    Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show()
                }
            }

        }

        val buttonForget = findViewById<Button>(R.id.forgetButton)
        buttonForget.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }
}