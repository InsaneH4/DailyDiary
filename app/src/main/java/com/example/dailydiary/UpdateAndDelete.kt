package com.example.dailydiary

interface UpdateAndDelete {
    fun modifyItem(itemId: String?, isDone: Boolean?)
    fun editItem(
        itemId: String?,
        isDone: Boolean?,
        taskName: String?,
        startDate: String?,
        dueDate: String?
    )

    fun onItemDelete(itemId: String?)
}