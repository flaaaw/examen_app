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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examen_api.ui.components.UserAvatar
import com.example.examen_api.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: Int,
    viewModel: UserViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // 2. Fotografía del contacto
                        UserAvatar(name = user.name, image = user.image, size = 120.dp, fontSize = 48)

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. Información principal (Nombre)
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4. Acciones rápidas
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ActionButton(icon = Icons.Default.Email, label = "Mensaje") {
                                Toast.makeText(context, "Mensaje", Toast.LENGTH_SHORT).show()
                            }
                            ActionButton(icon = Icons.Default.Phone, label = "Llamar") {
                                Toast.makeText(context, "Llamar", Toast.LENGTH_SHORT).show()
                            }
                            ActionButton(icon = Icons.Default.Star, label = "Video") {
                                Toast.makeText(context, "Video", Toast.LENGTH_SHORT).show()
                            }
                            ActionButton(icon = Icons.Default.Email, label = "Correo") {
                                Toast.makeText(context, "Correo", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // 5. Información de contacto
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ContactInfoCard(
                                label = "móvil",
                                value = user.phone ?: "No disponible"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            ContactInfoCard(
                                label = "correo electrónico",
                                value = user.email
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // 6. Acción de eliminar
                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(
                                "Eliminar Contacto",
                                color = Color.Red,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
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
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(56.dp)
        ) {
             Box(contentAlignment = Alignment.Center) {
                 Icon(icon, contentDescription = label, tint = Color(0xFF2962FF)) // Blue icon
             }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF2962FF))
    }
}

@Composable
fun ContactInfoCard(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)), // Lighter gray
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF2962FF) // Blue value
            )
        }
    }
}
