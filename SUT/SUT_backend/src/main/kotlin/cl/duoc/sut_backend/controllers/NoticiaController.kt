package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.dtos.CrearNoticiaRequest
import cl.duoc.sut_backend.dtos.NoticiaResponse
import cl.duoc.sut_backend.models.Noticia
import cl.duoc.sut_backend.repositories.NoticiaRepository
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.http.MediaType
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/api/noticias")
class NoticiaController(
    private val noticiaRepository: NoticiaRepository,
    private val usuarioRepository: UsuarioRepository
) {

    @GetMapping
    fun obtenerNoticias(): ResponseEntity<List<NoticiaResponse>> {
        val noticias = noticiaRepository.findAllByOrderByFechaPublicacionDesc()

        val response = noticias.map { n ->
            NoticiaResponse(
                id = n.id,
                titulo = n.titulo,
                contenido = n.contenido,
                fecha = n.fechaPublicacion,
                autor = "${n.autor.nombre} ${n.autor.apellido}",
                urlImagen = n.urlImagen
            )
        }
        return ResponseEntity.ok(response)
    }

    /**@PostMapping
    fun crearNoticia(@RequestBody request: CrearNoticiaRequest): ResponseEntity<String> {
        val email = SecurityContextHolder.getContext().authentication!!.name
        val autor = usuarioRepository.findByEmail(email)
            .orElseThrow{ RuntimeException("Usuario no encontrado.") }

        val nuevaNoticia = Noticia(
            id = 0, // Mejor usa 'null' si tu Entity usa @GeneratedValue. Si es primitivo (long), usa 0.
            titulo = request.titulo,
            contenido = request.contenido,
            fechaPublicacion = LocalDateTime.now(), // <--- ¡FALTABA ESTO!
            urlImagen = request.urlImagen,
            autor = autor // OJO: Revisa si tu modelo Noticia pide un String o un Objeto Usuario.
            // Si en tu modelo 'autor' es tipo String, usa la línea de arriba.
            // Si 'autor' es tipo Usuario, usa: autor = autor
        )

        noticiaRepository.save(nuevaNoticia)
        return ResponseEntity.ok("Noticia publicada correctamente")
    }**/
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]) // 1. Aceptamos Multipart
    fun crearNoticia(
        @RequestParam("titulo") titulo: String,
        @RequestParam("contenido") contenido: String,
        @RequestParam("imagen", required = false) imagen: MultipartFile? // 2. Recibimos el archivo
    ): ResponseEntity<String> {

        // A. Obtener usuario (Autor)
        val email = SecurityContextHolder.getContext().authentication!!.name
        val autor = usuarioRepository.findByEmail(email)
            .orElseThrow{ RuntimeException("Usuario no encontrado.") }

        // B. Lógica de guardado de imagen (Igual que en Avisos)
        var rutaImagenFinal: String? = null

        if (imagen != null && !imagen.isEmpty) {
            try {
                // Usamos la carpeta "uploads" en la raíz (que mapea a /app/uploads en Docker)
                val directorioUploads = Paths.get("uploads")
                if (!Files.exists(directorioUploads)) {
                    Files.createDirectories(directorioUploads)
                }

                // Nombre único para evitar sobrescribir
                val nombreArchivo = "${System.currentTimeMillis()}_${imagen.originalFilename}"
                val rutaCompleta = directorioUploads.resolve(nombreArchivo)

                // Guardar físicamente
                Files.copy(imagen.inputStream, rutaCompleta, StandardCopyOption.REPLACE_EXISTING)

                // Guardar la URL pública con el prefijo /api/uploads/
                rutaImagenFinal = "/api/uploads/$nombreArchivo"

            } catch (e: Exception) {
                e.printStackTrace()
                return ResponseEntity.internalServerError().body("Error al subir la imagen de la noticia")
            }
        }

        // C. Crear el objeto Noticia
        val nuevaNoticia = Noticia(
            id = 0, // Generalmente no es necesario poner el ID si es autoincremental, Spring lo ignora o lo maneja
            titulo = titulo,
            contenido = contenido,
            fechaPublicacion = LocalDateTime.now(),
            urlImagen = rutaImagenFinal, // Aquí va la ruta generada o null
            autor = autor
        )

        // D. Guardar en Base de Datos
        noticiaRepository.save(nuevaNoticia)

        return ResponseEntity.ok("Noticia publicada correctamente")
    }
}