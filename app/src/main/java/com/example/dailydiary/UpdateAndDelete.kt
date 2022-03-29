package com.example.dailydiary

interface UpdateAndDelete {
    fun modifyItem(itemId: String?, isDone: Boolean?)
    fun onItemDelete(itemId: String?)
}