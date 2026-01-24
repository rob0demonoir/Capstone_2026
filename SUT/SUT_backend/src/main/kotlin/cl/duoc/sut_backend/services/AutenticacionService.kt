package cl.duoc.sut_backend.services

import cl.duoc.sut_backend.dtos.LoginRequest
import cl.duoc.sut_backend.dtos.RegistroRequest
import cl.duoc.sut_backend.models.Rol
import cl.duoc.sut_backend.models.Usuario
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AutenticacionService (
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
){
    fun registrar(request: RegistroRequest): Usuario{
        if (usuarioRepository.existsByEmail(request.email)){
            throw RuntimeException("El email ya está registrado")
        }
        if (usuarioRepository.existsByRut(request.rut)){
            throw RuntimeException("El rut ya está registrado")
        }
        val usuario = Usuario(
            rut = request.rut,
            nombre = request.nombre,
            apellido = request.apellido,
            email = request.email,
            contrasena = passwordEncoder.encode(request.contrasena),
            direccion = request.direccion,
            fechaNacimiento = request.fechaNacimiento,
            telefono = request.telefono,
            rol = Rol.VECINO,
            habilitado = true
        )

        return usuarioRepository.save(usuario)
}
    fun login(request: LoginRequest): String {
        val usuario = usuarioRepository.findByEmail(request.email)
            .orElseThrow{ RuntimeException("Usuario o Contraseña Incorrectos") }
        if(!passwordEncoder.matches(request.contrasena,usuario.contrasena)){
            throw(RuntimeException("Contraseña inválida"))
        }
        return jwtService.generarToken(usuario.email)

    }


}