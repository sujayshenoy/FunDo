package com.example.fundo.ui.home.label

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fundo.R
import com.example.fundo.data.wrappers.Label
import com.example.fundo.data.wrappers.User
import com.example.fundo.databinding.ActivityLabelBinding

class LabelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLabelBinding
    private lateinit var labelViewModel: LabelViewModel
    private lateinit var currentUser: User
    private var labelList = ArrayList<Label>()
    private lateinit var labelAdapter: LabelsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLabelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDataFromIntent()

        labelViewModel = ViewModelProvider(this@LabelActivity)[LabelViewModel::class.java]

        labelAdapter =
            LabelsRecyclerAdapter(this@LabelActivity, labelList, labelViewModel, currentUser)
        binding.labelRecyclerView.layoutManager = LinearLayoutManager(this@LabelActivity)
        binding.labelRecyclerView.adapter = labelAdapter

        attachListeners()
        attachObservers()
    }

    private fun initDataFromIntent() {
        labelList = intent.getSerializableExtra("labelList") as ArrayList<Label>
        currentUser = intent.getSerializableExtra("currentUser") as User
    }

    private fun attachObservers() {
        labelViewModel.addLabelStatus.observe(this@LabelActivity) {
            binding.newLabelTextEdit.setText("")
            labelList.add(it)
            val pos = labelList.indexOf(it)
            labelAdapter.notifyItemInserted(pos)
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
            val intent = Intent()
            intent.putExtra("labelList", labelList)
            setResult(0, intent)
            finish()
        }
    }
}