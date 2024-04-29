package net.tomczek.todo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

data class TodoAppState(val initialTopBar: @Composable () -> Unit = {}, val initialContent: @Composable () -> Unit = {}, val initialActiveList: String = "default") {

    var topBar = mutableStateOf(initialTopBar)

    val content = mutableStateOf(initialContent)

    val activeList = mutableStateOf(initialActiveList)
}