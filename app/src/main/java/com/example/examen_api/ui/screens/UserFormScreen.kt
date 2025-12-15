package com.example.examen_api.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examen_api.ui.viewmodel.UserViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun UserFormScreen(
    userId: Int? = null,
    viewModel: UserViewModel,
    onBack: () -> Unit,
    onUserCreated: (Int) -> Unit = {}, // New callback with default
    sharedTransitionScope: androidx.compose.animation.SharedTransitionScope,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope
) {
    // ... (state code remains same) ...
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Validation states
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    
    // Load data if editing
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.getUser(userId)
        }
    }
    
    LaunchedEffect(viewModel.currentUser) {
        if (userId != null && viewModel.currentUser != null && viewModel.currentUser!!.id == userId) {
            name = viewModel.currentUser!!.name
            email = viewModel.currentUser!!.email
            phone = viewModel.currentUser!!.phone ?: ""
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    val saveAction = {
        // Reset errors
        nameError = null
        emailError = null
        phoneError = null

        var isValid = true
        
        if (name.length < 3) {
            nameError = "El nombre es requerido" // Or specific length msg
            isValid = false
        }
        
        if (phone.length != 10) {
            phoneError = "El teléfono es requerido" // Or 10 digits msg
            isValid = false
        }
        
        if (email.isEmpty()) {
            emailError = "El email es requerido"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Email inválido"
            isValid = false
        }

        if (isValid) {
             if (userId == null) {
                val file = imageUri?.let { uri ->
                    createTmpFileFromUri(context, uri, "profile_image")
                }
                
                if (imageUri != null && file == null) {
                    Toast.makeText(context, "Error procesando la imagen", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.createUser(name, email, phone, file) { newUserId ->
                        Toast.makeText(context, "Contacto guardado", Toast.LENGTH_SHORT).show()
                        onUserCreated(newUserId)
                    }
                }
            } else {
                val file = imageUri?.let { uri ->
                    createTmpFileFromUri(context, uri, "profile_image_update")
                }
                
                if (imageUri != null && file == null) {
                    Toast.makeText(context, "Error procesando la imagen", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updateUser(userId, name, email, phone, file) { 
                        Toast.makeText(context, "Contacto guardado", Toast.LENGTH_SHORT).show()
                        onBack() 
                    }
                }
            }
        }
    }

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            if (userId == null) "New Contact" else "Edit Contact",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        TextButton(onClick = onBack) {
                            Text("Cancel", fontSize = 17.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    actions = {
                        TextButton(onClick = saveAction) {
                            Text("Save", fontSize = 17.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = if (userId == null) {
                Modifier.sharedBounds(
                    rememberSharedContentState(key = "fab-create"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            } else {
                Modifier
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
    
                // 2. Fotografía del contacto
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .clickable { launcher.launch("image/*") }
                        .let { modifier ->
                            if (userId != null) {
                                modifier.sharedElement(
                                    rememberSharedContentState(key = "image-$userId"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            } else {
                                modifier
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                     if (imageUri != null) {
                        val bitmap = remember(imageUri) {
                            try {
                                context.contentResolver.openInputStream(imageUri!!)?.use {
                                    BitmapFactory.decodeStream(it)
                                }?.asImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Foto",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                     } else if (userId != null && viewModel.currentUser?.image != null) {
                          coil.compose.AsyncImage(
                            model = viewModel.currentUser!!.image,
                            contentDescription = "Foto actual",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                     } else {
                         Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera), 
                            contentDescription = "Add Photo", 
                            modifier = Modifier.size(50.dp),
                            tint = Color(0xFF2962FF) // Azul para el icono también
                        )
                     }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Add Photo", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
    
                Spacer(modifier = Modifier.height(32.dp))
    
                // 3. Campos del formulario
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // a) Full Name
                    ValidatedTextField(
                        value = name,
                        onValueChange = { newName ->
                            if (newName.length <= 50 && newName.none { it.isDigit() }) {
                                name = newName
                                if (nameError != null) nameError = null
                            }
                        },
                        label = "Full Name",
                        placeholder = "John Doe",
                        leadingIcon = Icons.Default.Person,
                        errorMessage = nameError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
    
                    // b) Phone Number
                    ValidatedTextField(
                        value = phone,
                        onValueChange = { 
                            if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                phone = it
                                if (phoneError != null) phoneError = null
                            }
                        },
                        label = "Phone Number",
                        placeholder = "(555) 123-4567",
                        leadingIcon = Icons.Default.Phone,
                        errorMessage = phoneError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                    )
    
                    // c) Email Address
                    ValidatedTextField(
                        value = email,
                        onValueChange = { 
                            if (it.length <= 60) {
                                email = it 
                                if (emailError != null) emailError = null
                            }
                        },
                        label = "Email Address",
                        placeholder = "john.doe@example.com",
                        leadingIcon = Icons.Default.Email,
                        errorMessage = emailError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
                    )
                }
    
                Spacer(modifier = Modifier.height(40.dp))
    
                // 4. Botón Guardar (parte inferior)
                Button(
                    onClick = saveAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2962FF) // Azul principal solicitado
                    )
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "Save Contact",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

fun createTmpFileFromUri(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val mimeType = context.contentResolver.getType(uri)
        val extension = when {
            mimeType?.contains("png") == true -> ".png"
            mimeType?.contains("gif") == true -> ".gif"
            else -> ".jpg"
        }
        
        val stream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile(fileName, extension, context.cacheDir)
        stream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val isError = errorMessage != null
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(contentAlignment = Alignment.CenterEnd) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = if (isError) Color.Red else LocalContentColor.current) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = keyboardOptions,
                singleLine = true,
                isError = isError,
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorLeadingIconColor = Color.Red,
                    errorCursorColor = Color.Red,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            if (isError) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 16.dp, bottom = 12.dp) // Adjust padding to float inside bottom right
                )
            }
        }
    }
}
