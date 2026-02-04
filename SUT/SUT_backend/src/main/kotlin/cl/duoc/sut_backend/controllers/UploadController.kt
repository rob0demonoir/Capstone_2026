package cl.duoc.sut_backend.controllers

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@RestController
@RequestMapping("/api/uploads")
class UploadController {

    // Carpeta donde se guardarán las fotos (dentro del contenedor o servidor)
    private val uploadDir = Paths.get("/app/uploads")

    init {
        // Crea la carpeta si no existe al iniciar
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
    }

    // 1. SUBIR IMAGEN
    @PostMapping
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        try {
            // Generar nombre único para evitar colisiones (ej: foto_perrito.jpg -> uuid_foto_perrito.jpg)
            val fileName = "${UUID.randomUUID()}_${file.originalFilename}"
            val targetLocation = uploadDir.resolve(fileName)

            // Guardar el archivo
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)

            // Devolver la URL relativa (ej: /api/uploads/nombre_archivo.jpg)
            val fileUrl = "/api/uploads/$fileName"

            return ResponseEntity.ok(mapOf("url" to fileUrl))
        } catch (e: Exception) {
            return ResponseEntity.internalServerError().body(mapOf("error" to "No se pudo subir la imagen"))
        }
    }

    // 2. VER IMAGEN (Servir el archivo)
    @GetMapping("/{filename:.+}")
    fun serveFile(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = uploadDir.resolve(filename)
        val resource = UrlResource(file.toUri())

        return if (resource.exists() || resource.isReadable) {
            // Detectar tipo de archivo (imagen/jpeg, imagen/png, etc.)
            val contentType = Files.probeContentType(file) ?: "application/octet-stream"

            ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${resource.filename}\"")
                .body(resource)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}