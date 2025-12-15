@file:Suppress("DEPRECATION")
package com.example.examen_api.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examen_api.data.model.User
import com.example.examen_api.data.model.UserRequest
import com.example.examen_api.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserViewModel : ViewModel() {
    var users by mutableStateOf<List<User>>(emptyList())
        private set
    
    var currentUser by mutableStateOf<User?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun fetchUsers() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getUsers()
                if (response.isSuccessful) {
                    users = response.body()?.data ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getUser(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getUser(id)
                if (response.isSuccessful) {
                    currentUser = response.body()?.data
                } else {
                    errorMessage = "Error al obtener usuario"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    @Suppress("DEPRECATION")
    fun createUser(name: String, email: String, phone: String, imageFile: File?, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                // ... parts setup ...
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = if (imageFile != null) {
                    val mimeType = when {
                        imageFile.name.endsWith(".png", true) -> "image/png"
                        imageFile.name.endsWith(".gif", true) -> "image/gif"
                        else -> "image/jpeg"
                    }
                    val requestFile = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
                } else {
                    null
                }

                val response = RetrofitClient.apiService.createUser(
                    name = namePart,
                    email = emailPart,
                    phone = phonePart,
                    image = imagePart
                )
                if (response.isSuccessful) {
                    fetchUsers()
                    val newUser = response.body()?.data
                    if (newUser != null) {
                        onSuccess(newUser.id)
                    } else {
                        errorMessage = "Error: Respuesta vacía"
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val message = try {
                        val json = org.json.JSONObject(errorBody)
                        val msg = json.optString("message", "Error desconocido")
                        val errors = json.optJSONObject("errors")
                        if (errors != null) {
                            "$msg: ${errors.toString()}"
                        } else {
                            msg
                        }
                    } catch (e: Exception) {
                        "Error al crear: ${response.code()} $errorBody"
                    }
                    errorMessage = message
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateUser(id: Int, name: String, email: String, phone: String, imageFile: File?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())
                val methodPart = "PUT".toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = if (imageFile != null) {
                   val mimeType = when {
                        imageFile.name.endsWith(".png", true) -> "image/png"
                        imageFile.name.endsWith(".gif", true) -> "image/gif"
                        else -> "image/jpeg"
                    }
                   val requestFile = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                   MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
                } else {
                   null
                }

                val response = RetrofitClient.apiService.updateUser(
                    id = id,
                    name = namePart,
                    email = emailPart,
                    phone = phonePart,
                    method = methodPart,
                    image = imagePart
                )

                if (response.isSuccessful) {
                    fetchUsers()
                    getUser(id) // Refresh current user details
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val message = try {
                        org.json.JSONObject(errorBody).getString("message")
                    } catch (e: Exception) {
                        "Error al actualizar: ${response.code()}"
                    }
                    errorMessage = message
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteUser(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.deleteUser(id)
                if (response.isSuccessful) {
                    fetchUsers()
                    onSuccess()
                } else {
                    errorMessage = "Error al eliminar"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun clearError() {
        errorMessage = null
    }
}
