package com.example.examen_api.data.remote

import com.example.examen_api.data.model.User
import com.example.examen_api.data.model.UserRequest
import com.example.examen_api.data.model.UserResponseWrapper
import com.example.examen_api.data.model.UserSingleResponseWrapper
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

interface ApiService {
    @GET("api/users")
    suspend fun getUsers(): Response<UserResponseWrapper>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<UserSingleResponseWrapper>

    @Multipart
    @POST("api/register") // Cambiado a register para coincidir con AuthController
    suspend fun registerUser(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password_confirmation") passwordConfirmation: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<UserSingleResponseWrapper>

    @Multipart
    @POST("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("_method") method: RequestBody = "PUT".toRequestBody("text/plain".toMediaTypeOrNull()),
        @Part image: MultipartBody.Part?
    ): Response<UserSingleResponseWrapper>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
}
