package cl.duoc.sut_movil.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.LoginRequest
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.network.RetrofitClient
import cl.duoc.sut_mobile.utils.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(private val sessionManager: SessionManager) : ViewModel() {

    // Estados de la UI
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false) // Para navegar

    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    fun onLoginClick() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Complete todos los campos"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(email.trim(), password.trim()))

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!
                    sessionManager.saveToken(token)
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