package com.example.dailydiary

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.*

//TODO: Add task editing feature, start/end date feature
class TasksFragment : Fragment(), UpdateAndDelete {
    private lateinit var quesadilla: DatabaseReference
    private var toDoList: LinkedList<ToDoModel>? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var listViewItem: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

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
            val dueField = EditText(this.context)
            nameField.setSingleLine()
            dueField.setSingleLine()
            nameField.hint = "Name"
            dueField.hint = "Due date"
            alertDialog.setTitle("Add a task")
            layout.addView(nameField)
            layout.addView(dueField)
            alertDialog.setView(layout)
            alertDialog.setPositiveButton("Add") { dialog, _ ->
                val todoItemData = ToDoModel.createList()
                if (nameField.text.isEmpty()) {
                    todoItemData.taskName = "New task"
                } else {
                    todoItemData.taskName = nameField.text.toString()
                }
                if (dueField.text.isEmpty()) {
                    todoItemData.dueDate = "No due date"
                } else {
                    todoItemData.dueDate = dueField.text.toString()
                }
                todoItemData.done = false
                val newItemData = quesadilla.child("todo").push()
                todoItemData.id = newItemData.key
                newItemData.setValue(todoItemData)
                dialog.dismiss()
                Toast.makeText(this.context, "Task saved", Toast.LENGTH_LONG).show()
            }
            alertDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
        toDoList = LinkedList()
        adapter = ToDoAdapter(context, toDoList!!)
        listViewItem.adapter = adapter
        quesadilla.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList!!.clear()
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
                toDoItemData.dueDate = map["dueDate"] as String?
                toDoList!!.add(toDoItemData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemId: String?, isDone: Boolean?) {
        val itemReference = quesadilla.child("todo").child(itemId!!)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemId: String?) {
        val itemReference = quesadilla.child("todo").child(itemId!!)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}