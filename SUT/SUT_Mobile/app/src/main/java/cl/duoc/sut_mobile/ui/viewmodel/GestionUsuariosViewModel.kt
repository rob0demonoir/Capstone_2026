package cl.duoc.sut_mobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.network.ApiService
import kotlinx.coroutines.launch

class GestionUsuariosViewModel(private val apiService: ApiService) : ViewModel() {

    // Lista completa original
    private var todosLosUsuarios = listOf<Usuario>()

    // Lista que se ve en pantalla (filtrada)
    var usuariosVisibles by mutableStateOf<List<Usuario>>(emptyList())

    var isLoading by mutableStateOf(false)
    var searchQuery by mutableStateOf("")

    fun cargarUsuarios() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getUsuarios()
                if (response.isSuccessful) {
                    todosLosUsuarios = response.body() ?: emptyList()
                    filtrar(searchQuery)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun filtrar(query: String) {
        searchQuery = query
        if (query.isBlank()) {
            usuariosVisibles = todosLosUsuarios
        } else {
            usuariosVisibles = todosLosUsuarios.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.apellido.contains(query, ignoreCase = true) ||
                        it.rut.contains(query, ignoreCase = true)
            }
        }
    }

    fun alternarRol(usuario: Usuario) {
        viewModelScope.launch {
            val nuevoRol = if (usuario.rol == "ADMINISTRADOR") "VECINO" else "ADMINISTRADOR"
            try {
                isLoading = true
                val response = apiService.actualizarRol(usuario.id, nuevoRol)
                if (response.isSuccessful) {
                    cargarUsuarios()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // --- NUEVA FUNCIÓN: ELIMINAR ---
    fun eliminarUsuario(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.eliminarUsuario(id)
                if (response.isSuccessful) {
                    // Borrado exitoso: Lo quitamos de la lista local
                    // Esto evita tener que llamar a cargarUsuarios() de nuevo
                    todosLosUsuarios = todosLosUsuarios.filter { it.id != id }

                    // Volvemos a aplicar el filtro para refrescar la pantalla
                    filtrar(searchQuery)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}