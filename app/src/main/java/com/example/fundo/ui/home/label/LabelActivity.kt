package com.example.fundo.ui.home.label

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fundo.R
import com.example.fundo.common.Logger
import com.example.fundo.data.wrappers.Label
import com.example.fundo.data.wrappers.User
import com.example.fundo.databinding.ActivityLabelBinding
import com.example.fundo.ui.home.HomeViewModel
import kotlin.properties.Delegates

class LabelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLabelBinding
    private lateinit var labelViewModel: LabelViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var currentUser: User
    private var labelList = ArrayList<Label>()
    private lateinit var labelAdapter: LabelsRecyclerAdapter
    private var mode by Delegates.notNull<Int>()
    private val selectedLabels = ArrayList<Label>()
    private var attachedLabels = ArrayList<Label>()

    companion object {
        const val MODE_ADD = 0
        const val MODE_SELECT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLabelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDataFromIntent()

        labelViewModel = ViewModelProvider(this@LabelActivity)[LabelViewModel::class.java]
        homeViewModel = ViewModelProvider(this@LabelActivity)[HomeViewModel::class.java]

        homeViewModel.getLabelFromDB(this@LabelActivity, currentUser)
        labelAdapter =
            LabelsRecyclerAdapter(
                this@LabelActivity,
                labelList,
                labelViewModel, currentUser, mode,
                attachedLabels
            )
        binding.labelRecyclerView.layoutManager = LinearLayoutManager(this@LabelActivity)
        binding.labelRecyclerView.adapter = labelAdapter

        attachListeners()
        attachObservers()
        setUpOpMode()
    }

    private fun setUpOpMode() {
        when (mode) {
            MODE_ADD -> {
            }
            MODE_SELECT -> {
                binding.newLabelLayout.visibility = View.GONE
                binding.saveLabelsFab.visibility = View.VISIBLE
            }
        }
    }

    private fun initDataFromIntent() {
        currentUser = intent.getSerializableExtra("currentUser") as User
        mode = intent.getIntExtra("mode", MODE_ADD)
        if (mode == MODE_SELECT) {
            attachedLabels = intent.getSerializableExtra("attachedLabels") as ArrayList<Label>
        }
    }

    private fun attachObservers() {
        labelViewModel.addLabelStatus.observe(this@LabelActivity) {
            binding.newLabelTextEdit.setText("")
            labelList.add(it)
            val pos = labelList.indexOf(it)
            labelAdapter.notifyItemInserted(pos)
        }

        homeViewModel.getLabelFromDB.observe(this@LabelActivity) {
            labelList.addAll(it)
            labelAdapter.notifyDataSetChanged()
        }

        labelViewModel.deleteLabelStatus.observe(this@LabelActivity) {
            val pos = labelList.indexOf(it)
            labelList.remove(it)
            labelAdapter.notifyItemRemoved(pos)
        }

        labelViewModel.updateLabelStatus.observe(this@LabelActivity) {
            labelList.forEachIndexed { index, label ->
                if (it.firebaseId == label.firebaseId) {
                    label.name = it.name
                    label.lastModified = it.lastModified
                    labelAdapter.notifyItemChanged(index)
                }
            }
        }
    }

    private fun attachListeners() {
        binding.newLabelTextEdit.setOnFocusChangeListener { _, b ->
            if (b) {
                binding.saveNewLabelButton.visibility = View.VISIBLE
                binding.addNewLabelButton.setImageDrawable(getDrawable(R.drawable.button_close))
                binding.addNewLabelButton.tag = "close"
            } else {
                binding.saveNewLabelButton.visibility = View.GONE
                binding.addNewLabelButton.setImageDrawable(getDrawable(R.drawable.button_add))
                binding.addNewLabelButton.tag = "add"
                binding.newLabelTextEdit.setText("")
            }
        }

        binding.addNewLabelButton.setOnClickListener {
            if (binding.addNewLabelButton.tag == "add") {
                binding.newLabelTextEdit.requestFocus()
            } else {
                binding.newLabelTextEdit.clearFocus()
            }
        }

        binding.saveNewLabelButton.setOnClickListener {
            val label = Label(name = binding.newLabelTextEdit.text.toString())
            if (label.name.isNotEmpty()) {
                labelViewModel.addLabel(this@LabelActivity, label, currentUser)
            }
        }

        binding.backButton.setOnClickListener {
            if (mode == MODE_ADD) {
                val intent = Intent()
                intent.putExtra("labelList", labelList)
                setResult(0, intent)
                finish()
            } else {
                finish()
            }
        }

        binding.saveLabelsFab.setOnClickListener {
            labelList.forEach { label ->
                if (label.isChecked) {
                    selectedLabels.add(label)
                }
            }

            val intent = Intent()
            intent.putExtra("selectedLabels", selectedLabels)
            setResult(0, intent)
            finish()
        }
    }
}