package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.models.Usuario
import cl.duoc.sut_backend.models.Rol
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import cl.duoc.sut_backend.dtos.ActualizarPerfilRequest
import org.springframework.security.core.context.SecurityContextHolder
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
        val usuarioOpt = usuarioRepository.findById(id)

        if (usuarioOpt.isPresent) {
            val usuario = usuarioOpt.get()

            return try {
                // --- CORRECCIÓN AQUÍ ---
                // 1. Limpiamos comillas dobles (") y espacios en blanco
                val rolLimpio = nuevoRol.replace("\"", "").trim().uppercase()

                // 2. Debug Log (Para que veas en la consola de IntelliJ qué está llegando)
                println("Recibido: $nuevoRol | Limpio: $rolLimpio")

                // 3. Convertimos
                usuario.rol = Rol.valueOf(rolLimpio)
                usuarioRepository.save(usuario)

                ResponseEntity.ok(mapOf("mensaje" to "Rol actualizado a ${usuario.rol}"))
            } catch (e: IllegalArgumentException) {
                println("Error al convertir rol: ${e.message}") // Log de error
                ResponseEntity.badRequest().body(mapOf("error" to "El rol '$nuevoRol' no existe"))
            }
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/perfil")
    fun actualizarMiPerfil(@RequestBody request: ActualizarPerfilRequest): ResponseEntity<Usuario> {
        val emailAuth = SecurityContextHolder.getContext().authentication!!.name
        val usuario = usuarioRepository.findByEmail(emailAuth).orElseThrow()

        // Actualizamos solo los datos permitidos
        usuario.telefono = request.telefono
        usuario.direccion = request.direccion
        usuario.email = request.email // Ojo: si cambia email, el próximo login debe ser con el nuevo

        usuarioRepository.save(usuario)
        return ResponseEntity.ok(usuario)
    }
}
