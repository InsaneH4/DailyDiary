package com.example.dailydiary.tasks

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
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
import com.example.dailydiary.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TasksFragment : Fragment(), UpdateAndDelete {
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("M/d/yy")

    @SuppressLint("SimpleDateFormat")
    private val dateTimeFormat = SimpleDateFormat("M/d/yy H:m aa")

    @RequiresApi(Build.VERSION_CODES.O)
    private val date = LocalDateTime.now().format(dateFormatter)

    @RequiresApi(Build.VERSION_CODES.O)
    private val tomorrow = LocalDateTime.now().plusDays(1).format(dateFormatter)
    private lateinit var toDoList: LinkedList<ToDoModel>
    private lateinit var database: DatabaseReference
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
        createNotificationChannel()
        val taskFab = view.findViewById<FloatingActionButton>(R.id.taskFab)
        listViewItem = view.findViewById(R.id.taskListView)!!
        database = FirebaseDatabase.getInstance().reference
        taskFab?.setOnClickListener {
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
            remindField.hint = "Reminder"
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
                    todoItemData.remindTime = remindField.text.toString()
                    val remindAt = dateTimeFormat.parse(remindField.text.toString())
                    scheduleNotification(
                        remindAt!!.time, "Work on " + nameField.text.toString(),
                        alertDialog.context
                    )
                }
                todoItemData.done = false
                val newItemData = database.child("tasks").push()
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
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No item added", Toast.LENGTH_LONG).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Suppress("SameParameterValue")
    fun scheduleNotification(time: Long, message: String, context: Context) {
        val intent = Intent(context, Notification::class.java)
        intent.putExtra(messageExtra, message)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = desc
        val notificationManager =
            requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
        val itemReference = database.child("tasks").child(itemId!!)
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
        val itemReference = database.child("tasks").child(itemId!!)
        itemReference.child("taskName").setValue(taskName)
        itemReference.child("startDate").setValue(startDate)
        itemReference.child("dueDate").setValue(dueDate)
        itemReference.child("remindTime").setValue(remindTime)
    }

    override fun onItemDelete(itemId: String?) {
        val itemReference = database.child("tasks").child(itemId!!)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}