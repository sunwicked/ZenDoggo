import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.Task
import data.model.Habit
import data.model.Routine
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Search

@Composable
fun AddItemsToRoutineDialog(
    onDismiss: () -> Unit,
    onSave: (List<Task>, List<Habit>) -> Unit,
    availableTasks: List<Task>,
    availableHabits: List<Habit>,
    currentRoutine: Routine
) {
    var selectedTasks by remember { mutableStateOf(currentRoutine.tasks.toSet()) }
    var selectedHabits by remember { mutableStateOf(currentRoutine.habits.toSet()) }
    var searchQuery by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Items to Routine") },
        text = {
            Column {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Tabs
                TabRow(selectedTabIndex = activeTab) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Tasks (${selectedTasks.size})") }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Habits (${selectedHabits.size})") }
                    )
                }

                when (activeTab) {
                    0 -> TasksList(
                        tasks = availableTasks.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true)
                        },
                        selectedTasks = selectedTasks,
                        onSelectionChanged = { task, selected ->
                            selectedTasks = if (selected) {
                                selectedTasks + task
                            } else {
                                selectedTasks - task
                            }
                        }
                    )
                    1 -> HabitsList(
                        habits = availableHabits.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true)
                        },
                        selectedHabits = selectedHabits,
                        onSelectionChanged = { habit, selected ->
                            selectedHabits = if (selected) {
                                selectedHabits + habit
                            } else {
                                selectedHabits - habit
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(selectedTasks.toList(), selectedHabits.toList())
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TasksList(
    tasks: List<Task>,
    selectedTasks: Set<Task>,
    onSelectionChanged: (Task, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .height(300.dp)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(tasks) { task ->
            SelectableItemCard(
                title = task.name,
                description = task.description,
                isSelected = task in selectedTasks,
                onSelectionChanged = { selected -> onSelectionChanged(task, selected) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                },
                trailingContent = if (task.isCompleted) {
                    {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )
        }
    }
}

@Composable
private fun HabitsList(
    habits: List<Habit>,
    selectedHabits: Set<Habit>,
    onSelectionChanged: (Habit, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .height(300.dp)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(habits) { habit ->
            SelectableItemCard(
                title = habit.name,
                description = habit.description,
                isSelected = habit in selectedHabits,
                onSelectionChanged = { selected -> onSelectionChanged(habit, selected) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Loop,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔥 ${habit.streak}",
                            style = MaterialTheme.typography.caption
                        )
                        if (habit.isCompleted) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Completed",
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SelectableItemCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    leadingIcon: @Composable () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChanged(!isSelected) },
        elevation = if (isSelected) 4.dp else 1.dp,
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colors.primary)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged
            )
            leadingIcon()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = title)
                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            trailingContent?.invoke()
        }
    }
} 