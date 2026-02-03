package cl.duoc.sut_mobile.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.sut_mobile.R
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.ui.viewmodel.LoginViewModel
import cl.duoc.sut_mobile.utils.SessionManager

@Composable
fun LoginScreen(
    apiService: ApiService,
    context: Context,
    onLoginSuccess: () -> Unit,
    onIrARegistro: () -> Unit
) {
    // 1. Instanciamos el ViewModel aquí mismo usando los parámetros recibidos
    val viewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(apiService, SessionManager(context)) as T
            }
        }
    )

    // 2. Estados del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Obtenemos el estado del login desde el ViewModel
    val loginState = viewModel.loginResult
    val isLoading = viewModel.isLoading

    // 3. Efecto: Si el login es exitoso, navegamos
    LaunchedEffect(loginState) {
        if (loginState == true) {
            viewModel.resetLoginState()
            onLoginSuccess() // <--- Esto avisa a MainActivity que cambie de pantalla
        } else if (loginState == false) {
            Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            viewModel.resetLoginState() // Reiniciamos para permitir otro intento
        }
    }

    // 4. Interfaz Gráfica
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo (Asegúrate de tener un logo o comenta esta parte)
        // Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo", modifier = Modifier.size(100.dp))

        Text(text = "Bienvenido a SUT", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. AGREGAMOS ESTE BOTÓN AL FINAL ---
        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}