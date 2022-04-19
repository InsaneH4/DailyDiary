package com.example.dailydiary.contacts

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.dailydiary.R
import java.util.*

class ContactsAdapter(context: Context?, contactsList: LinkedList<ContactModel>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var contactList = contactsList
    private var updateAndDelete: UpdateAndDeleteContact = context as UpdateAndDeleteContact
    override fun getCount(): Int {
        return contactList.size
    }

    override fun getItem(p0: Int): Any {
        return contactList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val id: String? = contactList[p0].contactId
        var firstName: String? = contactList[p0].firstName
        var lastName: String? = contactList[p0].lastName
        var title: String? = contactList[p0].title
        var birthday: String? = contactList[p0].birthday
        var address: String? = contactList[p0].address
        var email: String? = contactList[p0].email
        var phone: String? = contactList[p0].phone
        val view: View
        val viewHolder: ListViewHolder
        if (p1 == null) {
            view = inflater.inflate(R.layout.contact_list_layout, p2, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = view.tag as ListViewHolder
        }
        val nameText = "$firstName $lastName"
        viewHolder.nameLabel.text = nameText
        viewHolder.viewButton.setOnClickListener {
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
            firstNameField.inputType = 0
            lastNameField.inputType = 0
            titleField.inputType = 0
            bdayField.inputType = 0
            addressField.inputType = 0
            emailField.inputType = 0
            phoneField.inputType = 0
            firstNameField.setText(firstName)
            lastNameField.setText(lastName)
            titleField.setText(title)
            bdayField.setText(birthday)
            addressField.setText(address)
            emailField.setText(email)
            phoneField.setText(phone)
            alertDialog.setTitle("View this contact")
            layout.addView(firstNameField)
            layout.addView(lastNameField)
            layout.addView(titleField)
            layout.addView(bdayField)
            layout.addView(addressField)
            layout.addView(emailField)
            layout.addView(phoneField)
            scroll.addView(layout)
            alertDialog.setView(scroll)
            alertDialog.setPositiveButton("Back") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }

        viewHolder.editButton.setOnClickListener {
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
            firstNameField.setText(firstName)
            firstNameField.hint = "First name"
            lastNameField.setText(lastName)
            lastNameField.hint = "Last name"
            titleField.setText(title)
            titleField.hint = "Title"
            bdayField.setText(birthday)
            bdayField.hint = "Birthday"
            addressField.setText(address)
            addressField.hint = "Home address"
            emailField.setText(email)
            emailField.hint = "Email address"
            phoneField.setText(phone)
            phoneField.hint = "Phone number"
            alertDialog.setTitle("Edit this contact")
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
                firstName = firstNameField.text.toString()
                lastName = lastNameField.text.toString()
                title = titleField.text.toString()
                birthday = bdayField.text.toString()
                address = addressField.text.toString()
                email = emailField.text.toString()
                phone = phoneField.text.toString()
                updateAndDelete.editContact(
                    id,
                    firstName,
                    lastName,
                    title,
                    birthday,
                    address,
                    email,
                    phone
                )
                dialog.dismiss()
                Toast.makeText(view.context, "Contact saved", Toast.LENGTH_LONG).show()
            }
            alertDialog.setNegativeButton("Delete") { dialog, _ ->
                updateAndDelete.onContactDelete(id)
                dialog.dismiss()
                Toast.makeText(view.context, "Contact deleted", Toast.LENGTH_LONG).show()
            }
            alertDialog.show()
        }
        return view
    }

    internal class ListViewHolder(row: View?) {
        val nameLabel: TextView = row!!.findViewById(R.id.nameText)
        val editButton: ImageButton = row!!.findViewById(R.id.contactEdit)
        val viewButton: ImageButton = row!!.findViewById(R.id.contactView)
    }
}