package com.example.dailydiary.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydiary.R
import com.example.dailydiary.taskModel.TodoModel
import java.util.*


class TodoAdapter(activity : Fragment) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private var todoList: LinkedList<TodoModel> = LinkedList<TodoModel>()
    private var activity: Fragment = Fragment()
    fun todoAdapter(activity: Fragment) {
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: TodoModel = todoList[position]
        holder.task.text = item.task
        holder.task.isChecked = toBool(item.status)
    }

    private fun toBool(x: Int): Boolean {
        return x != 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(todoList : LinkedList<TodoModel>){
        this.todoList = todoList
        notifyDataSetChanged()
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var task: CheckBox = view.findViewById(R.id.todoCheckbox)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}