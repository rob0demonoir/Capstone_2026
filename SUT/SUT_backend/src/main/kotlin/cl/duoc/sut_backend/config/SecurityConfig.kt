package cl.duoc.sut_backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.*

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val jwtAuthFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider
){
    // Filtros HTTP
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf{ it.disable() } //CSRF DOWN
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/autenticacion/**").permitAll() //se puede entrar al login y al registro sin autenticación
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/uploads/**").permitAll()//se pueden ver imagenes sin autenticacion (solo ver)
                    .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMINISTRADOR")
                    .anyRequest().authenticated() // para todo lo demás se debe autenticar
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }


    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // 1. PERMITIR CUALQUIER ORIGEN (Tailscale, Localhost, IP)
        configuration.allowedOriginPatterns = listOf("*")

        // 2. MÉTODOS
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")

        // 3. CABECERAS (Importante para que pase el Token JWT)
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("Authorization") // <--- AGREGA ESTO

        // 4. CREDENTIALS
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}

    /**@Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:5173") // La URL de tu React
        configuration.allowedOrigins = listOf("http://192.168.100.19:8085") // La URL de tu React
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }   **/