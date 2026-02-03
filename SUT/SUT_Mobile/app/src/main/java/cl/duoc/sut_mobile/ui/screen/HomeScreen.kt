package cl.duoc.sut_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings // Icono para gestión
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cl.duoc.sut_mobile.model.EstadoSolicitud
import cl.duoc.sut_mobile.model.Noticia
import cl.duoc.sut_mobile.model.Solicitud
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onIrASolicitudes: () -> Unit,
    onIrAGestionUsuarios: () -> Unit, // <--- NUEVO PARÁMETRO: Recibimos la navegación
    onLogout: () -> Unit
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
                    // Le pasamos la navegación al Dashboard de Admin
                    AdminDashboard(viewModel, onIrAGestionUsuarios)
                } else {
                    VecinoDashboard(noticias, onIrASolicitudes)
                }
            }
        }

        // Botón recargar
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
    onLogout: () -> Unit
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

            Column(modifier = Modifier.weight(1f)) {
                Text("Hola, ${usuario.nombre}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Text(usuario.rol, fontSize = 14.sp, color = Color.Gray)
            }

            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar Sesión",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun AdminDashboard(
    viewModel: HomeViewModel,
    onIrAGestionUsuarios: () -> Unit // <--- Recibimos la función aquí
) {
    val solicitudes = viewModel.solicitudesAdmin
    val pendientes = solicitudes.filter { it.estado == EstadoSolicitud.PENDIENTE }
    var showNoticiaDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Panel de Control",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- NUEVO BOTÓN: GESTIÓN DE USUARIOS ---
            Button(
                onClick = onIrAGestionUsuarios,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF6C00) // Un naranjo/ámbar para diferenciarlo
                )
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestionar Usuarios y Roles")
            }
            // ----------------------------------------

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Solicitudes por revisar: ${pendientes.size}",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

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
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
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
        }

        // Botón flotante para noticias
        FloatingActionButton(
            onClick = { showNoticiaDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Publicar Noticia", tint = Color.White)
        }
    }

    if (showNoticiaDialog) {
        NuevaNoticiaDialog(
            onDismiss = { showNoticiaDialog = false },
            onPublicar = { titulo, contenido ->
                viewModel.publicarNoticia(titulo, contenido)
                showNoticiaDialog = false
            }
        )
    }
}

// ... El resto de componentes (NuevaNoticiaDialog, SolicitudAdminItem, VecinoDashboard, NoticiaItem)
// NO CAMBIAN, pero los incluyo para que el archivo esté completo.

@Composable
fun NuevaNoticiaDialog(onDismiss: () -> Unit, onPublicar: (String, String) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publicar Anuncio") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido del mensaje") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (titulo.isNotBlank() && contenido.isNotBlank()) onPublicar(titulo, contenido) }) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun SolicitudAdminItem(solicitud: Solicitud, onAprobar: () -> Unit, onRechazar: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(solicitud.nombreSolicitante, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Certificado de ${solicitud.tipo}", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onRechazar, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red), modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("Rechazar")
                }
                Button(onClick = onAprobar, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32)), modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Text("Aprobar")
                }
            }
        }
    }
}

@Composable
fun VecinoDashboard(noticias: List<Noticia>, onIrASolicitudes: () -> Unit) {
    Text("Noticias de tu Barrio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))

    Button(onClick = onIrASolicitudes, modifier = Modifier.fillMaxWidth()) {
        Text("Gestionar mis Certificados")
    }
    Spacer(modifier = Modifier.height(16.dp))

    if (noticias.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            Text("No hay noticias publicadas aún.", color = Color.Gray)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
            items(noticias) { noticia ->
                NoticiaItem(noticia)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun NoticiaItem(noticia: Noticia) {
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