package net.tomczek.todo.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

class TodoList(val name: String, vararg todos: Todo = emptyArray()) {

    private val _todos: MutableList<Todo> = mutableStateListOf()

    init {
        this._todos.addAll(todos)
    }

    fun add(todo: Todo): Todo {
        this._todos.add(todo)
        return todo
    }

    fun getOne(id: UUID): Todo? {
        return this._todos.find { it.id == id }
    }

    fun getAll(): List<Todo> {
        return this._todos
    }

    fun remove(id: UUID): Boolean {
        return this._todos.removeIf {it.id == id  }
    }

    fun update(todo: Todo): Todo? {
        val index = this._todos.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            this._todos[index] = todo
            Log.i("TodoList", "Updated todo: ${todo.title}")
        } else {
            return null
        }
        return todo
    }
}
