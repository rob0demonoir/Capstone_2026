/**package cl.duoc.sut_mobile.ui.screen

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
}**/

package cl.duoc.sut_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.sut_mobile.model.Noticia
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val usuario = viewModel.usuario
    val noticias = viewModel.noticias
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5)) // Fondo gris muy suave
    ) {
        if (isLoading && usuario == null) {
            // Loading inicial a pantalla completa
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (usuario != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 1. TARJETA DE USUARIO (Header)
                TarjetaUsuarioHeader(usuario)

                Spacer(modifier = Modifier.height(20.dp))

                // 2. CONTENIDO SEGÚN ROL
                // "Control de Tráfico"
                if (usuario.rol.uppercase().contains("ADMIN")) {
                    AdminDashboard()
                } else {
                    VecinoDashboard(noticias)
                }
            }
        }

        // Botón flotante para recargar si hay error o para actualizar
        FloatingActionButton(
            onClick = { viewModel.cargarDatos() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Recargar")
        }

        // Mostrar mensaje de error si existe (tipo Toast o texto abajo)
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            )
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun TarjetaUsuarioHeader(usuario: Usuario) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Hola, ${usuario.nombre}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black // Texto negro forzado
                )
                Text(
                    text = usuario.rol,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AdminDashboard() {
    Text(
        "Panel de Administración",
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text("Aquí irán las herramientas para gestionar solicitudes.", color = Color.Gray)

    // Aquí agregaremos botones más adelante
}

@Composable
fun VecinoDashboard(noticias: List<Noticia>) {
    Text(
        "Noticias de tu Barrio",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (noticias.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay noticias publicadas aún.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el botón flotante
        ) {
            items(noticias) { noticia ->
                NoticiaItem(noticia)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun NoticiaItem(noticia: Noticia) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = noticia.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Fecha y Autor
            Row {
                Text(
                    // Truco simple para limpiar la "T" de la fecha (2026-02-01T20:00 -> 2026-02-01 20:00)
                    text = noticia.fecha.replace("T", " ").take(16),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "| Por: ${noticia.autor}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = noticia.contenido,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.DarkGray
            )
        }
    }
}