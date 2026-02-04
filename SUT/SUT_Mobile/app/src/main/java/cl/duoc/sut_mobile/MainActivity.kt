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
import cl.duoc.sut_mobile.ui.screen.RegistroScreen
import cl.duoc.sut_mobile.ui.screen.SolicitudesScreen
import cl.duoc.sut_mobile.ui.screen.GestionUsuariosScreen // Importante
import cl.duoc.sut_mobile.ui.viewmodel.GestionUsuariosViewModel // Importante
import cl.duoc.sut_mobile.ui.theme.SUT_MobileTheme
import cl.duoc.sut_mobile.ui.viewmodel.HomeViewModel
import cl.duoc.sut_mobile.ui.viewmodel.SolicitudesViewModel
import cl.duoc.sut_mobile.utils.SessionManager
//para el workmanager
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cl.duoc.sut_mobile.ui.screen.AvisosScreen
import cl.duoc.sut_mobile.ui.viewmodel.AvisosViewModel
import cl.duoc.sut_mobile.worker.NotificacionWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofitClient = RetrofitClient(this)
        val apiService = retrofitClient.instance.create(ApiService::class.java)

        setContent {
            SUT_MobileTheme {
                val context = LocalContext.current

                val launcherPermisos = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { esConcedido ->
                        if (esConcedido) {
                            iniciarWorkerNoticias(context)
                        }
                    }
                )

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcherPermisos.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        iniciarWorkerNoticias(context)
                    }
                }
                AppNavigation(apiService = apiService, context = this)
            }
        }
    }

    private fun iniciarWorkerNoticias(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<NotificacionWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "worker_noticias_vecinos",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun AppNavigation(apiService: ApiService, context: Context) {
    val sessionManager = remember { SessionManager(context) }

    var isLoggedIn by remember { mutableStateOf(!sessionManager.getToken().isNullOrBlank()) }
    var isRegistering by remember { mutableStateOf(false) }

    // Puede ser: "home", "solicitudes", "gestion_usuarios"
    var currentScreen by remember { mutableStateOf("home") }


    // --- VIEWMODELS ---
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(apiService) as T
            }
        }
    )

    val solicitudesViewModel: SolicitudesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SolicitudesViewModel(apiService, context) as T
            }
        }
    )

    val gestionUsuariosViewModel: GestionUsuariosViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GestionUsuariosViewModel(apiService) as T
            }
        }
    )

    val avisosViewModel: AvisosViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AvisosViewModel(apiService) as T
            }
        }
    )

    // --- LÓGICA DE NAVEGACIÓN ---
    if (isLoggedIn) {
        // --- ZONA PRIVADA ---
        when (currentScreen) {
            "home" -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onIrASolicitudes = {
                        currentScreen = "solicitudes"
                        solicitudesViewModel.cargarSolicitudes()
                    },
                    onIrAAvisos = { currentScreen = "avisos" },
                    // --- NUEVO: Callback para ir a gestión de usuarios ---
                    onIrAGestionUsuarios = {
                        currentScreen = "gestion_usuarios"
                        gestionUsuariosViewModel.cargarUsuarios() // Cargamos la lista al entrar
                    },
                    onLogout = {
                        Log.d("DEBUG_LOGOUT", "¡Click recibido! Cerrando sesión...")
                        sessionManager.logout()
                        homeViewModel.limpiarDatos()
                        solicitudesViewModel.solicitudes = emptyList()

                        isLoggedIn = false
                        isRegistering = false
                        currentScreen = "home"
                    }
                )
            }
            "solicitudes" -> {
                SolicitudesScreen(
                    viewModel = solicitudesViewModel,
                    onBack = {
                        currentScreen = "home"
                        homeViewModel.cargarDatos()
                    }
                )
            }
            "avisos" -> {
                AvisosScreen(
                    viewModel = avisosViewModel,
                    onBack = { currentScreen = "home" }
                )
            }
            // --- NUEVO: Pantalla de Gestión ---
            "gestion_usuarios" -> {
                GestionUsuariosScreen(
                    viewModel = gestionUsuariosViewModel,
                    onBack = {
                        currentScreen = "home"
                        // Opcional: Recargar home por si cambiaste tu propio rol (raro pero posible)
                        homeViewModel.cargarDatos()
                    }
                )
            }
        }
    } else {
        // --- ZONA PÚBLICA ---
        if (isRegistering) {
            RegistroScreen(
                apiService = apiService,
                onRegistroExitoso = {
                    isRegistering = false
                },
                onCancelar = {
                    isRegistering = false
                }
            )
        } else {
            LoginScreen(
                apiService = apiService,
                context = context,
                onLoginSuccess = {
                    isLoggedIn = true
                    currentScreen = "home"
                    homeViewModel.cargarDatos()
                },
                onIrARegistro = {
                    isRegistering = true
                }
            )
        }
    }
}