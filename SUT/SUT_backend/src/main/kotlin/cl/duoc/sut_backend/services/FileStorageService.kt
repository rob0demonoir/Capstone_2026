package cl.duoc.sut_backend.services

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class FileStorageService {
    private val rootLocation = Paths.get("/app/uploads") // O "/app/uploads" si prefieres ser explícito

    fun guardarArchivo(archivo: MultipartFile): String {
        if (archivo.isEmpty) return ""

        try {
            if (!Files.exists(rootLocation)) Files.createDirectories(rootLocation)

            val nombreArchivo = "${System.currentTimeMillis()}_${archivo.originalFilename}"
            val destino = rootLocation.resolve(nombreArchivo)

            Files.copy(archivo.inputStream, destino, StandardCopyOption.REPLACE_EXISTING)

            // Retornamos la ruta con el prefijo correcto
            return "/api/uploads/$nombreArchivo"
        } catch (e: Exception) {
            throw RuntimeException("Error al guardar archivo", e)
        }
    }
}