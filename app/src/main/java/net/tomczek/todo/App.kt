package net.tomczek.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.tomczek.todo.model.ListStore
import net.tomczek.todo.model.Todo
import net.tomczek.todo.model.TodoList
import java.util.UUID

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val initialListStore = ListStore()
            initialListStore.addList(
                name = "default",
                Todo(title = "IOS rocks", notes = "IOS is much better!")
            )
            TodoApp(initialListStore = initialListStore)
        }
    }
}

@Composable
fun TodoApp(initialListStore: ListStore = ListStore()) {
    val todoAppState = TodoAppState()
    val currentList = initialListStore.getList(todoAppState.activeList.value)

    toShowList(todoAppState, currentList)
    TodoScaffold(todoAppState = todoAppState)
}

fun toCreateTodo(todoAppState: TodoAppState, activeList: TodoList) {
    todoAppState.topBar.value = {
        AppHeaderBar(title = "Aufgabe erstellen", cancelFn = {
            toShowList(todoAppState, activeList)
        })
    }
    todoAppState.content.value = {
        TodoForm(initialTodo = Todo(), modifier = Modifier.padding(8.dp), onTodoSaved = { todo ->
            activeList.add(todo)
            toShowList(todoAppState, activeList)
        })
    }
}

fun toShowList(todoAppState: TodoAppState, activeList: TodoList) {
    todoAppState.topBar.value = {
        AppHeaderBar(title = activeList.name, addFn = {
            toCreateTodo(todoAppState, activeList)
        })
    }
    todoAppState.content.value = {
        TodoListComp(
            list = activeList,
            delFn = { id -> activeList.remove(id) },
            editFn = { id ->
                val todo = activeList.getOne(id)
                if (todo != null) {
                    toEditTodo(
                        todoAppState = todoAppState,
                        activeList = activeList,
                        todo = todo
                    )
                }
            }
        )
    }
}

fun toEditTodo(todoAppState: TodoAppState, activeList: TodoList, todo: Todo) {
    todoAppState.topBar.value = {
        AppHeaderBar(title = "Aufgabe bearbeiten", cancelFn = {
            toShowList(todoAppState, activeList)
        })
    }
    todoAppState.content.value = {
        TodoForm(
            initialTodo = todo,
            onTodoSaved = { updatedTodo ->
                activeList.update(updatedTodo)
                toShowList(todoAppState, activeList)
            },
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun TodoScaffold(todoAppState: TodoAppState) {
    Scaffold(
        topBar = todoAppState.topBar.value
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            todoAppState.content.value()
        }
    }
}

@Preview
@Composable
fun AppHeaderBarPreview() {
    AppHeaderBar(title = "Todo List")
}

@Composable
fun AppHeaderBar(title: String = "", addFn: (() -> Unit)? = null, cancelFn: (() -> Unit)? = null) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RectangleShape
            ) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
            }
            Text(
                text = title, modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(), fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )
            if (addFn != null) {
                Button(
                    onClick = { addFn() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RectangleShape
                ) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add todo")
                }
            } else if (cancelFn != null) {
                Button(
                    onClick = { cancelFn() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RectangleShape
                ) {
                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Cancel")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoListCompPreview() {
    val todo1 = Todo("IOS rocks", "IOS is much better!")
    val todo2 = Todo("Android 2nd place", "Android is only second place")
    val todo3 = Todo("Google = Black Rock", "Google is completely owned by black rock")
    val todo4 = Todo("I am broke", "That's why I use android")
    val list = TodoList(name = "default", todo1, todo2, todo3, todo4)
    TodoListComp(list = list)
}

@Composable
fun TodoListComp(list: TodoList, delFn: (id: UUID) -> Unit = {}, editFn: (id: UUID) -> Unit = {}) {
    LazyColumn(modifier = Modifier.padding(4.dp)) {
        items(list.getAll(), key = { todo -> todo.id }) { todo ->
            TodoCard(
                todo = todo,
                modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 4.dp),
                delFn = { delFn(todo.id) },
                editFn = { editFn(todo.id) },
                completedChanged = { completed ->
                    list.update(todo.copy(completed = completed))
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoCardPreview() {
    TodoCard(todo = Todo(
        "IOS rocks",
        "IOS is much better, beacuse a lot of reasons i don't want to tell now."
    ),
        editFn = {},
        delFn = {})
}

@Composable
fun TodoCard(
    todo: Todo,
    modifier: Modifier = Modifier,
    delFn: (() -> Unit)? = null,
    editFn: (() -> Unit)? = null,
    completedChanged: ((Boolean) -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(percent = 20),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Checkbox(
                    checked = todo.completed,
                    onCheckedChange = { completedChanged?.invoke(it) })
                Column(
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 0.dp, 0.dp)
                        .fillMaxWidth(0.5f)
                ) {
                    Text(
                        text = todo.title,
                        fontWeight = MaterialTheme.typography.headlineMedium.fontWeight
                    )
                    Text(
                        text = todo.notes,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                    )
                }
            }
            Row(
                modifier = Modifier.padding(4.dp, 0.dp, 8.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (editFn != null) {
                    IconButton(
                        onClick = editFn
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Create,
                            contentDescription = "Edit Button"
                        )
                    }
                }
                if (delFn != null) {
                    IconButton(
                        onClick = delFn
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Button"
                        )
                    }
                }
            }
        }
    }
}
