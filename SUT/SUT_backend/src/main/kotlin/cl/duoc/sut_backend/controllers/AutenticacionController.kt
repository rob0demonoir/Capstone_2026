package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.dtos.RegistroRequest
import cl.duoc.sut_backend.services.AutenticacionService
import cl.duoc.sut_backend.dtos.LoginRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/autenticacion")
class AutenticacionController (
    private val autenticacionService: AutenticacionService
){

    @PostMapping("/registro")
    fun registrar(@RequestBody request: RegistroRequest): ResponseEntity<Any>{
        return try{
            val nuevoUsuario = autenticacionService.registrar(request)
            ResponseEntity.ok(mapOf("mensaje" to "Usuario creado exitosamente", "id" to nuevoUsuario.id))
        } catch (e: Exception){
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any>{
        return try {
            val token = autenticacionService.login(request)
            ResponseEntity.ok(mapOf("token" to token))
        } catch (e: Exception){
            ResponseEntity.status(401).body(mapOf("error" to e.message))
        }
    }

}