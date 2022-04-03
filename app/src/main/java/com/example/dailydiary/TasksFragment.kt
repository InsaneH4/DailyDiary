package com.example.dailydiary

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.LinkedList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TasksFragment : Fragment(), UpdateAndDelete {
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    private val date = LocalDateTime.now().format(dateFormatter)

    @RequiresApi(Build.VERSION_CODES.O)
    private val tomorrow = LocalDateTime.now().plusDays(1).format(dateFormatter)
    private lateinit var toDoList: LinkedList<ToDoModel>
    private lateinit var quesadilla: DatabaseReference
    private lateinit var adapter: ToDoAdapter
    private lateinit var listViewItem: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab = view.findViewById<FloatingActionButton>(R.id.taskFab)
        listViewItem = view.findViewById(R.id.taskListView)!!
        quesadilla = FirebaseDatabase.getInstance().reference
        fab?.setOnClickListener {
            val layout = LinearLayout(this.context)
            layout.orientation = LinearLayout.VERTICAL
            val alertDialog = AlertDialog.Builder(this.context)
            val nameField = EditText(this.context)
            val startField = EditText(this.context)
            val dueField = EditText(this.context)
            val remindField = EditText(this.context)
            nameField.setSingleLine()
            startField.setSingleLine()
            dueField.setSingleLine()
            remindField.setSingleLine()
            nameField.hint = "Name"
            startField.hint = "Start date"
            dueField.hint = "Due date"
            remindField.hint = "Reminder time"
            alertDialog.setTitle("Add a task")
            layout.addView(nameField)
            layout.addView(startField)
            layout.addView(dueField)
            layout.addView(remindField)
            alertDialog.setView(layout)
            alertDialog.setPositiveButton("Add") { dialog, _ ->
                val todoItemData = ToDoModel.createList()
                if (nameField.text.isEmpty()) {
                    todoItemData.taskName = "New task"
                } else {
                    todoItemData.taskName = nameField.text.toString()
                }
                when {
                    startField.text.isEmpty() -> {
                        todoItemData.startDate = "No start date"
                    }
                    startField.text.toString().equals("today", ignoreCase = true) -> {
                        todoItemData.startDate = "Start date: $date"
                    }
                    startField.text.toString().equals("tomorrow", ignoreCase = true) -> {
                        todoItemData.startDate = "Start date: $tomorrow"
                    }
                    else -> {
                        todoItemData.startDate = "Start date: " + startField.text.toString()
                    }
                }
                when {
                    dueField.text.isEmpty() -> {
                        todoItemData.dueDate = "No due date"
                    }
                    dueField.text.toString().equals("today", ignoreCase = true) -> {
                        todoItemData.dueDate = "Due date: $date"
                    }
                    dueField.text.toString().equals("tomorrow", ignoreCase = true) -> {
                        todoItemData.dueDate =
                            "Due date: $tomorrow"
                    }
                    else -> {
                        todoItemData.dueDate = "Due date: " + dueField.text.toString()
                    }
                }
                if (remindField.text.isEmpty()) {
                    todoItemData.remindTime = "No reminder set"
                } else {
                    todoItemData.remindTime = "Reminder set for: " + remindField.text.toString()
                }
                todoItemData.done = false
                val newItemData = quesadilla.child("tasks").push()
                todoItemData.id = newItemData.key
                newItemData.setValue(todoItemData)
                dialog.dismiss()
                Toast.makeText(this.context, "Task added", Toast.LENGTH_LONG).show()
            }
            alertDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
        toDoList = LinkedList()
        adapter = ToDoAdapter(context, toDoList)
        listViewItem.adapter = adapter
        quesadilla.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No item added", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {
        val items = snapshot.children.iterator()
        if (items.hasNext()) {
            val toDoIndexedValue = items.next()
            val itemsIterator = toDoIndexedValue.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val toDoItemData = ToDoModel.createList()
                val map = currentItem.value as HashMap<*, *>
                toDoItemData.id = currentItem.key
                toDoItemData.done = map["done"] as Boolean?
                toDoItemData.taskName = map["taskName"] as String?
                toDoItemData.startDate = map["startDate"] as String?
                toDoItemData.dueDate = map["dueDate"] as String?
                toDoItemData.remindTime = map["remindTime"] as String?
                toDoList.add(toDoItemData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemId: String?, isDone: Boolean?) {
        val itemReference = quesadilla.child("tasks").child(itemId!!)
        itemReference.child("done").setValue(isDone)
    }

    override fun editItem(
        itemId: String?,
        isDone: Boolean?,
        taskName: String?,
        startDate: String?,
        dueDate: String?,
        remindTime: String?
    ) {
        val itemReference = quesadilla.child("tasks").child(itemId!!)
        itemReference.child("taskName").setValue(taskName)
        itemReference.child("startDate").setValue(startDate)
        itemReference.child("dueDate").setValue(dueDate)
        itemReference.child("remindTime").setValue(remindTime)
    }

    override fun onItemDelete(itemId: String?) {
        val itemReference = quesadilla.child("tasks").child(itemId!!)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}