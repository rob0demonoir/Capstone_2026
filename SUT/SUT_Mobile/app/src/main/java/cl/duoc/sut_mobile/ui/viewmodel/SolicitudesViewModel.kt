package cl.duoc.sut_mobile.ui.viewmodel

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.CrearSolicitudRequest
import cl.duoc.sut_mobile.model.Solicitud
import cl.duoc.sut_mobile.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SolicitudesViewModel(
    private val apiService: ApiService,
    private val context: Context // Necesitamos contexto para guardar archivos y mostrar Toasts
) : ViewModel() {

    var solicitudes by mutableStateOf<List<Solicitud>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensaje by mutableStateOf<String?>(null)

    fun cargarSolicitudes() {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = apiService.getMisSolicitudes()
                if (response.isSuccessful && response.body() != null) {
                    solicitudes = response.body()!!
                }
            } catch (e: Exception) {
                Log.e("SOLICITUDES", "Error al cargar", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun crearSolicitud(tipo: String, comentario: String) {
        viewModelScope.launch {
            try {
                val request = CrearSolicitudRequest(tipo, comentario)
                val response = apiService.crearSolicitud(request)
                if (response.isSuccessful) {
                    mensaje = "Solicitud enviada con éxito"
                    cargarSolicitudes() // Recargar la lista
                } else {
                    mensaje = "Error al enviar solicitud"
                }
            } catch (e: Exception) {
                mensaje = "Error de conexión"
            }
        }
    }

    // --- MAGIA DE DESCARGA ---
    fun descargarCertificado(solicitudId: Long) {
        viewModelScope.launch(Dispatchers.IO) { // Hacemos esto en segundo plano (IO)
            try {
                val response = apiService.descargarCertificado(solicitudId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    guardarArchivo(body.byteStream(), "certificado_$solicitudId.pdf")
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al descargar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("DESCARGA", "Error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun guardarArchivo(inputStream: InputStream, nombreArchivo: String) {
        try {
            // Guardar en la carpeta pública de Descargas (Download)
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, nombreArchivo)

            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Archivo guardado en Descargas: $nombreArchivo", Toast.LENGTH_LONG).show()
                mensaje = "Descarga completada: $nombreArchivo"
            }
        } catch (e: Exception) {
            Log.e("ARCHIVO", "No se pudo guardar", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}