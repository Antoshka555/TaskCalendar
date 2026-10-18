package ru.university.taskcalendar

import kotlinx.coroutines.flow.Flow


class TaskRepository(private val taskDao: TaskDao) {


    val tasks: Flow<List<Task>> = taskDao.observeAll()

    // --- CREATE ---
    suspend fun addTask(task: Task): Long = taskDao.insert(task)

    suspend fun addAll(tasks: List<Task>) = taskDao.insertAll(tasks)

    // --- UPDATE ---
    suspend fun updateTask(task: Task) = taskDao.update(task)

    // --- DELETE ---
    suspend fun deleteTask(task: Task) = taskDao.delete(task)

    suspend fun deleteById(id: Long) = taskDao.deleteById(id)

    // --- READ ---
    suspend fun getAll(): List<Task> = taskDao.getAll()

    suspend fun getById(id: Long): Task? = taskDao.getById(id)

    suspend fun getByDate(date: String): List<Task> = taskDao.getByDate(date)
}