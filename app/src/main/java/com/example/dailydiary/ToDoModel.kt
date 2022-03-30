package com.example.dailydiary

class ToDoModel {
    companion object Factory {
        fun createList(): ToDoModel = ToDoModel()
    }

    var id: String? = null
    var taskName: String? = null
    var dueDate: String? = null
    var done: Boolean? = false
}