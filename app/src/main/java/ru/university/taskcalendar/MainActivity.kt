package ru.university.taskcalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivityLifecycle"
    private val dbTag = "RoomCheck"

    private lateinit var adapter: TaskAdapter
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "onCreate: Activity создана")

        taskDao = AppDatabase.getInstance(this).taskDao()

        val recyclerView: RecyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        seedIfEmpty()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskDao.observeAll().collect { tasks ->
                    adapter.submitList(tasks)
                    Log.d(dbTag, "UI получил ${tasks.size} задач из БД")
                }
            }
        }

        val fab: FloatingActionButton = findViewById(R.id.addTaskFab)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }

    private fun seedIfEmpty() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val existing = taskDao.getAll()
                Log.d(dbTag, "Проверка БД: найдено ${existing.size} задач")

                if (existing.isEmpty()) {
                    val seed = listOf(
                        Task(
                            title = getString(R.string.task_1_title),
                            description = getString(R.string.task_1_desc),
                            date = "2026-05-01", time = "09:00"
                        ),
                        Task(
                            title = getString(R.string.task_2_title),
                            description = getString(R.string.task_2_desc),
                            date = "2026-05-02", time = "07:30", isDone = true
                        ),
                        Task(
                            title = getString(R.string.task_3_title),
                            description = getString(R.string.task_3_desc),
                            date = "2026-05-03", time = "19:00"
                        ),
                        Task(
                            title = getString(R.string.task_4_title),
                            description = getString(R.string.task_4_desc),
                            date = "2026-05-04", time = "20:00", isDone = true
                        ),
                        Task(
                            title = getString(R.string.task_5_title),
                            description = getString(R.string.task_5_desc),
                            date = "2026-05-05", time = "12:00"
                        )
                    )
                    taskDao.insertAll(seed)
                    Log.d(dbTag, "Вставлено ${seed.size} стартовых задач")
                }

                taskDao.getAll().forEach { t ->
                    Log.d(
                        dbTag,
                        "task id=${t.id} title='${t.title}' date=${t.date} ${t.time} done=${t.isDone}"
                    )
                }

                val byDate = taskDao.getByDate("2026-05-02")
                Log.d(dbTag, "Задач на 2026-05-02: ${byDate.size}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart: Activity становится видимой")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume: Activity доступна для взаимодействия")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause: Activity теряет фокус")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop: Activity больше не видна")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy: Activity уничтожена")
    }
}