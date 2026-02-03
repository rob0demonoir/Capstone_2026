/**package cl.duoc.sut_mobile

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

    /**
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(apiService) as T
            }
        }
    )**/

    // 4. LÓGICA DE NAVEGACIÓN
    var isLoggedIn by remember { mutableStateOf(false) }

    // CORRECCIÓN 2: Ahora usamos 'loginViewModel' aquí también
    if (loginViewModel.loginSuccess) {
        isLoggedIn = true
    }

    // 5. DECIDIMOS QUÉ PANTALLA MOSTRAR
    if (isLoggedIn) {
        // ✨✨✨ MAGIA AQUÍ ✨✨✨
        // Creamos el HomeViewModel SOLO cuando ya estamos logueados.
        // Esto forzará que el bloque 'init' se ejecute AHORA MISMO y pida el perfil con el token nuevo.
        val homeViewModel: HomeViewModel = viewModel(
            factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(apiService) as T
                }
            }
        )

        HomeScreen(viewModel = homeViewModel)
    } else {
        LoginScreen(viewModel = loginViewModel)
    }
}**/
package cl.duoc.sut_mobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.network.RetrofitClient
import cl.duoc.sut_mobile.ui.screen.HomeScreen
import cl.duoc.sut_mobile.ui.screen.LoginScreen
import cl.duoc.sut_mobile.ui.screen.SolicitudesScreen
import cl.duoc.sut_mobile.ui.theme.SUT_MobileTheme // Asegúrate que este nombre coincida con tu tema
import cl.duoc.sut_mobile.ui.viewmodel.HomeViewModel
import cl.duoc.sut_mobile.ui.viewmodel.SolicitudesViewModel
import cl.duoc.sut_mobile.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos Retrofit y ApiService aquí
        val retrofitClient = RetrofitClient(this)
        val apiService = retrofitClient.instance.create(ApiService::class.java)

        setContent {
            // Usa el nombre de tu tema (puede ser SUTMobileTheme o similar)
            SUT_MobileTheme {
                AppNavigation(apiService = apiService, context = this)
            }
        }
    }
}

@Composable
fun AppNavigation(apiService: ApiService, context: Context) {
    val sessionManager = remember { SessionManager(context) }

    // Estado para saber si estamos logueados
    var isLoggedIn by remember { mutableStateOf(!sessionManager.getToken().isNullOrBlank()) }

    // --- ESTADO DE NAVEGACIÓN NUEVO ---
    // Puede ser: "home", "solicitudes"
    var currentScreen by remember { mutableStateOf("home") }


    // --- VIEWMODELS ---
    // 1. ViewModel del Home
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(apiService) as T
            }
        }
    )

    // 2. ViewModel de Solicitudes (NUEVO)
    val solicitudesViewModel: SolicitudesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SolicitudesViewModel(apiService, context) as T
            }
        }
    )

    // --- LÓGICA DE PANTALLAS ---
    if (isLoggedIn) {
        // Si está logueado, miramos 'currentScreen' para saber qué mostrar
        when (currentScreen) {
            "home" -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onIrASolicitudes = {
                        // AQUÍ OCURRE EL CAMBIO: El usuario pidió ir a solicitudes
                        currentScreen = "solicitudes"
                        // Opcional: Recargar datos al entrar
                        solicitudesViewModel.cargarSolicitudes()
                    },
                    onLogout = {
                        Log.d("DEBUG_LOGOUT", "¡Click recibido! Cerrando sesión...")

                        // 1. Borramos token del disco
                        sessionManager.logout()

                        // 2. ¡LIMPIEZA DE MEMORIA! (Nuevo)
                        homeViewModel.limpiarDatos()

                        // 3. Reseteamos vista de solicitudes (por si acaso)
                        solicitudesViewModel.solicitudes = emptyList()

                        // 4. Cambiamos estado para ir al Login
                        isLoggedIn = false
                        currentScreen = "home"
                    }
                )
            }
            "solicitudes" -> {
                SolicitudesScreen(
                    viewModel = solicitudesViewModel,
                    onBack = {
                        // AQUÍ VOLVEMOS: El usuario tocó "atrás"
                        currentScreen = "home"
                        // Opcional: Recargar Home por si algo cambió
                        homeViewModel.cargarDatos()
                    }
                )
            }
        }
    } else {
        // Si NO está logueado, mostramos Login
        LoginScreen(
            apiService = apiService,
            context = context,
            onLoginSuccess = {
                // 1. Cambiamos estado para mostrar el Home
                isLoggedIn = true
                currentScreen = "home"

                // --- AGREGAMOS ESTA LÍNEA MÁGICA ---
                // Le ordenamos al HomeViewModel que cargue los datos INMEDIATAMENTE
                homeViewModel.cargarDatos()
            }
        )
    }
}