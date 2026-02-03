package cl.duoc.sut_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.sut_mobile.model.EstadoSolicitud
import cl.duoc.sut_mobile.model.Solicitud
import cl.duoc.sut_mobile.ui.viewmodel.SolicitudesViewModel

@Composable
fun SolicitudesScreen(
    viewModel: SolicitudesViewModel,
    onBack: () -> Unit // Callback para volver atrás
) {
    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarSolicitudes()
    }

    val solicitudes = viewModel.solicitudes
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Solicitud")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header con botón volver
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text("Mis Certificados", style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (solicitudes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No has solicitado certificados aún.", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(solicitudes) { solicitud ->
                        SolicitudItem(solicitud, onDescargar = {
                            viewModel.descargarCertificado(solicitud.id)
                        })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (viewModel.mensaje != null) {
        // Mostrar feedback rápido
        Text(text = viewModel.mensaje!!, color = Color.Blue, modifier = Modifier.padding(16.dp))
        // Reset mensaje (opcional, mejor usar Snackbar)
    }

    if (showDialog) {
        NuevaSolicitudDialog(
            onDismiss = { showDialog = false },
            onConfirm = { tipo, comentario ->
                viewModel.crearSolicitud(tipo, comentario)
                showDialog = false
            }
        )
    }
}

@Composable
fun SolicitudItem(solicitud: Solicitud, onDescargar: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Certificado de ${solicitud.tipo}", fontWeight = FontWeight.Bold)
                Text(text = solicitud.fechaSolicitud.take(10), fontSize = 12.sp, color = Color.Gray)

                // Badge de Estado
                val colorEstado = when (solicitud.estado) {
                    EstadoSolicitud.APROBADA -> Color(0xFF4CAF50) // Verde
                    EstadoSolicitud.RECHAZADA -> Color(0xFFF44336) // Rojo
                    EstadoSolicitud.PENDIENTE -> Color(0xFFFF9800) // Naranja
                }

                Text(
                    text = solicitud.estado.name,
                    color = colorEstado,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Botón de Descarga (Solo si está aprobada)
            if (solicitud.estado == EstadoSolicitud.APROBADA) {
                IconButton(onClick = onDescargar) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Descargar PDF",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun NuevaSolicitudDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var comentario by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Solicitar Certificado") },
        text = {
            Column {
                Text("Tipo: Residencia (Único disponible por ahora)")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Motivo / Comentario") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm("RESIDENCIA", comentario) }) {
                Text("Enviar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}