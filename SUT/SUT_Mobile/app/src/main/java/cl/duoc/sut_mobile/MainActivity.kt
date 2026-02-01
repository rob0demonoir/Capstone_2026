package cl.duoc.sut_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.network.RetrofitClient
import cl.duoc.sut_mobile.ui.screen.LoginScreen
import cl.duoc.sut_mobile.ui.screen.HomeScreen // Importamos la pantalla nueva
import cl.duoc.sut_mobile.ui.viewmodel.LoginViewModel
import cl.duoc.sut_mobile.ui.viewmodel.HomeViewModel
import cl.duoc.sut_mobile.ui.theme.SUT_MobileTheme
import cl.duoc.sut_mobile.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SUT_MobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current

    // 1. INICIALIZAMOS LAS DEPENDENCIAS
    val sessionManager = remember { SessionManager(context) }
    val retrofitClient = remember { RetrofitClient(context) }
    val apiService = remember { retrofitClient.instance.create(ApiService::class.java) }

    // 2. CREAMOS EL VIEWMODEL DEL LOGIN
    // CORRECCIÓN 1: Le puse nombre explícito 'loginViewModel'
    val loginViewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(sessionManager, apiService) as T
            }
        }
    )

    // 3. CREAMOS EL VIEWMODEL DEL HOME
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(apiService) as T
            }
        }
    )

    // 4. LÓGICA DE NAVEGACIÓN
    var isLoggedIn by remember { mutableStateOf(false) }

    // CORRECCIÓN 2: Ahora usamos 'loginViewModel' aquí también
    if (loginViewModel.loginSuccess) {
        isLoggedIn = true
    }

    // 5. DECIDIMOS QUÉ PANTALLA MOSTRAR
    if (isLoggedIn) {
        // CORRECCIÓN 3: Esto ahora llamará a la pantalla del archivo externo
        HomeScreen(viewModel = homeViewModel)
    } else {
        // CORRECCIÓN 4: Ahora coincide el nombre de la variable
        LoginScreen(viewModel = loginViewModel)
    }
}