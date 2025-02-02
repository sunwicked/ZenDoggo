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
import data.model.Habit
import data.model.Routine
import data.model.RoutineType
import data.model.Task

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
    var showDeleteConfirmation by remember { mutableStateOf<Routine?>(null) }

    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
        habitViewModel.loadHabits()
        routineViewModel.loadAllRoutines()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Morning Routines Section
            item {
                SectionHeader(
                    title = "Morning Routines",
                    icon = Icons.Outlined.WbSunny
                )
            }
            items(routines.filter { it.type == RoutineType.MORNING }) { routine ->
                RoutineCard(
                    routine = routine,
                    onTaskToggle = { task -> 
                        taskViewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
                    },
                    onHabitToggle = { habit ->
                        habitViewModel.updateHabit(habit.copy(isCompleted = !habit.isCompleted))
                    },
                    onDelete = { showDeleteConfirmation = routine }
                )
            }

            // Tasks Section
            item {
                SectionHeader(
                    title = "Tasks",
                    icon = Icons.Outlined.Assignment,
                    count = tasks.size
                )
            }
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    onToggle = { taskViewModel.updateTask(task.copy(isCompleted = !task.isCompleted)) }
                )
            }

            // Habits Section
            item {
                SectionHeader(
                    title = "Habits",
                    icon = Icons.Outlined.Loop,
                    count = habits.size
                )
            }
            items(habits) { habit ->
                HabitCard(
                    habit = habit,
                    onToggle = { habitViewModel.updateHabit(habit.copy(isCompleted = !habit.isCompleted)) }
                )
            }

            // Evening Routines Section
            item {
                SectionHeader(
                    title = "Evening Routines",
                    icon = Icons.Outlined.NightsStay
                )
            }
            items(routines.filter { it.type == RoutineType.EVENING }) { routine ->
                RoutineCard(
                    routine = routine,
                    onTaskToggle = { task -> 
                        taskViewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
                    },
                    onHabitToggle = { habit ->
                        habitViewModel.updateHabit(habit.copy(isCompleted = !habit.isCompleted))
                    },
                    onDelete = { showDeleteConfirmation = routine }
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
                currentType = RoutineType.MORNING,
                availableTasks = tasks,
                availableHabits = habits
            )
        }
        null -> { /* No dialog shown */ }
    }

    // Add delete confirmation dialog
    showDeleteConfirmation?.let { routine ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Routine") },
            text = { Text("Are you sure you want to delete '${routine.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        routineViewModel.deleteRoutine(routine.id)
                        showDeleteConfirmation = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 8.dp)
        )
        if (count != null) {
            Text(
                text = "($count)",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "View all",
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun TaskCard(task: Task, onToggle: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.subtitle1
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
private fun RoutineCard(
    routine: Routine,
    onTaskToggle: (Task) -> Unit,
    onHabitToggle: (Habit) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = routine.name,
                        style = MaterialTheme.typography.h6
                    )
                    
                    routine.type.let { type ->
                        Text(
                            text = "Type: $type",
                            style = MaterialTheme.typography.subtitle2,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            RoutineDetails(
                routine = routine,
                onTaskToggle = onTaskToggle,
                onHabitToggle = onHabitToggle
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete routine"
                    )
                }
            }
        }
    }
}

@Composable
private fun RoutineDetails(
    routine: Routine,
    onTaskToggle: (Task) -> Unit,
    onHabitToggle: (Habit) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Show start and end times if set
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            routine.startTime?.let { startTime ->
                Text(
                    text = "Start: $startTime",
                    style = MaterialTheme.typography.caption
                )
            }
            routine.endTime?.let { endTime ->
                Text(
                    text = "End: $endTime",
                    style = MaterialTheme.typography.caption
                )
            }
        }

        // Tasks section
        if (routine.tasks.isNotEmpty()) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            routine.tasks.forEach { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ${task.name}",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onTaskToggle(task) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted) 
                                Icons.Outlined.CheckCircle 
                            else 
                                Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = if (task.isCompleted) "Mark incomplete" else "Mark complete",
                            tint = if (task.isCompleted) 
                                MaterialTheme.colors.primary 
                            else 
                                MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Habits section
        if (routine.habits.isNotEmpty()) {
            Text(
                text = "Habits",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            routine.habits.forEach { habit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "• ${habit.name}",
                            style = MaterialTheme.typography.body2
                        )
                        Text(
                            text = "🔥 ${habit.streak}",
                            style = MaterialTheme.typography.caption
                        )
                    }
                    IconButton(
                        onClick = { onHabitToggle(habit) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (habit.isCompleted) 
                                Icons.Outlined.CheckCircle 
                            else 
                                Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = if (habit.isCompleted) "Mark incomplete" else "Mark complete",
                            tint = if (habit.isCompleted) 
                                MaterialTheme.colors.primary 
                            else 
                                MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
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
    onToggle: () -> Unit,
    isExpandable: Boolean = false,
    expandedContent: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
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
                    
                    description.split('\n').forEach { line ->
                        when {
                            line.startsWith("Type:") -> Text(
                                text = line,
                                style = MaterialTheme.typography.subtitle2,
                                color = MaterialTheme.colors.primary
                            )
                            line.startsWith("Start:") || line.startsWith("End:") -> Text(
                                text = line,
                                style = MaterialTheme.typography.caption
                            )
                            line.startsWith("Tasks:") || line.startsWith("Habits:") -> Text(
                                text = line,
                                style = MaterialTheme.typography.subtitle1,
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            line.startsWith("•") -> Text(
                                text = line,
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            else -> Text(
                                text = line,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                }
                
                if (isExpandable) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            contentDescription = if (expanded) "Show less" else "Show more"
                        )
                    }
                }
            }

            if (expanded && expandedContent != null) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                expandedContent()
            }
        }
    }
}

@Composable
private fun HabitCard(habit: Habit, onToggle: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.subtitle1
                )
                if (habit.description.isNotBlank()) {
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Checkbox(
                checked = habit.isCompleted,
                onCheckedChange = { onToggle() }
            )
        }
    }
} 