/** package cl.duoc.sut_mobile.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.sut_mobile.ui.viewmodel.LoginViewModel

// 1. COMPONENTE CON ESTADO (Stateful)
// Este es el que llama MainActivity. Recibe el ViewModel y "desempaqueta" los datos.
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    // Llamamos a la parte visual pasándole los datos del ViewModel
    LoginContent(
        email = viewModel.email,
        onEmailChange = { viewModel.email = it },
        password = viewModel.password,
        onPasswordChange = { viewModel.password = it },
        isLoading = viewModel.isLoading,
        errorMessage = viewModel.errorMessage,
        onLoginClick = { viewModel.onLoginClick() }
    )
}

// 2. COMPONENTE SIN ESTADO (Stateless / Pure UI)
// Este solo sabe dibujar. Es fácil de previsualizar y reutilizar.
@Composable
fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
} **/

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
import cl.duoc.sut_mobile.R // Asegúrate que importe TU R
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.ui.viewmodel.LoginViewModel
import cl.duoc.sut_mobile.utils.SessionManager

@Composable
fun LoginScreen(
    apiService: ApiService,
    context: Context,
    onLoginSuccess: () -> Unit
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
    }
}