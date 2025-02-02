import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun FloatingMenu(
    onAddTask: () -> Unit,
    onAddHabit: () -> Unit,
    onAddRoutine: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Menu items - only visible when expanded
        if (expanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                // Task FAB
                FloatingActionButton(
                    onClick = {
                        onAddTask()
                        expanded = false
                    },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = MaterialTheme.colors.secondary
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Assignment,
                        contentDescription = "Add Task"
                    )
                }
                
                // Habit FAB
                FloatingActionButton(
                    onClick = {
                        onAddHabit()
                        expanded = false
                    },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = MaterialTheme.colors.secondary
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Loop,
                        contentDescription = "Add Habit"
                    )
                }
                
                // Routine FAB
                FloatingActionButton(
                    onClick = {
                        onAddRoutine()
                        expanded = false
                    },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = MaterialTheme.colors.secondary
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Add Routine"
                    )
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
    }
} 