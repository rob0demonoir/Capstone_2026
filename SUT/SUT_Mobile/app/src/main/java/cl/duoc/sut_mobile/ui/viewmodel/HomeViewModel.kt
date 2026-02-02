package cl.duoc.sut_mobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.Noticia
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.network.ApiService
import kotlinx.coroutines.launch

class HomeViewModel(private val apiService: ApiService) : ViewModel() {

    var usuario by mutableStateOf<Usuario?>(null)
    var noticias by mutableStateOf<List<Noticia>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // El bloque init se ejecuta apenas se crea el ViewModel
    init {
        cargarDatos()
    }

    fun cargarDatos() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val responsePerfil = apiService.getPerfil()
                if (responsePerfil.isSuccessful && responsePerfil.body() != null) {
                    usuario = responsePerfil.body()
                } else {
                    errorMessage = "Error al cargar perfil: ${responsePerfil.code()}"
                }
                val responseNoticias = apiService.getNoticias()
                if (responseNoticias.isSuccessful && responseNoticias.body() != null) {
                    noticias = responseNoticias.body()!!
                }


            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Función para recargar si falla (puedes poner un botón de reintentar)
    fun reintentar() {
        cargarDatos()
    }
}