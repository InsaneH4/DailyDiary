package com.example.dailydiary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydiary.adapter.TodoAdapter
import com.example.dailydiary.taskModel.TodoModel
import java.util.LinkedList

class TasksFragment : Fragment() {
    private val tasksRecyclerView = view?.findViewById<RecyclerView>(R.id.tasksRecyclerView)
    private val tasksAdapter: TodoAdapter = TodoAdapter(this)
    private val taskList: LinkedList<TodoModel> = LinkedList<TodoModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tasksRecyclerView?.layoutManager = LinearLayoutManager(this.context)
        tasksRecyclerView?.adapter = tasksAdapter
        val task = TodoModel()
        task.task = "Test task lolll"
        task.status = 0
        task.id = 1
        taskList.add(task)
        taskList.add(task)
        taskList.add(task)
        tasksAdapter.setTasks(taskList)
    }
}