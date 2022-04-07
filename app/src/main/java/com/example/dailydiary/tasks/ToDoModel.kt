package com.example.dailydiary.tasks

class ToDoModel {
    companion object Factory {
        fun createList(): ToDoModel = ToDoModel()
    }

    var id: String? = null
    var taskName: String? = null
    var startDate: String? = null
    var dueDate: String? = null
    var remindTime: String? = null
    var done: Boolean? = false
}