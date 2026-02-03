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
}

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
} **/
package cl.duoc.sut_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import cl.duoc.sut_mobile.model.Solicitud
import cl.duoc.sut_mobile.model.EstadoSolicitud
import cl.duoc.sut_mobile.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onIrASolicitudes: () -> Unit,
    onLogout: () -> Unit// <--- CAMBIO 1: Recibimos la función de navegación
) {
    val usuario = viewModel.usuario
    val noticias = viewModel.noticias
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
    ) {
        if (isLoading && usuario == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (usuario != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 1. TARJETA DE USUARIO
                TarjetaUsuarioHeader(usuario = usuario, onLogout = onLogout)

                Spacer(modifier = Modifier.height(20.dp))

                // 2. CONTENIDO SEGÚN ROL
                if (usuario.rol.uppercase().contains("ADMIN")) {
                    AdminDashboard(viewModel)
                } else {
                    // <--- CAMBIO 2: Le pasamos la función al Dashboard del vecino
                    VecinoDashboard(noticias, onIrASolicitudes)
                }
            }
        }

        // Botón recargar (Floating Action Button)
        FloatingActionButton(
            onClick = { viewModel.cargarDatos() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Recargar")
        }

        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            )
        }
    }
}
@Composable
fun TarjetaUsuarioHeader(
    usuario: Usuario,
    onLogout: () -> Unit // <--- Recibimos la función
) {
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

            // Columna con datos del usuario
            Column(modifier = Modifier.weight(1f)) { // .weight(1f) hace que ocupe el espacio disponible
                Text("Hola, ${usuario.nombre}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Text(usuario.rol, fontSize = 14.sp, color = Color.Gray)
            }

            // --- BOTÓN DE CERRAR SESIÓN ---
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.ExitToApp, // Icono de puerta/salida
                    contentDescription = "Cerrar Sesión",
                    tint = Color.Red // Lo ponemos rojo para que se entienda que es salir
                )
            }
        }
    }
}

// ... AdminDashboard queda igual ...
@Composable
fun AdminDashboard(viewModel: HomeViewModel) { // Pasamos el ViewModel para usar las acciones
    val solicitudes = viewModel.solicitudesAdmin

    // Separamos pendientes de las listas
    val pendientes = solicitudes.filter { it.estado == EstadoSolicitud.PENDIENTE }
    val historial = solicitudes.filter { it.estado != EstadoSolicitud.PENDIENTE }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Panel de Control",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Solicitudes por revisar: ${pendientes.size}",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SECCIÓN PENDIENTES
        if (pendientes.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(
                    "¡Todo al día! No hay pendientes.",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF2E7D32)
                )
            }
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) { // Altura limitada para scroll
                items(pendientes) { solicitud ->
                    SolicitudAdminItem(
                        solicitud = solicitud,
                        onAprobar = { viewModel.responderSolicitud(solicitud.id, true) },
                        onRechazar = { viewModel.responderSolicitud(solicitud.id, false) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // (Opcional) Aquí podrías mostrar el historial...
    }
}

@Composable
fun SolicitudAdminItem(
    solicitud: Solicitud,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(solicitud.nombreSolicitante, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Certificado de ${solicitud.tipo}", fontSize = 14.sp, color = Color.Gray)
                    Text("Fecha: ${solicitud.fechaSolicitud.take(10)}", fontSize = 12.sp, color = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón Rechazar
                Button(
                    onClick = onRechazar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Rechazar")
                }

                // Botón Aprobar
                Button(
                    onClick = onAprobar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32)),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Aprobar")
                }
            }
        }
    }
}

@Composable
fun VecinoDashboard(
    noticias: List<Noticia>,
    onIrASolicitudes: () -> Unit // <--- CAMBIO 3: Recibimos la función aquí también
) {
    Text(
        "Noticias de tu Barrio",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(12.dp))

    // --- CAMBIO 4: BOTÓN DE ACCIÓN ---
    // Agregamos el botón antes de la lista de noticias (o después, como prefieras)
    Button(
        onClick = onIrASolicitudes, // <--- Aquí se ejecuta la magia
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("Gestionar mis Certificados")
    }

    Spacer(modifier = Modifier.height(16.dp))
    // ---------------------------------

    if (noticias.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            Text("No hay noticias publicadas aún.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(noticias) { noticia ->
                NoticiaItem(noticia)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ... NoticiaItem queda igual ...
@Composable
fun NoticiaItem(noticia: Noticia) {
    // (Tu código anterior del item de noticia)
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(noticia.titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(noticia.fecha.replace("T", " ").take(16), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("| Por: ${noticia.autor}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(noticia.contenido, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}