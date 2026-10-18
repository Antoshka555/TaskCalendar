package ru.university.taskcalendar

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "TaskViewModel"


    private val repository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )


    val tasks: LiveData<List<Task>> = repository.tasks.asLiveData()



    private val _selectedTask = MutableLiveData<Task?>()
    val selectedTask: LiveData<Task?> = _selectedTask


    private val _closeScreen = MutableLiveData<Boolean>()
    val closeScreen: LiveData<Boolean> = _closeScreen


    private val _taskSaved = MutableLiveData<Boolean>()
    val taskSaved: LiveData<Boolean> = _taskSaved



    fun addTask(task: Task) {
        viewModelScope.launch {
            val id = withContext(Dispatchers.IO) { repository.addTask(task) }
            Log.d(tag, "addTask: вставлена задача id=$id title='${task.title}'")
            _taskSaved.value = true
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.updateTask(task) }
            Log.d(tag, "updateTask: обновлена задача id=${task.id}")
            _selectedTask.value = task
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.deleteTask(task) }
            Log.d(tag, "deleteTask: удалена задача id=${task.id}")
            _closeScreen.value = true
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.deleteById(id) }
            Log.d(tag, "deleteById: удалена задача id=$id")
            _closeScreen.value = true
        }
    }

    /** Переключает статус «выполнено / не выполнено». */
    fun toggleDone(task: Task) {
        updateTask(task.copy(isDone = !task.isDone))
    }

    /** Загружает одну задачу по id для экрана деталей. */
    fun loadTask(id: Long) {
        viewModelScope.launch {
            val task = withContext(Dispatchers.IO) { repository.getById(id) }
            Log.d(tag, "loadTask: id=$id, найдена=${task != null}")
            if (task == null) {
                _closeScreen.value = true
            } else {
                _selectedTask.value = task
            }
        }
    }

    // ---------- Демонстрация задания 14 (наполнение БД) ----------

    private var seedStarted = false

    /**
     * Наполняет БД стартовыми задачами ОДИН раз.
     * Флаг seedStarted живёт в ViewModel, а ViewModel переживает
     * поворот экрана — поэтому при повороте запрос в БД не повторяется.
     */
    fun seedIfEmptyOnce() {
        if (seedStarted) {
            Log.d(tag, "seedIfEmptyOnce: уже выполнялось, пропускаем")
            return
        }
        seedStarted = true

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existing = repository.getAll()
                Log.d(tag, "Проверка БД: найдено ${existing.size} задач")

                if (existing.isEmpty()) {
                    val app = getApplication<Application>()
                    val seed = listOf(
                        Task(
                            title = app.getString(R.string.task_1_title),
                            description = app.getString(R.string.task_1_desc),
                            date = "2026-05-01", time = "09:00"
                        ),
                        Task(
                            title = app.getString(R.string.task_2_title),
                            description = app.getString(R.string.task_2_desc),
                            date = "2026-05-02", time = "07:30", isDone = true
                        ),
                        Task(
                            title = app.getString(R.string.task_3_title),
                            description = app.getString(R.string.task_3_desc),
                            date = "2026-05-03", time = "19:00"
                        ),
                        Task(
                            title = app.getString(R.string.task_4_title),
                            description = app.getString(R.string.task_4_desc),
                            date = "2026-05-04", time = "20:00", isDone = true
                        ),
                        Task(
                            title = app.getString(R.string.task_5_title),
                            description = app.getString(R.string.task_5_desc),
                            date = "2026-05-05", time = "12:00"
                        )
                    )
                    repository.addAll(seed)
                    Log.d(tag, "Вставлено ${seed.size} стартовых задач")
                }

                repository.getAll().forEach { t ->
                    Log.d(
                        tag,
                        "task id=${t.id} title='${t.title}' date=${t.date} ${t.time} done=${t.isDone}"
                    )
                }

                val byDate = repository.getByDate("2026-05-02")
                Log.d(tag, "Задач на 2026-05-02: ${byDate.size}")
            }
        }
    }
}