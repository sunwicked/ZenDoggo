import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@Composable
fun RoutinesScreen(
    viewModel: RoutineViewModel,
    paddingValues: PaddingValues
) {
    val routines by viewModel.routines.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedType) {
        viewModel.loadRoutines(selectedType)
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
                        onEditClick = { /* TODO: Implement edit */ }
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
}

@Composable
private fun RoutineItem(
    routine: Routine,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.h6
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Routine")
                }
            }

            // Time display
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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

            // Tasks and Habits lists
            if (routine.tasks.isNotEmpty()) {
                Text(
                    text = "Tasks (${routine.tasks.size})",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(top = 8.dp)
                )
                routine.tasks.forEach { task ->
                    Text(
                        text = "• ${task.name}",
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            if (routine.habits.isNotEmpty()) {
                Text(
                    text = "Habits (${routine.habits.size})",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(top = 8.dp)
                )
                routine.habits.forEach { habit ->
                    Text(
                        text = "• ${habit.name}",
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
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