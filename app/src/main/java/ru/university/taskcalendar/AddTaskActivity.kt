package ru.university.taskcalendar

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTaskActivity : AppCompatActivity() {

    private val tag = "AddTaskActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val titleEdit = findViewById<EditText>(R.id.addTitleEdit)
        val descEdit = findViewById<EditText>(R.id.addDescriptionEdit)
        val dateEdit = findViewById<EditText>(R.id.addDateEdit)
        val timeEdit = findViewById<EditText>(R.id.addTimeEdit)

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

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val newId = AppDatabase.getInstance(this@AddTaskActivity)
                        .taskDao()
                        .insert(task)
                    Log.d(tag, "Задача вставлена с id=$newId")
                }
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}