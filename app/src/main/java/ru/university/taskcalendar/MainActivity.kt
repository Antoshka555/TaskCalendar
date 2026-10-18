package ru.university.taskcalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private val tag = "MainActivityLifecycle"


    private val viewModel: TaskViewModel by viewModels()

    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "onCreate: Activity создана")

        val recyclerView: RecyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        viewModel.tasks.observe(this) { tasks ->
            Log.d(tag, "observe: получено ${tasks.size} задач")
            adapter.submitList(tasks)
        }


        viewModel.seedIfEmptyOnce()

        val fab: FloatingActionButton = findViewById(R.id.addTaskFab)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
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