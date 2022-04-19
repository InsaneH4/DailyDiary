package com.example.dailydiary.contacts


class ContactModel{
    companion object Factory {
        fun createList(): ContactModel = ContactModel()
    }
    var contactId: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var title: String? = null
    var birthday: String? = null
    var address: String? = null
    var email: String? = null
    var phone: String? = null
}