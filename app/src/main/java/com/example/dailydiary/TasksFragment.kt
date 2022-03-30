package com.example.dailydiary

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

class TasksFragment : Fragment(), UpdateAndDelete {
    private lateinit var database: DatabaseReference
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
        database = FirebaseDatabase.getInstance().reference
        fab?.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            val textField = EditText(this.context)
            textField.setSingleLine()
            textField.hint = "New task"
            alertDialog.setTitle("Add a task")
            alertDialog.setView(textField)
            alertDialog.setPositiveButton("Add") { dialog, _ ->
                val todoItemData = ToDoModel.createList()
                if (textField.text.isEmpty()) {
                    todoItemData.taskName = "New task"
                } else {
                    todoItemData.taskName = textField.text.toString()
                }
                todoItemData.done = false
                val newItemData = database.child("todo").push()
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
        database.addValueEventListener(object : ValueEventListener {
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
                toDoList!!.add(toDoItemData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemId: String?, isDone: Boolean?) {
        val itemReference = database.child("todo").child(itemId!!)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemId: String?) {
        val itemReference = database.child("todo").child(itemId!!)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}