package com.example.fundo.ui.newnote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fundo.databinding.NewNoteBinding

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: NewNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = NewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachListeners()
    }

    private fun attachListeners() {
        binding.saveNoteFab.setOnClickListener{
            val title = binding.titleTextEdit.text.toString()
            val content = binding.contentTextEdit.text.toString()

            val intent = Intent()
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
