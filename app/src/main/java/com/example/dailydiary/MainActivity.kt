package com.example.dailydiary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), UpdateAndDelete {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeCurrentFragment(TasksFragment())
        bottom_navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tasks_item -> makeCurrentFragment(TasksFragment())
                R.id.contacts_item -> makeCurrentFragment(ContactsFragment())
                R.id.settings_item -> makeCurrentFragment(SettingsFragment())
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
        val quesadilla = FirebaseDatabase.getInstance().reference
        val itemReference = quesadilla.child("tasks").child(itemId!!)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemId: String?) {
        val quesadilla = FirebaseDatabase.getInstance().reference
        val itemReference = quesadilla.child("tasks").child(itemId!!)
        itemReference.removeValue()
    }
}