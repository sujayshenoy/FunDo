package com.example.fundo.ui.note

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fundo.R
import com.example.fundo.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private var id: Long? = 0
    private var archived = false
    private var noteTitle = ""
    private var content = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadDataFromIntent()
        attachListeners()
    }

    private fun loadDataFromIntent() {
        val intent = intent
        id = intent.extras?.getLong("id")
        noteTitle = intent.extras?.getString("title") ?: ""
        content = intent.extras?.getString("content") ?: ""
        archived = intent.extras?.getBoolean("archived") ?: false

        if (noteTitle.isNotEmpty() || content.isNotEmpty()) {
            binding.titleTextEdit.setText(title)
            binding.contentTextEdit.setText(content)
            binding.deleteButton.visibility = View.VISIBLE
            if (archived) {
                binding.archiveButton.setImageDrawable(getDrawable(R.drawable.button_unarchive))
                binding.archiveButton.tag = "unarchive"
            }
            binding.archiveButton.visibility = View.VISIBLE
        }
    }

    private fun attachListeners() {
        binding.saveNoteFab.setOnClickListener {
            noteTitle = binding.titleTextEdit.text.toString()
            content = binding.contentTextEdit.text.toString()
            returnData()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.deleteButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("id", id)

            setResult(DELETE_NOTE, intent)
            finish()
        }

        binding.archiveButton.setOnClickListener {
            if (it.tag == "archive") {
                archived = true
                returnData()
            } else {
                archived = false
                returnData()
            }
        }
    }

    private fun returnData() {
        val intent = Intent()
        intent.putExtra("id", id)
        intent.putExtra("title", noteTitle)
        intent.putExtra("content", content)
        intent.putExtra("archived", archived)

        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val DELETE_NOTE = 0
    }
}
