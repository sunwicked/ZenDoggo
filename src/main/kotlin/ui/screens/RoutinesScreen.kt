import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.Habit
import data.model.Routine
import data.model.RoutineType
import data.model.Task
import java.time.LocalTime

@Composable
fun RoutinesScreen(
    viewModel: RoutineViewModel,
    taskViewModel: TaskViewModel,
    habitViewModel: HabitViewModel,
    paddingValues: PaddingValues
) {
    val routines by viewModel.routines.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()
    val habits by habitViewModel.habits.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditItemsDialog by remember { mutableStateOf<Routine?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Routine?>(null) }

    LaunchedEffect(selectedType) {
        viewModel.loadRoutines(selectedType)
        taskViewModel.loadTasks()
        habitViewModel.loadHabits()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Routine Type Selector
        ScrollableTabRow(
            selectedTabIndex = RoutineType.values().indexOf(selectedType),
            modifier = Modifier.fillMaxWidth()
        ) {
            RoutineType.values().forEach { type ->
                Tab(
                    selected = type == selectedType,
                    onClick = { viewModel.setSelectedType(type) },
                    text = { Text(type.name) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(routines) { routine ->
                    RoutineItem(
                        routine = routine,
                        onEditClick = { showEditItemsDialog = routine },
                        onDeleteClick = { showDeleteConfirmation = routine }
                    )
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Routine")
            }
        }
    }

    if (showAddDialog) {
        AddRoutineDialog(
            onDismiss = { showAddDialog = false },
            onRoutineAdded = { routine ->
                viewModel.addRoutine(routine)
                showAddDialog = false
            },
            currentType = selectedType
        )
    }

    // Show edit items dialog
    showEditItemsDialog?.let { routine ->
        AddItemsToRoutineDialog(
            onDismiss = { showEditItemsDialog = null },
            onSave = { selectedTasks, selectedHabits ->
                viewModel.updateRoutine(
                    routine.copy(
                        tasks = selectedTasks,
                        habits = selectedHabits
                    )
                )
            },
            availableTasks = tasks,
            availableHabits = habits,
            currentRoutine = routine
        )
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
                        viewModel.deleteRoutine(routine.id)
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
fun AddRoutineDialog(
    onDismiss: () -> Unit,
    onRoutineAdded: (Routine) -> Unit,
    currentType: RoutineType
) {
    var routineName by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New ${currentType.name} Routine") },
        text = {
            Column {
                TextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                // TODO: Add time pickers for startTime and endTime
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (routineName.isNotBlank()) {
                        onRoutineAdded(
                            Routine(
                                name = routineName,
                                type = currentType,
                                startTime = startTime,
                                endTime = endTime
                            )
                        )
                    }
                },
                enabled = routineName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
