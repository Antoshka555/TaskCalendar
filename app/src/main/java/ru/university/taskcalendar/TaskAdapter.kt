package ru.university.taskcalendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DIFF) {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val date: TextView = itemView.findViewById(R.id.taskDate)
        val description: TextView = itemView.findViewById(R.id.taskDescription)
        val status: TextView = itemView.findViewById(R.id.taskStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.title.text = task.title
        holder.date.text = "${task.date} • ${task.time}"
        holder.description.text = task.description

        if (task.isDone) {
            holder.status.text = holder.itemView.context.getString(R.string.task_done)
            holder.status.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            holder.status.text = holder.itemView.context.getString(R.string.task_not_done)
            holder.status.setTextColor(Color.parseColor("#C62828"))
        }

        holder.itemView.setOnClickListener { onClick(task) }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
        }
    }
}