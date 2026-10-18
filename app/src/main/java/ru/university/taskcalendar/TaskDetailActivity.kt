package ru.university.taskcalendar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    private val tag = "TaskDetailActivity"
    private lateinit var taskDao: TaskDao
    private var taskId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        taskDao = AppDatabase.getInstance(this).taskDao()

        findViewById<Button>(R.id.detailToggleButton).setOnClickListener { toggleDone() }
        findViewById<Button>(R.id.detailDeleteButton).setOnClickListener { deleteTask() }

        loadTask()
    }

    private fun loadTask() {
        lifecycleScope.launch {
            val task = withContext(Dispatchers.IO) { taskDao.getById(taskId) }
            if (task == null) {
                Log.d(tag, "Задача id=$taskId не найдена")
                finish()
                return@launch
            }
            bind(task)
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

    private fun toggleDone() {
        lifecycleScope.launch {
            val task = withContext(Dispatchers.IO) { taskDao.getById(taskId) } ?: return@launch
            withContext(Dispatchers.IO) { taskDao.update(task.copy(isDone = !task.isDone)) }
            loadTask()
        }
    }

    private fun deleteTask() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { taskDao.deleteById(taskId) }
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}