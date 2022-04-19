package com.example.dailydiary.contacts

interface UpdateAndDeleteContact {
    fun editContact(
        itemId: String?,
        firstName: String?,
        lastName: String?,
        title: String?,
        birthday: String?,
        address: String?,
        email: String?,
        phone: String?
    )

    fun onContactDelete(itemId: String?)
}