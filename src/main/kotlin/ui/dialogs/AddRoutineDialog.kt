import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.Habit
import data.model.Routine
import data.model.RoutineType
import data.model.Task
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Composable
fun AddRoutineDialog(
    routine: Routine? = null,  // null for creation, non-null for editing
    onDismiss: () -> Unit,
    onRoutineAdded: (Routine) -> Unit = {},  // for creation
    onRoutineSaved: (Routine) -> Unit = {},  // for editing
    currentType: RoutineType = RoutineType.MORNING,
    availableTasks: List<Task>,
    availableHabits: List<Habit>
) {
    var routineName by remember { mutableStateOf(routine?.name ?: "") }
    var selectedType by remember { mutableStateOf(routine?.type ?: currentType) }
    var startTime by remember { mutableStateOf(routine?.startTime) }
    var endTime by remember { mutableStateOf(routine?.endTime) }
    var selectedTasks by remember { mutableStateOf(routine?.tasks?.toSet() ?: emptySet()) }
    var selectedHabits by remember { mutableStateOf(routine?.habits?.toSet() ?: emptySet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (routine == null) "Add New Routine" else "Edit Routine") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Routine name input
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Routine type selection
                Column {
                    Text(
                        text = "Type",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoutineType.values().forEach { type ->
                            OutlinedButton(
                                onClick = { selectedType = type },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = if (selectedType == type) 
                                        MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                    else 
                                        MaterialTheme.colors.surface
                                )
                            ) {
                                Text(type.name)
                            }
                        }
                    }
                }

                // Time selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Start time
                    OutlinedButton(
                        onClick = { /* TODO: Show time picker */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(startTime?.toString() ?: "Start Time")
                    }
                    Spacer(Modifier.width(8.dp))
                    // End time
                    OutlinedButton(
                        onClick = { /* TODO: Show time picker */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(endTime?.toString() ?: "End Time")
                    }
                }

                // Tasks selection
                Column {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    availableTasks.forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedTasks.contains(task),
                                onCheckedChange = { checked ->
                                    selectedTasks = if (checked) {
                                        selectedTasks + task
                                    } else {
                                        selectedTasks - task
                                    }
                                }
                            )
                            Text(
                                text = task.name,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                // Habits selection
                Column {
                    Text(
                        text = "Habits",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    availableHabits.forEach { habit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedHabits.contains(habit),
                                onCheckedChange = { checked ->
                                    selectedHabits = if (checked) {
                                        selectedHabits + habit
                                    } else {
                                        selectedHabits - habit
                                    }
                                }
                            )
                            Text(
                                text = habit.name,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (routineName.isNotBlank()) {
                        val newRoutine = Routine(
                            id = routine?.id ?: UUID.randomUUID().toString(),
                            name = routineName,
                            type = selectedType,
                            startTime = startTime,
                            endTime = endTime,
                            tasks = selectedTasks.toList(),
                            habits = selectedHabits.toList()
                        )
                        if (routine == null) {
                            onRoutineAdded(newRoutine)
                        } else {
                            onRoutineSaved(newRoutine)
                        }
                    }
                },
                enabled = routineName.isNotBlank()
            ) {
                Text(if (routine == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 