import androidx.compose.material.*
import androidx.compose.runtime.Composable
import data.model.Habit
import data.model.Task

@Composable
fun DeleteConfirmationDialog(
    item: Any,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Confirmation") },
        text = {
            Text(
                text = when (item) {
                    is Task -> "Are you sure you want to delete task '${item.name}'?"
                    is Habit -> "Are you sure you want to delete habit '${item.name}'?"
                    else -> "Are you sure you want to delete this item?"
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 