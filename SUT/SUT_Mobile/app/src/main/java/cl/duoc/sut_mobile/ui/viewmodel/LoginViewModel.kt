package cl.duoc.sut_mobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.LoginRequest
import cl.duoc.sut_mobile.model.LoginResponse
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.utils.SessionManager
import kotlinx.coroutines.launch

// 1. AHORA RECIBE EL APISERVICE EN EL CONSTRUCTOR (Inyección de dependencias simple)
class LoginViewModel(
    private val sessionManager: SessionManager,
    private val apiService: ApiService
) : ViewModel() {

    // Estados de la UI
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    // YA NO CREAMOS RETROFIT AQUÍ DENTRO (Eliminamos el error de 'instance')
    // private val apiService = RetrofitClient.instance.create... (BORRADO)

    fun onLoginClick() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Complete todos los campos"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                // Usamos el apiService que recibimos en el constructor
                val response = apiService.login(LoginRequest(email.trim(), password.trim()))

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse  = response.body()!!
                    sessionManager.saveToken(loginResponse.token)
                    loginSuccess = true
                } else {
                    errorMessage = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}