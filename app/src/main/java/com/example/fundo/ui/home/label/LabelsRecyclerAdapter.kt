package com.example.fundo.ui.home.label

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundo.R
import com.example.fundo.common.Logger
import com.example.fundo.common.Utilities
import com.example.fundo.data.models.CloudDBLabel
import com.example.fundo.data.wrappers.Label
import com.example.fundo.data.wrappers.User

class LabelsRecyclerAdapter(
    private val context: Context,
    private val labelsList: List<Label>,
    private val labelViewModel: LabelViewModel,
    private val currentUser: User
) :
    RecyclerView.Adapter<LabelsRecyclerAdapter.LabelsViewHolder>() {

    class LabelsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editLabelTextEdit: EditText = itemView.findViewById(R.id.editLabelTextEdit)
        val deleteLabelButton: ImageView = itemView.findViewById(R.id.deleteLabelButton)
        val editLabelButton: ImageView = itemView.findViewById(R.id.editLabelButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerlabels, parent, false)
        return LabelsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LabelsViewHolder, position: Int) {
        val currentLabel = labelsList[position]
        val editLabelTextEdit = holder.editLabelTextEdit
        val editLabelButton = holder.editLabelButton
        val deleteLabelButton = holder.deleteLabelButton

        editLabelTextEdit.setText(currentLabel.name)

        editLabelTextEdit.setOnFocusChangeListener { _, b ->
            if (b) {
                editLabelButton.setImageDrawable(context.getDrawable(R.drawable.button_check))
                deleteLabelButton.setImageDrawable(context.getDrawable(R.drawable.button_delete))
                editLabelButton.tag = "save"
                deleteLabelButton.tag = "delete"
            } else {
                editLabelButton.setImageDrawable(context.getDrawable(R.drawable.button_edit))
                deleteLabelButton.setImageDrawable(context.getDrawable(R.drawable.icon_label))
                editLabelButton.tag = "label"
                deleteLabelButton.tag = "edit"
            }
        }

        deleteLabelButton.setOnClickListener {
            if (it.tag == "delete") {
                labelViewModel.deleteLabel(context, currentLabel, currentUser)
            } else {
                editLabelTextEdit.requestFocus()
            }
        }

        editLabelButton.setOnClickListener {
            if (it.tag == "save") {
                val updateLabel = currentLabel.copy(name = editLabelTextEdit.text.toString())
                if (updateLabel.name.isEmpty()) {
                    labelViewModel.deleteLabel(context, currentLabel, currentUser)
                } else if (updateLabel.name != currentLabel.name) {
                    labelViewModel.updateLabel(context, updateLabel, currentUser)
                    currentLabel.name = updateLabel.name
                }
            } else {
                editLabelTextEdit.requestFocus()
            }
        }
    }

    override fun getItemCount(): Int {
        return labelsList.size
    }
}