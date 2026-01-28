package cl.duoc.sut_backend.config

import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class ApplicationConfig(
    private val usuarioRepository: UsuarioRepository
) {
    @Bean
    fun servicioDetallesUsuario(): UserDetailsService {
        return UserDetailsService { username ->
            usuarioRepository.findByEmail(username)
                .orElseThrow{ UsernameNotFoundException ("Usuario no encontrado.") }
            }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    @Bean
    fun proveedorAutenticacion(passwordEncoder: PasswordEncoder, userDetailsService: UserDetailsService): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder)
        return authProvider
    }

    @Bean
    fun administradorAutenticacion(config: AuthenticationConfiguration): AuthenticationManager{
        return config.authenticationManager
    }

}
