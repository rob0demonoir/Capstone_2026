package cl.duoc.sut_mobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.EstadoSolicitud
import cl.duoc.sut_mobile.model.Noticia
import cl.duoc.sut_mobile.model.ResponderSolicitudRequest
import cl.duoc.sut_mobile.model.Solicitud
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.network.ApiService
import kotlinx.coroutines.launch

class HomeViewModel(private val apiService: ApiService) : ViewModel() {

    var usuario by mutableStateOf<Usuario?>(null)
    var noticias by mutableStateOf<List<Noticia>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var solicitudesAdmin by mutableStateOf<List<Solicitud>>(emptyList())

    // El bloque init se ejecuta apenas se crea el ViewModel
    init {
        cargarDatos()
    }

    fun cargarDatos() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                // 1. Cargar Usuario
                val userResponse = apiService.getPerfil()
                if (userResponse.isSuccessful) {
                    usuario = userResponse.body()

                    // SI ES ADMIN, CARGAMOS LAS SOLICITUDES
                    if (usuario?.rol?.uppercase()?.contains("ADMIN") == true) {
                        cargarSolicitudesAdmin()
                    }
                }

                // 2. Cargar Noticias (igual que antes)
                val noticiasResponse = apiService.getNoticias()
                if (noticiasResponse.isSuccessful) {
                    noticias = noticiasResponse.body() ?: emptyList()
                }

            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun cargarSolicitudesAdmin() {
        val response = apiService.getTodasSolicitudes()
        if (response.isSuccessful) {
            solicitudesAdmin = response.body() ?: emptyList()
        }
    }

    // Función para Aprobar/Rechazar
    fun responderSolicitud(id: Long, aprobar: Boolean) {
        viewModelScope.launch {
            val nuevoEstado = if (aprobar) EstadoSolicitud.APROBADA else EstadoSolicitud.RECHAZADA
            val comentario = if (aprobar) "Aprobado desde App Móvil" else "Rechazado por Admin"

            val request = ResponderSolicitudRequest(nuevoEstado, comentario)

            try {
                val response = apiService.responderSolicitud(id, request)
                if (response.isSuccessful) {
                    // Recargar la lista para ver el cambio
                    cargarSolicitudesAdmin()
                }
            } catch (e: Exception) {
                errorMessage = "Error al responder solicitud"
            }
        }
    }

    fun limpiarDatos() {
        usuario = null
        noticias = emptyList()
        solicitudesAdmin = emptyList()
        errorMessage = null
        isLoading = false
    }

    // Función para recargar si falla (puedes poner un botón de reintentar)
    fun reintentar() {
        cargarDatos()
    }
}