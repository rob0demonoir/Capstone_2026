package cl.duoc.sut_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.sut_mobile.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val usuario = viewModel.usuario
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)), // Fondo gris claro
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()

            error != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ocurrió un error 😢", color = Color.Red)
                    Text(error, style = MaterialTheme.typography.bodySmall)
                    Button(onClick = { viewModel.reintentar() }) {
                        Text("Reintentar")
                    }
                }
            }

            usuario != null -> {
                // TARJETA DE PERFIL
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // 90% del ancho
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "¡Hola, ${usuario.nombre}!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = usuario.rol,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider()

                        Spacer(modifier = Modifier.height(16.dp))

                        InfoRow(label = "Email:", value = usuario.email)
                        InfoRow(label = "RUT:", value = usuario.rut ?: "No informado")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}