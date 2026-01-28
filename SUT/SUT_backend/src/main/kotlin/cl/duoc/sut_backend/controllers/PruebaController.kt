package cl.duoc.sut_backend.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/prueba")
class PruebaController {

    @GetMapping
    fun saludo(): ResponseEntity<String> {
        return ResponseEntity.ok("¡FUNCIONA! Has entrado al área segura 🛡️")
    }
}