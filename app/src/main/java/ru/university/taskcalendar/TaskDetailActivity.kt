package ru.university.taskcalendar

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class TaskDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    private val viewModel: TaskViewModel by viewModels()
    private var taskId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)

        if (savedInstanceState == null) {
            viewModel.loadTask(taskId)
        }

        // Подписка на одну задачу.
        viewModel.selectedTask.observe(this) { task ->
            if (task != null) bind(task)
        }

        // Экран закрывается, когда задача удалена или не найдена.
        viewModel.closeScreen.observe(this) { close ->
            if (close == true) {
                Toast.makeText(this, R.string.task_not_found, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        findViewById<Button>(R.id.detailToggleButton).setOnClickListener {
            viewModel.selectedTask.value?.let { viewModel.toggleDone(it) }
        }

        findViewById<Button>(R.id.detailDeleteButton).setOnClickListener {
            viewModel.selectedTask.value?.let { viewModel.deleteTask(it) }
        }
    }

    private fun bind(task: Task) {
        findViewById<TextView>(R.id.detailTaskTitle).text = task.title
        findViewById<TextView>(R.id.detailTaskId).text = task.id.toString()
        findViewById<TextView>(R.id.detailTaskDate).text = "${task.date} ${task.time}"
        findViewById<TextView>(R.id.detailTaskDescription).text =
            task.description.ifEmpty { "—" }

        val statusView = findViewById<TextView>(R.id.detailTaskStatus)
        if (task.isDone) {
            statusView.text = getString(R.string.task_done)
            statusView.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            statusView.text = getString(R.string.task_not_done)
            statusView.setTextColor(Color.parseColor("#C62828"))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}