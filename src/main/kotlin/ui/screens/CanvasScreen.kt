import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.PlaylistAdd

@Composable
fun CanvasScreen(
    taskViewModel: TaskViewModel,
    habitViewModel: HabitViewModel,
    routineViewModel: RoutineViewModel,
    paddingValues: PaddingValues
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val habits by habitViewModel.habits.collectAsState()
    val routines by routineViewModel.routines.collectAsState()
    
    var showDialog by remember { mutableStateOf<DialogType?>(null) }

    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
        habitViewModel.loadHabits()
        routineViewModel.loadRoutines()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tasks
            items(tasks) { task ->
                ItemCard(
                    title = task.name,
                    description = task.description,
                    icon = Icons.Outlined.Assignment,
                    isCompleted = task.isCompleted,
                    onToggle = { taskViewModel.updateTask(task.copy(isCompleted = !task.isCompleted)) }
                )
            }

            // Habits
            items(habits) { habit ->
                ItemCard(
                    title = habit.name,
                    description = "${habit.description}\n🔥 Streak: ${habit.streak}",
                    icon = Icons.Outlined.Loop,
                    isCompleted = habit.isCompleted,
                    onToggle = { habitViewModel.updateHabit(habit.copy(isCompleted = !habit.isCompleted)) }
                )
            }

            // Routines
            items(routines) { routine ->
                ItemCard(
                    title = routine.name,
                    description = "Type: ${routine.type}\nTasks: ${routine.tasks.size}, Habits: ${routine.habits.size}",
                    icon = Icons.Outlined.Schedule,
                    isCompleted = false,
                    onToggle = { /* Routines don't have completion state */ }
                )
            }
        }

        FloatingMenu(
            onAddTask = { showDialog = DialogType.Task },
            onAddHabit = { showDialog = DialogType.Habit },
            onAddRoutine = { showDialog = DialogType.Routine }
        )
    }

    // Dialogs
    when (showDialog) {
        DialogType.Task -> {
            AddTaskDialog(
                onDismiss = { showDialog = null },
                onTaskAdded = { task ->
                    taskViewModel.addTask(task)
                    showDialog = null
                }
            )
        }
        DialogType.Habit -> {
            AddHabitDialog(
                onDismiss = { showDialog = null },
                onHabitAdded = { habit ->
                    habitViewModel.addHabit(habit)
                    showDialog = null
                }
            )
        }
        DialogType.Routine -> {
            AddRoutineDialog(
                onDismiss = { showDialog = null },
                onRoutineAdded = { routine ->
                    routineViewModel.addRoutine(routine)
                    showDialog = null
                },
                currentType = RoutineType.MORNING
            )
        }
        null -> { /* No dialog shown */ }
    }
}

private enum class DialogType {
    Task, Habit, Routine
}

@Composable
private fun ItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6
                )
                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
            
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (isCompleted) "Mark incomplete" else "Mark complete",
                    tint = if (isCompleted) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
} 