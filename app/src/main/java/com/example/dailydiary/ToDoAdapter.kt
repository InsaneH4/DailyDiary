package com.example.dailydiary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

class ToDoAdapter(context: Context?, toDoList: MutableList<ToDoModel>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = toDoList
    private var updateAndDelete : UpdateAndDelete = context as UpdateAndDelete
    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(p0: Int): Any {
        return itemList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val id: String? = itemList[p0].id
        val taskName: String? = itemList[p0].taskName
        val done: Boolean? = itemList[p0].done
        val view: View
        val viewHolder: ListViewHolder
        if (p1 == null) {
            view = inflater.inflate(R.layout.row_itemslayout, p2, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = view.tag as ListViewHolder
        }
        viewHolder.textLabel.text = taskName
        if (done != null) {
            viewHolder.isDone.isChecked = done
        }
        viewHolder.isDone.setOnClickListener {
            updateAndDelete.modifyItem(id, !done!!)
        }
        viewHolder.isDeleted.setOnClickListener {
            updateAndDelete.onItemDelete(id)
        }
        return view
    }

    private class ListViewHolder(row: View?) {
        val textLabel: TextView = row!!.findViewById(R.id.itemTextView)
        val isDone: CheckBox = row!!.findViewById(R.id.taskCheckbox)
        val isDeleted: ImageButton = row!!.findViewById(R.id.taskClose)
    }
}