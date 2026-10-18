package ru.university.taskcalendar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val titleEdit = findViewById<EditText>(R.id.addTitleEdit)
        val descEdit = findViewById<EditText>(R.id.addDescriptionEdit)
        val dateEdit = findViewById<EditText>(R.id.addDateEdit)
        val timeEdit = findViewById<EditText>(R.id.addTimeEdit)


        viewModel.taskSaved.observe(this) { saved ->
            if (saved == true) finish()
        }

        findViewById<Button>(R.id.addSaveButton).setOnClickListener {
            val title = titleEdit.text.toString().trim()
            if (title.isEmpty()) {
                titleEdit.error = getString(R.string.error_empty_title)
                return@setOnClickListener
            }

            val task = Task(
                title = title,
                description = descEdit.text.toString().trim(),
                date = dateEdit.text.toString().trim().ifEmpty { "2026-05-01" },
                time = timeEdit.text.toString().trim().ifEmpty { "09:00" }
            )

            viewModel.addTask(task)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}