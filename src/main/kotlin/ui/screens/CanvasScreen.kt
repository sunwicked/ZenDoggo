import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import data.model.Habit
import data.model.Routine
import data.model.RoutineType
import data.model.Task
import kotlin.math.roundToInt

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

    // Canvas state
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Add position state for each item
    val positions = remember { mutableStateMapOf<String, Offset>() }
    
    // Initialize positions if not set
    LaunchedEffect(tasks, habits, routines) {
        // Arrange items in a grid-like pattern
        var x = 100f
        var y = 100f
        
        routines.forEach { routine ->
            if (routine.id !in positions) {
                positions[routine.id] = Offset(x, y)
                x += 400f  // Space for large cards
                if (x > 1200f) {  // Wrap to next row
                    x = 100f
                    y += 300f
                }
            }
        }
        
        tasks.forEach { task ->
            if (task.id !in positions) {
                positions[task.id] = Offset(x, y)
                x += 250f  // Space for smaller cards
                if (x > 1200f) {
                    x = 100f
                    y += 150f
                }
            }
        }
        
        habits.forEach { habit ->
            if (habit.id !in positions) {
                positions[habit.id] = Offset(x, y)
                x += 250f
                if (x > 1200f) {
                    x = 100f
                    y += 150f
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
        habitViewModel.loadHabits()
        routineViewModel.loadAllRoutines()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFF1E1E1E))  // Dark background
    ) {
        // Canvas area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 2f)
                        offset += pan
                    }
                }
        ) {
            CanvasGrid()
            
            // Draw routines
            routines.forEach { routine ->
                DraggableCard(
                    modifier = Modifier
                        .offset { 
                            IntOffset(
                                positions[routine.id]?.x?.roundToInt() ?: 0,
                                positions[routine.id]?.y?.roundToInt() ?: 0
                            )
                        }
                        .width(380.dp),
                    onPositionChange = { offset ->
                        positions[routine.id] = offset
                    }
                ) {
                    RoutineCard(
                        routine = routine,
                        onTaskToggle = { task -> 
                            taskViewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
                        },
                        onHabitToggle = { habit ->
                            habitViewModel.updateHabit(habit.copy(isCompleted = !habit.isCompleted))
                        }
                    )
                }
            }

            // Draw tasks
            tasks.forEach { task ->
                DraggableCard(
                    modifier = Modifier
                        .offset { 
                            IntOffset(
                                positions[task.id]?.x?.roundToInt() ?: 0,
                                positions[task.id]?.y?.roundToInt() ?: 0
                            )
                        }
                        .width(230.dp),
                    onPositionChange = { offset ->
                        positions[task.id] = offset
                    }
                ) {
                    TaskCard(
                        task = task,
                        onToggle = { taskViewModel.updateTask(task.copy(isCompleted = !task.isCompleted)) }
                    )
                }
            }

            // Draw habits
            habits.forEach { habit ->
                DraggableCard(
                    modifier = Modifier
                        .offset { 
                            IntOffset(
                                positions[habit.id]?.x?.roundToInt() ?: 0,
                                positions[habit.id]?.y?.roundToInt() ?: 0
                            )
                        }
                        .width(230.dp),
                    onPositionChange = { offset ->
                        positions[habit.id] = offset
                    }
                ) {
                    HabitCard(
                        habit = habit,
                        onToggle = { habitViewModel.updateHabit(habit.copy(isCompleted = !habit.isCompleted)) }
                    )
                }
            }
        }

        // Controls overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Zoom controls
            ZoomControls(
                scale = scale,
                onZoomIn = { scale = (scale * 1.2f).coerceIn(0.5f, 2f) },
                onZoomOut = { scale = (scale / 1.2f).coerceIn(0.5f, 2f) },
                onReset = { 
                    scale = 1f
                    offset = Offset.Zero
                },
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // Add button
            FloatingActionButton(
                onClick = { showDialog = DialogType.Task },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Add, "Add Item")
            }
        }
    }

    // Show appropriate dialog based on selection
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
}

@Composable
private fun RoutineCard(
    routine: Routine,
    onTaskToggle: (Task) -> Unit,
    onHabitToggle: (Habit) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = routine.name,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = routine.type.name,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.primary
            )
            
            if (routine.tasks.isNotEmpty()) {
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(top = 8.dp)
                )
                routine.tasks.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• ${task.name}",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { onTaskToggle(task) }
                        )
                    }
                }
            }

            if (routine.habits.isNotEmpty()) {
                Text(
                    text = "Habits",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(top = 8.dp)
                )
                routine.habits.forEach { habit ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                        Checkbox(
                            checked = habit.isCompleted,
                            onCheckedChange = { onHabitToggle(habit) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
private fun HabitCard(
    habit: Habit,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                Text(
                    text = "🔥 ${habit.streak}",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Checkbox(
                checked = habit.isCompleted,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
private fun CanvasGrid() {
    val gridColor = Color.White.copy(alpha = 0.05f)  // Very subtle grid
    val gridSpacing = 50.dp  // Space between grid lines
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Draw vertical lines
        var x = 0f
        while (x < width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridSpacing.toPx()
        }
        
        // Draw horizontal lines
        var y = 0f
        while (y < height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridSpacing.toPx()
        }

        // Draw center lines slightly brighter
        val centerColor = Color.White.copy(alpha = 0.1f)
        drawLine(
            color = centerColor,
            start = Offset(width / 2, 0f),
            end = Offset(width / 2, height),
            strokeWidth = 1f
        )
        drawLine(
            color = centerColor,
            start = Offset(0f, height / 2),
            end = Offset(width, height / 2),
            strokeWidth = 1f
        )
    }
}

@Composable
private fun ZoomControls(
    scale: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            onClick = onZoomIn,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Default.ZoomIn, "Zoom In")
        }
        
        FloatingActionButton(
            onClick = onZoomOut,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Default.ZoomOut, "Zoom Out")
        }
        
        FloatingActionButton(
            onClick = onReset,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Default.CenterFocusStrong, "Reset View")
        }

        // Zoom percentage indicator
        Text(
            text = "${(scale * 100).roundToInt()}%",
            color = Color.White,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.surface.copy(alpha = 0.7f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(4.dp)
        )
    }
}

@Composable
private fun DraggableCard(
    modifier: Modifier = Modifier,
    onPositionChange: (Offset) -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    onPositionChange(Offset(offsetX, offsetY))
                }
            }
    ) {
        content()
    }
}

private enum class DialogType {
    Task, Habit, Routine
} 