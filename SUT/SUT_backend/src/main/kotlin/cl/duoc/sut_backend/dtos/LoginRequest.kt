package cl.duoc.sut_backend.dtos

data class LoginRequest (
    val email: String,
    val contrasena: String
)