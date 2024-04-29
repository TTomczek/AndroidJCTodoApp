package net.tomczek.todo.model

import androidx.compose.runtime.Immutable
import java.time.Clock
import java.time.LocalDate
import java.util.UUID

@Immutable
data class Todo(
    val title: String = "",
    val notes: String = "",
    val dueDate: LocalDate = LocalDate.now(Clock.systemUTC()),
    val completed: Boolean = false,
    val id: UUID = UUID.randomUUID()
)