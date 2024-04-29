package net.tomczek.todo.model;

class ListStore {

    private val _lists: MutableMap<String, TodoList> = mutableMapOf()
    val lists: Map<String, TodoList> = _lists

    fun addList(name: String, vararg todos: Todo): TodoList {
        val listNameAlreadyExists = this._lists.containsKey(name)
        if (listNameAlreadyExists) {
            throw IllegalArgumentException("List with name $name already exists")
        }

        val newList = TodoList(name = name, todos = todos)
        this._lists[name] = newList
        return newList
    }

    fun getList(name: String, createIfNotExists: Boolean = false): TodoList {
        val list = this._lists[name]

        if (list != null) {
            return list
        }

        if (createIfNotExists) {
            this._lists[name] = TodoList(name = name)
            val createdList = this._lists[name];
            if (createdList != null) {
                return createdList
            } else {
                throw IllegalStateException("List with name $name was not created")
            }
        } else {
            throw IllegalArgumentException("List with name $name does not exist")
        }
    }

    fun removeList(name: String): TodoList? {
        val listExists = this._lists.containsKey(name)
        if (!listExists) {
            return null
        }
        return this._lists.remove(name)
    }
}
