package com.example.examen_api.data.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val image: String? = null,
)

data class UserRequest(
    val name: String,
    val email: String,
    val phone: String? = null,
    val password: String? = null
)

data class UserResponseWrapper(
    val data: List<User>,
    val links: Map<String, String?>? = null,
    val meta: Map<String, Any>? = null
)
data class UserSingleResponseWrapper(val data: User)
