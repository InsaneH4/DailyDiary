package com.example.dailydiary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), UpdateAndDelete {
    private val quesadilla = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeCurrentFragment(TasksFragment())
        bottom_navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tasks_item -> makeCurrentFragment(TasksFragment())
                R.id.contacts_item -> makeCurrentFragment(ContactsFragment())
                R.id.info_item -> makeCurrentFragment(InfoFragment())
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
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
        remindTime : String?
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
    }
}