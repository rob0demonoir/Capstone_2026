package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.models.Usuario
import cl.duoc.sut_backend.models.Rol
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


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

    @GetMapping
    fun listarUsuarios(): ResponseEntity<List<Usuario>> {
        // En un caso real, aquí paginaríamos, pero para la tesis devuelve todos.
        // Opcional: Podrías ordenar por apellido
        val usuarios = usuarioRepository.findAll().sortedBy { it.apellido }
        return ResponseEntity.ok(usuarios)
    }

    @PutMapping("/{id}/rol")
    fun cambiarRol(
        @PathVariable id: Long,
        @RequestBody nuevoRol: String
    ): ResponseEntity<Any> {
        // 1. Buscamos el usuario de forma tradicional
        val usuarioOpt = usuarioRepository.findById(id)

        if (usuarioOpt.isPresent) {
            val usuario = usuarioOpt.get()

            return try {
                // 2. Intentamos convertir el texto "ADMIN" al Enum Rol.ADMIN
                usuario.rol = Rol.valueOf(nuevoRol.uppercase())
                usuarioRepository.save(usuario)

                // Retornamos OK
                ResponseEntity.ok(mapOf("mensaje" to "Rol actualizado a ${usuario.rol}"))
            } catch (e: IllegalArgumentException) {
                // Si mandaron un rol que no existe (ej: "SUPERMAN")
                ResponseEntity.badRequest().body(mapOf("error" to "El rol '$nuevoRol' no existe"))
            }
        } else {
            // 3. Si no encontramos el usuario
            return ResponseEntity.notFound().build()
        }
    }
}
