import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.Habit
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun HabitDialog(
    habit: Habit? = null,
    onDismiss: () -> Unit,
    onHabitAdded: (Habit) -> Unit = {},
    onHabitSaved: (Habit) -> Unit = {}
) {
    var habitName by remember { mutableStateOf(habit?.name ?: "") }
    var habitDescription by remember { mutableStateOf(habit?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (habit == null) "Add New Habit" else "Edit Habit") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    label = { Text("Habit Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = habitDescription,
                    onValueChange = { habitDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (habitName.isNotBlank()) {
                        val newHabit = Habit(
                            id = habit?.id ?: UUID.randomUUID().toString(),
                            name = habitName,
                            description = habitDescription,
                            isCompleted = habit?.isCompleted ?: false,
                            streak = habit?.streak ?: 0,
                            createdAt = habit?.createdAt ?: LocalDateTime.now()
                        )
                        if (habit == null) {
                            onHabitAdded(newHabit)
                        } else {
                            onHabitSaved(newHabit)
                        }
                    }
                },
                enabled = habitName.isNotBlank()
            ) {
                Text(if (habit == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 