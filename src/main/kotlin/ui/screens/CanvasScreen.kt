import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LowPriority
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import data.model.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private object CanvasColors {
    // Background colors
    val canvasBackground = Color(0xFF1A1B1E)  // Darker background
    val gridColor = Color.White.copy(alpha = 0.04f)
    val gridCenterColor = Color.White.copy(alpha = 0.08f)

    // Routine card colors by type
    val morningCard = Color(0xFF1E88E5).copy(alpha = 0.15f)  // Soft blue
    val afternoonCard = Color(0xFFFFB300).copy(alpha = 0.15f) // Warm amber
    val eveningCard = Color(0xFF7B1FA2).copy(alpha = 0.15f)  // Rich purple
    val nightCard = Color(0xFF2C3E50).copy(alpha = 0.15f)    // Deep slate

    // Task and Habit colors
    val taskCard = Color(0xFF2196F3).copy(alpha = 0.12f)
    val taskBorder = Color(0xFF2196F3).copy(alpha = 0.25f)
    val habitCard = Color(0xFF66BB6A).copy(alpha = 0.12f)
    val habitBorder = Color(0xFF66BB6A).copy(alpha = 0.25f)
    
    // Status colors
    val completedIcon = Color(0xFF4CAF50)
    val timeChipBackground = Color.White.copy(alpha = 0.08f)
    val textPrimary = Color.White
    val textSecondary = Color.White.copy(alpha = 0.7f)

    // Priority colors
    val priorityHigh = Color(0xFFE57373).copy(alpha = 0.8f)    // Red
    val priorityMedium = Color(0xFFFFB74D).copy(alpha = 0.8f)  // Orange
    val priorityLow = Color(0xFF81C784).copy(alpha = 0.8f)     // Green
    
    // Progress colors
    val progressBackground = Color.White.copy(alpha = 0.1f)
    val progressFill = Color.White.copy(alpha = 0.3f)
}

