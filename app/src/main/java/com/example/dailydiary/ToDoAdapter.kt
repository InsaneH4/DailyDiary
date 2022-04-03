package com.example.dailydiary

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ToDoAdapter(context: Context?, toDoList: LinkedList<ToDoModel>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    private val date = LocalDateTime.now().format(dateFormatter)

    @RequiresApi(Build.VERSION_CODES.O)
    private val tomorrow = LocalDateTime.now().plusDays(1).format(dateFormatter)
    private var itemList = toDoList
    private var updateAndDelete: UpdateAndDelete = context as UpdateAndDelete
    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(p0: Int): Any {
        return itemList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val id: String? = itemList[p0].id
        var taskName: String? = itemList[p0].taskName
        var startDate: String? = itemList[p0].startDate
        var dueDate: String? = itemList[p0].dueDate
        var remindTime: String? = itemList[p0].remindTime
        val done: Boolean? = itemList[p0].done
        val view: View
        val viewHolder: ListViewHolder
        if (p1 == null) {
            view = inflater.inflate(R.layout.row_itemslayout, p2, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = view.tag as ListViewHolder
        }
        viewHolder.textLabel.text = taskName
        viewHolder.startLabel.text = startDate
        viewHolder.dueLabel.text = dueDate
        viewHolder.remindLabel.text = remindTime
        if (done != null) {
            viewHolder.isDone.isChecked = done
        }
        val mp = MediaPlayer.create(view.context, R.raw.completion)
        viewHolder.isDone.setOnClickListener {
            if (!done!!) {
                mp.start()
            }
            updateAndDelete.modifyItem(id, !done)
        }
        viewHolder.isDeleted.setOnClickListener {
            updateAndDelete.onItemDelete(id)
        }
        viewHolder.editButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(view.context)
            val layout = LinearLayout(view.context)
            layout.orientation = LinearLayout.VERTICAL
            val nameField = EditText(view.context)
            val startField = EditText(view.context)
            val dueField = EditText(view.context)
            val remindField = EditText(view.context)
            nameField.setSingleLine()
            startField.setSingleLine()
            dueField.setSingleLine()
            remindField.setSingleLine()
            nameField.setText(taskName)
            startField.setText(startDate)
            dueField.setText(dueDate)
            remindField.setText(remindTime)
            alertDialog.setTitle("Edit this task")
            layout.addView(nameField)
            layout.addView(startField)
            layout.addView(dueField)
            layout.addView(remindField)
            alertDialog.setView(layout)
            alertDialog.setPositiveButton("Save") { dialog, _ ->
                taskName = nameField.text.toString()
                startDate = when {
                    startField.text.toString().equals("today", ignoreCase = true) -> {
                        "Start date: $date"
                    }
                    startField.text.toString().equals("tomorrow", ignoreCase = true) -> {
                        "Start date: $tomorrow"
                    }
                    else -> {
                        startField.text.toString()
                    }
                }
                dueDate = when {
                    dueField.text.toString().equals("today", ignoreCase = true) -> {
                        "Due date: $date"
                    }
                    dueField.text.toString().equals("tomorrow", ignoreCase = true) -> {
                        "Due date: $tomorrow"
                    }
                    else -> {
                        dueField.text.toString()
                    }
                }
                remindTime = remindField.text.toString()
                updateAndDelete.editItem(id, done, taskName, startDate, dueDate, remindTime)
                dialog.dismiss()
                Toast.makeText(view.context, "Task saved", Toast.LENGTH_LONG).show()
            }
            alertDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
        return view
    }

    internal class ListViewHolder(row: View?) {
        val textLabel: TextView = row!!.findViewById(R.id.itemTextView)
        val startLabel: TextView = row!!.findViewById(R.id.startTextView)
        val dueLabel: TextView = row!!.findViewById(R.id.dueTextView)
        val remindLabel: TextView = row!!.findViewById(R.id.remindTextView)
        val isDone: CheckBox = row!!.findViewById(R.id.taskCheckbox)
        val editButton: ImageButton = row!!.findViewById(R.id.taskEdit)
        val isDeleted: ImageButton = row!!.findViewById(R.id.taskClose)
    }
}