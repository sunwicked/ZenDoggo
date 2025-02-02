import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.Routine

@Composable
fun RoutineItem(
    routine: Routine,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            onEditClick()
                            showMenu = false
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit Items")
                        }
                        DropdownMenuItem(onClick = {
                            onDeleteClick()
                            showMenu = false
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colors.error
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Delete",
                                color = MaterialTheme.colors.error
                            )
                        }
                    }
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

            // Tasks and Habits counts
            Text(
                text = "Tasks: ${routine.tasks.size}",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Habits: ${routine.habits.size}",
                style = MaterialTheme.typography.body2
            )
        }
    }
} 