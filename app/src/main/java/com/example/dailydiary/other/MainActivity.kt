package com.example.dailydiary.other

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dailydiary.R
import com.example.dailydiary.contacts.ContactsFragment
import com.example.dailydiary.contacts.UpdateAndDeleteContact
import com.example.dailydiary.tasks.TasksFragment
import com.example.dailydiary.tasks.UpdateAndDelete
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), UpdateAndDelete, UpdateAndDeleteContact {
    private val url = "https://dailydiarycontacts-default-rtdb.firebaseio.com/"
    private val database = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val options =
            FirebaseOptions.Builder().setProjectId("dailydiarycontacts")
                .setApplicationId("1:947123898210:android:8a5478577877af5efa4e3b")
                .setDatabaseUrl(url).build()
        Firebase.initialize(this, options, "secondary")
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
    }

    override fun editContact(
        itemId: String?,
        firstName: String?,
        lastName: String?,
        title: String?,
        birthday: String?,
        address: String?,
        email: String?,
        phone: String?,
        notes: String?
    ) {
        val secondary = Firebase.app("secondary")
        val secondaryDatabase = Firebase.database(secondary)
        val database = secondaryDatabase.reference
        val itemReference = database.child("contacts").child(itemId!!)
        itemReference.child("firstName").setValue(firstName)
        itemReference.child("lastName").setValue(lastName)
        itemReference.child("title").setValue(title)
        itemReference.child("birthday").setValue(birthday)
        itemReference.child("address").setValue(address)
        itemReference.child("email").setValue(email)
        itemReference.child("phone").setValue(phone)
        itemReference.child("notes").setValue(notes)
    }

    override fun onContactDelete(itemId: String?) {
        val secondary = Firebase.app("secondary")
        val secondaryDatabase = Firebase.database(secondary)
        val database = secondaryDatabase.reference
        val itemReference = database.child("contacts").child(itemId!!)
        itemReference.removeValue()
    }
}