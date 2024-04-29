package net.tomczek.todo

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.tomczek.todo.components.TodoOutlinedTextField
import net.tomczek.todo.model.Todo
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Preview(showBackground = true)
@Composable
fun TodoFormPreview() {
    TodoForm(initialTodo = Todo())
}

@Composable
fun TodoForm(initialTodo: Todo, modifier: Modifier = Modifier, onTodoSaved: (Todo) -> Unit = {}) {
    var title by remember { mutableStateOf(initialTodo.title) }
    var description by remember { mutableStateOf(initialTodo.notes) }
    var dueDate by remember { mutableStateOf(initialTodo.dueDate) }
    var completed by remember { mutableStateOf(initialTodo.completed) }

    Column(modifier = modifier) {
        Label(text = "Titel")
        TodoOutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text(text = "Titel") },
            isError = !isValidTodoTitle(title),
            errorText = if (isValidTodoTitle(title)) "" else "Der Titel muss mindestens 3 Zeichen lang sein.",
            modifier = Modifier
                .fillMaxWidth()
        )
        Label(text = "Notizen")
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Notizen") },
            modifier = Modifier
                .fillMaxWidth()
        )
        Label(text = "Fälligkeit")
        Row {
            DatePickerField(initialDate = initialTodo.dueDate, valueChanged = {date -> dueDate = date})
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = completed, onCheckedChange = { completed = it })
            Text(
                text = "Erledigt",
                modifier = Modifier.clickable { completed = !completed })
        }
        ElevatedButton(onClick = {
            onTodoSaved(
                Todo(
                    id = initialTodo.id,
                    title = title,
                    notes = description,
                    dueDate = dueDate,
                    completed = completed
                )
            )
        }) {
            Text(text = "Speichern")
        }
    }
}

fun isValidTodoTitle(title: String): Boolean {
    return title.isNotBlank() && title.length >= 3
}

@Preview(showBackground = true)
@Composable
fun DatePickerFieldPreview() {
    DatePickerField()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(initialDate: LocalDate = LocalDate.now(), valueChanged: (LocalDate) -> Unit = {}) {
    val datePickerState = rememberDatePickerState()
    var dateValue by remember { mutableStateOf(initialDate) }
    var datePickerOpen by remember { mutableStateOf(false) }

    if (datePickerOpen) {
        DatePickerDialog(
            onDismissRequest = { datePickerOpen = false },
            dismissButton = {
                Button(onClick = { datePickerOpen = false }) {
                    Text(text = "Abbrechen")
                }
            },
            confirmButton = {
                Button(onClick = {
                    datePickerOpen = false
                    dateValue = toLocalDate(dateValue, datePickerState.selectedDateMillis)
                    valueChanged(dateValue)
                }) {
                    Text(text = "OK")
                }
            }) {
            DatePicker(state = datePickerState)
        }
    }

    Row {
        OutlinedTextField(
            value = toDateString(dateValue),
            onValueChange = { dateValue = toLocalDate(it) },
            placeholder = { Text("Datum") }
        )
        OutlinedButton(
            onClick = { datePickerOpen = true }, shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .offset(
                    (-72).dp, 1.dp
                )
                .height(56.dp)
        ) {
            Icon(imageVector = Icons.Outlined.DateRange, contentDescription = "Datum auswählen")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LabelPreview() {
    Label(text = "Titel")
}

@Composable
fun Label(text: String) {
    Text(
        text = text, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 0.dp, 8.dp),
        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        fontWeight = FontWeight.Bold
    )
}

fun toLocalDate(fallbackDate: LocalDate, milliseconds: Long?): LocalDate {
    return if (milliseconds != null) {
        Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDate()
    } else {
        fallbackDate
    }
}

fun toDateString(date: LocalDate): String {
    return date.toString()
}

fun toLocalDate(dateString: String): LocalDate {
    return LocalDate.parse(dateString)
}
