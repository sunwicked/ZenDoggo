import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LowPriority
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.model.Priority
import data.model.Task
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun TaskDialog(
    task: Task? = null,  // null for creation, non-null for editing
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit = {},  // for creation
    onTaskSaved: (Task) -> Unit = {}   // for editing
) {
    var taskName by remember { mutableStateOf(task?.name ?: "") }
    var taskDescription by remember { mutableStateOf(task?.description ?: "") }
    var selectedPriority by remember { mutableStateOf(task?.priority ?: Priority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add New Task" else "Edit Task") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Task name input
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Task description input
                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Priority selection
                Column {
                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriorityButton(
                            priority = Priority.HIGH,
                            selected = selectedPriority == Priority.HIGH,
                            onClick = { selectedPriority = Priority.HIGH },
                            modifier = Modifier.weight(1f)
                        )
                        PriorityButton(
                            priority = Priority.MEDIUM,
                            selected = selectedPriority == Priority.MEDIUM,
                            onClick = { selectedPriority = Priority.MEDIUM },
                            modifier = Modifier.weight(1f)
                        )
                        PriorityButton(
                            priority = Priority.LOW,
                            selected = selectedPriority == Priority.LOW,
                            onClick = { selectedPriority = Priority.LOW },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskName.isNotBlank()) {
                        val newTask = Task(
                            id = task?.id ?: UUID.randomUUID().toString(),
                            name = taskName,
                            description = taskDescription,
                            priority = selectedPriority,
                            isCompleted = task?.isCompleted ?: false,
                            progress = task?.progress ?: 0f,
                            timeScheduled = task?.timeScheduled,
                            createdAt = task?.createdAt ?: LocalDateTime.now()
                        )
                        if (task == null) {
                            onTaskAdded(newTask)
                        } else {
                            onTaskSaved(newTask)
                        }
                    }
                },
                enabled = taskName.isNotBlank()
            ) {
                Text(if (task == null) "Add" else "Save")
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
private fun PriorityButton(
    priority: Priority,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (priority) {
        Priority.HIGH -> Color(0xFFE57373)
        Priority.MEDIUM -> Color(0xFFFFB74D)
        Priority.LOW -> Color(0xFF81C784)
    }.copy(alpha = if (selected) 0.3f else 0.1f)

    val borderColor = when (priority) {
        Priority.HIGH -> Color(0xFFE57373)
        Priority.MEDIUM -> Color(0xFFFFB74D)
        Priority.LOW -> Color(0xFF81C784)
    }.copy(alpha = if (selected) 0.8f else 0.3f)

    val icon = when (priority) {
        Priority.HIGH -> Icons.Outlined.PriorityHigh
        Priority.MEDIUM -> Icons.Outlined.Sort
        Priority.LOW -> Icons.Outlined.LowPriority
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = backgroundColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (selected) Color.White else Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = priority.name,
                color = if (selected) Color.White else Color.White.copy(alpha = 0.7f)
            )
        }
    }
} 