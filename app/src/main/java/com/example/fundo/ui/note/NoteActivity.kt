package com.example.fundo.ui.note

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fundo.databinding.NewNoteBinding
import com.google.firebase.database.core.utilities.Utilities

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: NewNoteBinding
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = NewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadDataFromIntent()
        attachListeners()
    }

    private fun loadDataFromIntent() {
        val intent = intent
        id = intent.extras?.getString("id").toString()
        val title = intent.extras?.getString("title")
        val content = intent.extras?.getString("content")

        binding.titleTextEdit.setText(title)
        binding.contentTextEdit.setText(content)
    }

    private fun attachListeners() {
        binding.saveNoteFab.setOnClickListener{
            val title = binding.titleTextEdit.text.toString()
            val content = binding.contentTextEdit.text.toString()

            val intent = Intent()
            intent.putExtra("id",id)
            intent.putExtra("title",title)
            intent.putExtra("content",content)

            setResult(RESULT_OK,intent)
            finish()
        }

        binding.backButton.setOnClickListener{
            finish()
        }
    }
}
