package com.example.securednotepad

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class Notepad : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        val encryptManager = EncryptManager(this)

        val sp : SharedPreferences = this.getSharedPreferences("note", MODE_PRIVATE)
        val noteString = sp.getString("note", "").toString()
        var realNote = ""
        if(noteString != ""){
            realNote = encryptManager.decrypt(noteString)
        }

        val notepadText =  findViewById<EditText>(R.id.changeNoteText)
        notepadText.setText(realNote)

        // save the new note and starts the login again
        val saveBtn = findViewById<Button>(R.id.btnGoBack)
        saveBtn.setOnClickListener {

            val note = notepadText.text.toString()
            val encryptedNote = encryptManager.encrypt(note)

            val e : SharedPreferences.Editor = sp.edit()
            e.putString("note", encryptedNote)
            e.apply()

            startActivity(Intent(this, Login::class.java))
        }

        // also save the note but starts the ChangePassword Activity
        val resetBtn = findViewById<Button>(R.id.buttonReset)
        resetBtn.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
        }
    }
}