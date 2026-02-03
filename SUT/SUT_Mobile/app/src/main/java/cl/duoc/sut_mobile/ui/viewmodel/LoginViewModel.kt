/**package cl.duoc.sut_mobile.ui.viewmodel

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
}**/
package cl.duoc.sut_mobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.LoginRequest
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.utils.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Estado de la UI
    // null = no ha intentado, true = éxito, false = error
    var loginResult by mutableStateOf<Boolean?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(email: String, contrasena: String) {
        // Validaciones básicas antes de enviar
        if (email.isBlank() || contrasena.isBlank()) {
            errorMessage = "Por favor ingresa correo y contraseña"
            loginResult = false
            return
        }

        isLoading = true
        errorMessage = null // Limpiar error previo

        viewModelScope.launch {
            try {
                val request = LoginRequest(email, contrasena)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    // IMPORTANTE: Guardamos el token
                    // El SessionManager ya se encarga de persistirlo
                    sessionManager.saveToken(loginResponse.token)

                    loginResult = true
                } else {
                    // Error del servidor (ej: 401 Credenciales inválidas)
                    errorMessage = "Credenciales incorrectas"
                    loginResult = false
                }
            } catch (e: Exception) {
                // Error de conexión (Internet, servidor caído, timeout)
                e.printStackTrace()
                errorMessage = "Error de conexión: ${e.message}"
                loginResult = false
            } finally {
                isLoading = false
            }
        }
    }

    // Función para "limpiar" el estado si queremos intentar de nuevo sin recargar la pantalla
    fun resetLoginState() {
        loginResult = null
        errorMessage = null
    }
}