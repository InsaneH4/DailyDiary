package com.example.dailydiary.tasks

interface UpdateAndDelete {
    fun modifyItem(itemId: String?, isDone: Boolean?)
    fun editItem(
        itemId: String?,
        isDone: Boolean?,
        taskName: String?,
        startDate: String?,
        dueDate: String?,
        remindTime: String?
    )

    fun onItemDelete(itemId: String?)
}