package com.example.dailydiary.contacts

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.dailydiary.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import java.util.*

class ContactsFragment : Fragment(), UpdateAndDeleteContact {
    private lateinit var contactList: LinkedList<ContactModel>
    private lateinit var adapter: ContactsAdapter
    private lateinit var listViewItem: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondary = Firebase.app("secondary")
        val secondaryDatabase = Firebase.database(secondary)
        val database = secondaryDatabase.reference
        val contactsFab = view.findViewById<FloatingActionButton>(R.id.contactsFab)
        listViewItem = view.findViewById(R.id.contactsListView)!!
        contactsFab?.setOnClickListener {
            val alertDialog = AlertDialog.Builder(view.context)
            val layout = LinearLayout(view.context)
            layout.orientation = LinearLayout.VERTICAL
            val firstNameField = EditText(view.context)
            val lastNameField = EditText(view.context)
            val titleField = EditText(view.context)
            val bdayField = EditText(view.context)
            val addressField = EditText(view.context)
            val emailField = EditText(view.context)
            val phoneField = EditText(view.context)
            val scroll = ScrollView(view.context)
            firstNameField.isSingleLine = true
            lastNameField.isSingleLine = true
            titleField.isSingleLine = true
            bdayField.isSingleLine = true
            addressField.isSingleLine = true
            emailField.isSingleLine = true
            phoneField.isSingleLine = true
            firstNameField.hint = "First name"
            lastNameField.hint = "Last name"
            titleField.hint = "Title"
            bdayField.hint = "Birthday"
            addressField.hint = "Home address"
            emailField.hint = "Email address"
            phoneField.hint = "Phone number"
            alertDialog.setTitle("Create new contact")
            layout.addView(firstNameField)
            layout.addView(lastNameField)
            layout.addView(titleField)
            layout.addView(bdayField)
            layout.addView(addressField)
            layout.addView(emailField)
            layout.addView(phoneField)
            scroll.addView(layout)
            alertDialog.setView(scroll)
            alertDialog.setPositiveButton("Save") { dialog, _ ->
                val contactData = ContactModel.createList()
                if (firstNameField.text.isEmpty()) {
                    contactData.firstName = "N/A"
                } else {
                    contactData.firstName = firstNameField.text.toString()
                }
                if (lastNameField.text.isEmpty()) {
                    contactData.lastName = "No last name set"
                } else {
                    contactData.lastName = lastNameField.text.toString()
                }
                if (titleField.text.isEmpty()) {
                    contactData.title = "No title set"
                } else {
                    contactData.title = titleField.text.toString()
                }
                if (bdayField.text.isEmpty()) {
                    contactData.birthday = "No birthday set"
                } else {
                    contactData.birthday = bdayField.text.toString()
                }
                if (addressField.text.isEmpty()) {
                    contactData.address = "No address set"
                } else {
                    contactData.address = addressField.text.toString()
                }
                if (emailField.text.isEmpty()) {
                    contactData.email = "No email set"
                } else {
                    contactData.email = emailField.text.toString()
                }
                if (phoneField.text.isEmpty()) {
                    contactData.phone = "No phone # set"
                } else {
                    contactData.phone = phoneField.text.toString()
                }
                val newItemData = database.child("contacts").push()
                contactData.contactId = newItemData.key
                newItemData.setValue(contactData)
                dialog.dismiss()
                Toast.makeText(view.context, "Contact added", Toast.LENGTH_LONG).show()
            }
            alertDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
        contactList = LinkedList()
        adapter = ContactsAdapter(context, contactList)
        listViewItem.adapter = adapter
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Database error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {
        val items = snapshot.children.iterator()
        if (items.hasNext()) {
            val contactsIndexedValue = items.next()
            val itemsIterator = contactsIndexedValue.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val contactData = ContactModel.createList()
                val map = currentItem.value as HashMap<*, *>
                contactData.contactId = currentItem.key
                contactData.firstName = map["firstName"] as String?
                contactData.lastName = map["lastName"] as String?
                contactData.title = map["title"] as String?
                contactData.birthday = map["birthday"] as String?
                contactData.address = map["address"] as String?
                contactData.email = map["email"] as String?
                contactData.phone = map["phone"] as String?
                contactList.add(contactData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun editContact(
        itemId: String?,
        firstName: String?,
        lastName: String?,
        title: String?,
        birthday: String?,
        address: String?,
        email: String?,
        phone: String?
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
    }

    override fun onContactDelete(itemId: String?) {
        val secondary = Firebase.app("secondary")
        val secondaryDatabase = Firebase.database(secondary)
        val database = secondaryDatabase.reference
        val itemReference = database.child("contacts").child(itemId!!)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}