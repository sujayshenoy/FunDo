package com.example.fundo.ui.note

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.common.Logger
import com.example.fundo.config.Constants.ASSOCIATE_LABEL_REQUEST_CODE
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.data.wrappers.Label
import com.example.fundo.data.wrappers.User
import com.example.fundo.databinding.ActivityNoteBinding
import com.example.fundo.ui.home.label.LabelActivity
import com.example.fundo.ui.home.label.LabelActivity.Companion.MODE_SELECT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private var id: Long? = 0
    private var archived = false
    private var noteTitle = ""
    private var content = ""
    private var reminder: Date? = null
    private lateinit var firebaseID: String
    private lateinit var currentUser: User
    private lateinit var noteViewModel: NoteViewModel
    private val labelList = ArrayList<Label>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        noteViewModel = ViewModelProvider(this@NoteActivity)[NoteViewModel::class.java]

        loadDataFromIntent()
        attachListeners()
        noteViewModel.getLabels(this@NoteActivity, firebaseID, currentUser)

        noteViewModel.getLabels.observe(this@NoteActivity){
            labelList.addAll(it)
            Logger.logInfo("NoteActivity: labels-> $labelList")
        }

        noteViewModel.linkLabelStatus.observe(this@NoteActivity){
            Logger.logInfo("NoteActivity: Linked Labels to Note")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ASSOCIATE_LABEL_REQUEST_CODE && data != null){
            val selectedLabels = data.getSerializableExtra("selectedLabels") as ArrayList<Label>
            noteViewModel.linkNoteLabels(this@NoteActivity, firebaseID, selectedLabels, currentUser)

            val tempLabels = ArrayList<Label>()
            tempLabels.addAll(labelList)

            tempLabels.removeAll(selectedLabels)
            tempLabels.forEach {
                val linkID = "${firebaseID}_${it.firebaseId}"
                noteViewModel.unlinkNoteLabels(this@NoteActivity, linkID, currentUser)
            }

            labelList.clear()
            labelList.addAll(selectedLabels)
            val a = 0
        }
    }

    private fun loadDataFromIntent() {
        val intent = intent
        id = intent.extras?.getLong("id")
        noteTitle = intent.extras?.getString("title") ?: ""
        content = intent.extras?.getString("content") ?: ""
        archived = intent.extras?.getBoolean("archived") ?: false
        reminder = intent.extras?.getSerializable("reminder") as? Date
        firebaseID = intent.extras?.getString("firebaseID") ?: ""
        currentUser = intent.extras?.getSerializable("currentUser") as? User ?: User("","","")

        if (noteTitle.isNotEmpty() || content.isNotEmpty()) {
            binding.titleTextEdit.setText(noteTitle)
            binding.contentTextEdit.setText(content)
            binding.deleteButton.visibility = View.VISIBLE
            binding.reminderButton.visibility = View.VISIBLE
            binding.archiveButton.visibility = View.VISIBLE
            binding.addLabelButton.visibility = View.VISIBLE

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

        binding.addLabelButton.setOnClickListener {
            val intent = Intent(this@NoteActivity,LabelActivity::class.java)
            intent.putExtra("currentUser", currentUser)
            intent.putExtra("mode", MODE_SELECT)
            intent.putExtra("attachedLabels", labelList)
            startActivityForResult(intent,ASSOCIATE_LABEL_REQUEST_CODE)
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
                            pickedDateTime.set(year, month, day, hour, minute, 0)
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
