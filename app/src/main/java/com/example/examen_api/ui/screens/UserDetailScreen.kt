package com.example.examen_api.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examen_api.ui.components.UserAvatar
import com.example.examen_api.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun UserDetailScreen(
    userId: Int,
    viewModel: UserViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    sharedTransitionScope: androidx.compose.animation.SharedTransitionScope,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope
) {
    LaunchedEffect(userId) {
        viewModel.getUser(userId)
    }

    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Contacto") },
            text = { Text("¿Estás seguro de que deseas eliminar este contacto?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteUser(userId) {
                            showDeleteDialog = false
                            Toast.makeText(context, "Contacto eliminado", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Contactos", color = MaterialTheme.colorScheme.primary, fontSize = 17.sp)
                        }
                    }
                },
                actions = {
                    TextButton(onClick = { onEdit(userId) }) {
                        Text("Editar", color = MaterialTheme.colorScheme.primary, fontSize = 17.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (viewModel.isLoading && viewModel.currentUser == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                viewModel.currentUser?.let { user ->
                    with(sharedTransitionScope) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // 1. Header with Avatar and Name
                            Box(
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "image-${user.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            ) {
                                UserAvatar(name = user.name, image = user.image, size = 100.dp, fontSize = 40)
                            }
    
                            Spacer(modifier = Modifier.height(16.dp))
    
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "name-${user.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            )
    
                            // 2. Action Buttons Row (iOS Style)
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ActionButton(icon = Icons.Default.Send, label = "Mensaje") {
                                    Toast.makeText(context, "Mensaje", Toast.LENGTH_SHORT).show()
                                }
                                ActionButton(icon = Icons.Default.Phone, label = "Llamar") {
                                    Toast.makeText(context, "Llamar", Toast.LENGTH_SHORT).show()
                                }
                                ActionButton(icon = Icons.Default.PlayArrow, label = "Video") {
                                    Toast.makeText(context, "Video", Toast.LENGTH_SHORT).show()
                                }
                                ActionButton(icon = Icons.Default.Email, label = "Correo") {
                                    Toast.makeText(context, "Correo", Toast.LENGTH_SHORT).show()
                                }
                            }
    
                            // 3. Information Cards
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                InfoGroupCard {
                                    ContactInfoRow(
                                        label = "móvil",
                                        value = user.phone ?: "No disponible",
                                        isLast = false,
                                        actionIcon = Icons.Default.Send
                                    )
                                    ContactInfoRow(
                                        label = "correo electrónico",
                                        value = user.email,
                                        isLast = true,
                                        actionIcon = Icons.Default.Email
                                    )
                                }
                            }
    
                            // 4. Delete Action
                            Spacer(modifier = Modifier.height(40.dp))
                            
                            Surface(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Box(contentAlignment = Alignment.CenterStart) {
                                    Text(
                                        "Eliminar Contacto",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 17.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer, 
            modifier = Modifier.size(56.dp)
        ) {
             Box(contentAlignment = Alignment.Center) {
                 Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
             }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun InfoGroupCard(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), // Light gray background
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun ContactInfoRow(label: String, value: String, isLast: Boolean, actionIcon: ImageVector? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    if (!isLast) {
        Divider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 0.5.dp
        )
    }
}
