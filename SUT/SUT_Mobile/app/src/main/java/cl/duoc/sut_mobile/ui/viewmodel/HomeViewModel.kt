package cl.duoc.sut_mobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.network.ApiService
import kotlinx.coroutines.launch

class HomeViewModel(private val apiService: ApiService) : ViewModel() {

    var usuario by mutableStateOf<Usuario?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // El bloque init se ejecuta apenas se crea el ViewModel
    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = apiService.getPerfil()
                if (response.isSuccessful && response.body() != null) {
                    usuario = response.body()
                } else {
                    errorMessage = "Error al cargar perfil: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Función para recargar si falla (puedes poner un botón de reintentar)
    fun reintentar() {
        cargarPerfil()
    }
}