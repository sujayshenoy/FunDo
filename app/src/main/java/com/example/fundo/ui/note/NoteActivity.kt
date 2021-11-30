package com.example.fundo.ui.note

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.fundo.R
import com.example.fundo.common.Logger
import com.example.fundo.databinding.ActivityNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private var id: Long? = 0
    private var archived = false
    private var noteTitle = ""
    private var content = ""
    private var reminder: Date? = null

    @RequiresApi(Build.VERSION_CODES.O)
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
        reminder = intent.extras?.getSerializable("reminder") as? Date

        if (noteTitle.isNotEmpty() || content.isNotEmpty()) {
            binding.titleTextEdit.setText(noteTitle)
            binding.contentTextEdit.setText(content)
            binding.deleteButton.visibility = View.VISIBLE
            binding.reminderButton.visibility = View.VISIBLE
            binding.archiveButton.visibility = View.VISIBLE
            if (archived) {
                binding.archiveButton.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@NoteActivity,
                        R.drawable.button_unarchive
                    )
                )
                binding.archiveButton.tag = "unarchive"
            }

            reminder?.let {
                binding.reminderLayout.visibility = View.VISIBLE
                val formatter = SimpleDateFormat("dd MMM, hh:mm aa", Locale.getDefault())
                val date = formatter.format(it)
                binding.reminderTextView.text = date
            }
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

        binding.reminderLayout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this@NoteActivity)
                .setMessage("Do you want to delete this reminder?")
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    reminder = null
                    binding.reminderLayout.visibility = View.GONE
                }
                .setNegativeButton(
                    "No"
                ) { _, _ ->
                }.create()

            alertDialog.show()
        }

        binding.reminderButton.setOnClickListener {
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            val datePickerDialog = DatePickerDialog(
                this@NoteActivity, { _, year, month, day ->
                    TimePickerDialog(
                        this@NoteActivity, { _, hour, minute ->
                            val pickedDateTime = Calendar.getInstance()
                            pickedDateTime.set(year, month, day, hour, minute,0)
                            Logger.logInfo("Picked: ${pickedDateTime.time}")
                            reminder = pickedDateTime.time
                            binding.reminderLayout.visibility = View.VISIBLE
                            binding.reminderTextView.text = dateToString(pickedDateTime.time)
                        },
                        startHour,
                        startMinute,
                        false
                    ).show()
                },
                startYear,
                startMonth,
                startDay
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    private fun dateToString(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM, hh:mm aa", Locale.getDefault())
        return formatter.format(date)
    }

    private fun returnData() {
        val intent = Intent()
        intent.putExtra("id", id)
        intent.putExtra("title", noteTitle)
        intent.putExtra("content", content)
        intent.putExtra("archived", archived)
        intent.putExtra("reminder", reminder)

        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val DELETE_NOTE = 0
    }
}
