package cl.duoc.sut_backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    //ENCRIPTACION

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // Filtros HTTP
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf{ it.disable() } //CSRF DOWN
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/autenticacion/**").permitAll() //se puede entrar al login y al registro sin autenticación
                    .anyRequest().authenticated() // para todo lo demás se debe autenticar
            }
        return http.build()
    }
}