@Composable
fun CanvasScreen(
    routineViewModel: RoutineViewModel,
    taskViewModel: TaskViewModel,
    habitViewModel: HabitViewModel,
    paddingValues: PaddingValues
) {
    val routines by routineViewModel.routines.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()
    val habits by habitViewModel.habits.collectAsState()
    
    // Canvas state
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showAddRoutine by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf<DialogType?>(null) }
    
    // Store routine positions
    val positions = remember { mutableStateMapOf<String, Offset>() }
    
    // Initialize positions in a grid layout
    LaunchedEffect(routines) {
        var x = 100f
        var y = 100f
        routines.forEach { routine ->
            if (routine.id !in positions) {
                positions[routine.id] = Offset(x, y)
                x += 400f
                if (x > 1200f) {
                    x = 100f
                    y += 300f
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        routineViewModel.loadAllRoutines()
        taskViewModel.loadTasks()
        habitViewModel.loadHabits()
    }

    var itemToEdit by remember { mutableStateOf<Any?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Any?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(CanvasColors.canvasBackground)
    ) {
        // Canvas with routines
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
                RoutineNode(
                    routine = routine,
                    position = positions[routine.id] ?: Offset.Zero,
                    onPositionChange = { positions[routine.id] = it },
                    onTaskEdit = { task -> itemToEdit = task },
                    onTaskDelete = { task -> showDeleteConfirmation = task },
                    onHabitEdit = { habit -> itemToEdit = habit },
                    onHabitDelete = { habit -> showDeleteConfirmation = habit }
                )
            }
        }

        // FAB Menu
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Expanded FAB menu
            AnimatedVisibility(
                visible = showFabMenu,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 72.dp)  // Space for main FAB
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Add Task FAB
                    ExtendedFloatingActionButton(
                        text = { Text("Add Task") },
                        icon = { Icon(Icons.Default.Assignment, "Add Task") },
                        onClick = { 
                            showDialog = DialogType.Task
                            showFabMenu = false
                        },
                        backgroundColor = MaterialTheme.colors.primary
                    )

                    // Add Habit FAB
                    ExtendedFloatingActionButton(
                        text = { Text("Add Habit") },
                        icon = { Icon(Icons.Default.Loop, "Add Habit") },
                        onClick = { 
                            showDialog = DialogType.Habit
                            showFabMenu = false
                        },
                        backgroundColor = MaterialTheme.colors.secondary
                    )

                    // Add Routine FAB
                    ExtendedFloatingActionButton(
                        text = { Text("Add Routine") },
                        icon = { Icon(Icons.Default.PlaylistAdd, "Add Routine") },
                        onClick = { 
                            showDialog = DialogType.Routine
                            showFabMenu = false
                        }
                    )
                }
            }

            // Main FAB
            FloatingActionButton(
                onClick = { showFabMenu = !showFabMenu },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    if (showFabMenu) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (showFabMenu) "Close menu" else "Open menu"
                )
            }
        }
    }

    // Dialogs
    when (showDialog) {
        DialogType.Task -> {
            TaskDialog(
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

    // Handle edit dialogs
    itemToEdit?.let { item ->
        when (item) {
            is Task -> TaskDialog(
                task = item,
                onDismiss = { itemToEdit = null },
                onTaskSaved = { updatedTask ->
                    taskViewModel.updateTask(updatedTask)
                    itemToEdit = null
                }
            )
            is Habit -> HabitDialog(
                habit = item,
                onDismiss = { itemToEdit = null },
                onHabitSaved = { updatedHabit ->
                    habitViewModel.updateHabit(updatedHabit)
                    itemToEdit = null
                }
            )
        }
    }

    // Handle delete confirmation
    showDeleteConfirmation?.let { item ->
        DeleteConfirmationDialog(
            item = item,
            onConfirm = {
                when (item) {
                    is Task -> taskViewModel.deleteTask(item.id)
                    is Habit -> habitViewModel.deleteHabit(item.id)
                }
                showDeleteConfirmation = null
            },
            onDismiss = { showDeleteConfirmation = null }
        )
    }
}

private enum class DialogType {
    Task, Habit, Routine
}

@Composable
private fun RoutineNode(
    routine: Routine,
    position: Offset,
    onPositionChange: (Offset) -> Unit,
    onTaskEdit: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit,
    onHabitEdit: (Habit) -> Unit,
    onHabitDelete: (Habit) -> Unit
) {
    DraggableCard(
        modifier = Modifier
            .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
            .width(380.dp),
        onPositionChange = onPositionChange
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            backgroundColor = when(routine.type) {
                RoutineType.MORNING -> CanvasColors.morningCard
                RoutineType.AFTERNOON -> CanvasColors.afternoonCard
                RoutineType.EVENING -> CanvasColors.eveningCard
                RoutineType.NIGHT -> CanvasColors.nightCard
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = routine.name,
                            style = MaterialTheme.typography.h6,
                            color = CanvasColors.textPrimary
                        )
                        Text(
                            text = routine.type.name,
                            style = MaterialTheme.typography.caption,
                            color = CanvasColors.textSecondary
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        routine.startTime?.let { 
                            TimeChip(time = it)
                        }
                        routine.endTime?.let {
                            TimeChip(time = it)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tasks Section
                if (routine.tasks.isNotEmpty()) {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.subtitle1,
                        color = CanvasColors.textPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        routine.tasks.forEach { task ->
                            TaskCard(
                                task = task,
                                onEdit = { onTaskEdit(task) },
                                onDelete = { onTaskDelete(task) }
                            )
                        }
                    }
                }

                // Habits Section
                if (routine.habits.isNotEmpty()) {
                    Text(
                        text = "Habits",
                        style = MaterialTheme.typography.subtitle1,
                        color = CanvasColors.textPrimary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        routine.habits.forEach { habit ->
                            HabitCard(
                                habit = habit,
                                onEdit = { onHabitEdit(habit) },
                                onDelete = { onHabitDelete(habit) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = if (isHovered) 1.02f else 1f
                scaleY = if (isHovered) 1.02f else 1f
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isHovered = event.type == PointerEventType.Enter
                    }
                }
            }
            .animateContentSize(),
        backgroundColor = CanvasColors.taskCard,
        border = BorderStroke(
            1.dp,
            when (task.priority) {
                Priority.HIGH -> CanvasColors.priorityHigh
                Priority.MEDIUM -> CanvasColors.priorityMedium
                Priority.LOW -> CanvasColors.priorityLow
            }
        ),
        elevation = if (isHovered) 8.dp else 4.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Priority icon with color
                    Icon(
                        imageVector = when (task.priority) {
                            Priority.HIGH -> Icons.Outlined.PriorityHigh
                            Priority.MEDIUM -> Icons.Outlined.Sort
                            Priority.LOW -> Icons.Outlined.LowPriority
                        },
                        contentDescription = "Priority ${task.priority}",
                        tint = when (task.priority) {
                            Priority.HIGH -> CanvasColors.priorityHigh
                            Priority.MEDIUM -> CanvasColors.priorityMedium
                            Priority.LOW -> CanvasColors.priorityLow
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.body2,
                        color = CanvasColors.textPrimary
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.isCompleted) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = CanvasColors.completedIcon,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    // Add menu icon and dropdown
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = CanvasColors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    onEdit()
                                    showMenu = false
                                }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Edit")
                            }
                            
                            DropdownMenuItem(
                                onClick = {
                                    onDelete()
                                    showMenu = false
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Delete")
                            }
                        }
                    }
                }
            }
            
            // Progress indicator (only show if there's progress)
            if (!task.isCompleted && task.progress > 0f) {
                LinearProgressIndicator(
                    progress = task.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(4.dp),
                    backgroundColor = CanvasColors.progressBackground,
                    color = CanvasColors.progressFill
                )
            }
        }
    }
}

