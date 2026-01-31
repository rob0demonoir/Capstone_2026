package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.models.Usuario
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/usuarios")
class UsuarioController(
    private val usuarioRepository: UsuarioRepository
) {

    @GetMapping("/perfil")
    fun obtenerPerfil(authentication: Authentication): ResponseEntity<Usuario> {
        // Spring inyecta automáticamente el objeto 'authentication'
        // que contiene los datos del Token JWT desencriptado.
        val email = authentication.name

        // Buscamos al usuario en la DB
        // Usamos orElse(null) para manejar si no existe de forma segura en Kotlin
        val usuario = usuarioRepository.findByEmail(email).orElse(null)
            ?: return ResponseEntity.notFound().build()

        // 🔒 SEGURIDAD CRÍTICA:
        // Jamás devolvemos la contraseña hash al frontend, ni siquiera la encriptada.
        // La limpiamos en este objeto antes de responder (no afecta la DB).
        usuario.contrasena = ""

        return ResponseEntity.ok(usuario)
    }
}