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
import cl.duoc.sut_mobile.ui.viewmodel.LoginViewModel
import cl.duoc.sut_mobile.ui.theme.SUT_MobileTheme
import cl.duoc.sut_mobile.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SUT_MobileTheme { // Si tu tema se llama distinto, ajústalo aquí
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
    // 'remember' asegura que no se re-creen cada vez que la pantalla parpadea
    val sessionManager = remember { SessionManager(context) }
    val retrofitClient = remember { RetrofitClient(context) }
    val apiService = remember { retrofitClient.instance.create(ApiService::class.java) }

    // 2. CREAMOS EL VIEWMODEL USANDO UNA FACTORY (Fábrica)
    // Esto es necesario porque nuestro ViewModel tiene parámetros en el constructor
    val viewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(sessionManager, apiService) as T
            }
        }
    )

    // 3. LÓGICA DE NAVEGACIÓN
    var isLoggedIn by remember { mutableStateOf(false) }

    // Observamos si el login fue exitoso en el ViewModel
    if (viewModel.loginSuccess) {
        isLoggedIn = true
    }

    // 4. DECIDIMOS QUÉ PANTALLA MOSTRAR
    if (isLoggedIn) {
        HomeScreen()
    } else {
        // OJO: Aquí llamamos a LoginScreen (La VISTA), no a LoginViewModel
        LoginScreen(
            viewModel = viewModel
            // No necesitamos pasar onLoginSuccess aquí porque lo estamos observando arriba (línea 62)
            // O si tu LoginScreen lo pide, puedes pasarlo, pero el ViewModel ya maneja el estado.
        )
    }
}

@Composable
fun HomeScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text("¡Bienvenido al Home! 🏠")
    }
}