@Composable
private fun HabitCard(
    habit: Habit,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = if (isHovered) 1.02f else 1f
                scaleY = if (isHovered) 1.02f else 1f
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isHovered = event.type == PointerEventType.Enter
                    }
                }
            }
            .animateContentSize(),
        backgroundColor = CanvasColors.habitCard,
        border = BorderStroke(1.dp, CanvasColors.habitBorder),
        elevation = if (isHovered) 8.dp else 4.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Loop,
                        contentDescription = null,
                        tint = CanvasColors.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.body2,
                            color = CanvasColors.textPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔥 ${habit.streak}",
                                style = MaterialTheme.typography.caption,
                                color = CanvasColors.textSecondary
                            )
                            // Streak progress
                            LinearProgressIndicator(
                                progress = (habit.streak % 7) / 7f,
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(2.dp),
                                backgroundColor = CanvasColors.progressBackground,
                                color = CanvasColors.progressFill
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeChip(time: LocalTime) {
    Card(
        backgroundColor = CanvasColors.timeChipBackground,
        modifier = Modifier.height(24.dp)
    ) {
        Text(
            text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = MaterialTheme.typography.caption,
            color = CanvasColors.textSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CanvasGrid() {
    val gridSpacing = 50.dp  // Space between grid lines
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Draw vertical lines
        var x = 0f
        while (x < width) {
            drawLine(
                color = CanvasColors.gridColor,
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
                color = CanvasColors.gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridSpacing.toPx()
        }

        // Draw center lines slightly brighter
        drawLine(
            color = CanvasColors.gridCenterColor,
            start = Offset(width / 2, 0f),
            end = Offset(width / 2, height),
            strokeWidth = 1f
        )
        drawLine(
            color = CanvasColors.gridCenterColor,
            start = Offset(0f, height / 2),
            end = Offset(width, height / 2),
            strokeWidth = 1f
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