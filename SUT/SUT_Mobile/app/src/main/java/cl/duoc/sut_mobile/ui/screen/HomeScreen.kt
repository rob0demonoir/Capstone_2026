package cl.duoc.sut_mobile.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cl.duoc.sut_mobile.model.EstadoSolicitud
import cl.duoc.sut_mobile.model.Noticia
import cl.duoc.sut_mobile.model.Solicitud
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.ui.viewmodel.HomeViewModel
import coil3.compose.AsyncImage

// Asegúrate que la IP sea la correcta
const val BASE_URL = "http://192.168.100.19:8082"

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onIrASolicitudes: () -> Unit,
    onIrAGestionUsuarios: () -> Unit,
    onIrAAvisos: () -> Unit,
    onLogout: () -> Unit
) {
    val usuario = viewModel.usuario
    val noticias = viewModel.noticias
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    var showEditDialog by remember { mutableStateOf(false) }

    // ESTADO PARA LA NOTICIA SELECCIONADA (DETALLE)
    var noticiaSeleccionada by remember { mutableStateOf<Noticia?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
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
                TarjetaUsuarioHeader(
                    usuario = usuario,
                    onLogout = onLogout,
                    onEdit = { showEditDialog = true }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 2. CONTENIDO SEGÚN ROL
                if (usuario.rol.uppercase().contains("ADMIN")) {
                    AdminDashboard(
                        viewModel,
                        noticias,
                        onIrAGestionUsuarios,
                        onIrAAvisos,
                        onVerNoticia = { noticiaSeleccionada = it } // Pasamos el evento
                    )
                } else {
                    VecinoDashboard(
                        noticias,
                        onIrASolicitudes,
                        onIrAAvisos,
                        onVerNoticia = { noticiaSeleccionada = it } // Pasamos el evento
                    )
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
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            )
        }
    }

    // DIALOGO EDITAR PERFIL
    if (showEditDialog && usuario != null) {
        EditarPerfilDialog(
            usuario = usuario,
            onDismiss = { showEditDialog = false },
            onGuardar = { tel, dir, mail ->
                viewModel.actualizarPerfil(tel, dir, mail)
                showEditDialog = false
            }
        )
    }

    // DIALOGO DETALLE NOTICIA (NUEVO)
    if (noticiaSeleccionada != null) {
        DetalleNoticiaDialog(
            noticia = noticiaSeleccionada!!,
            onDismiss = { noticiaSeleccionada = null }
        )
    }
}

@Composable
fun TarjetaUsuarioHeader(usuario: Usuario, onLogout: () -> Unit, onEdit: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Hola, ${usuario.nombre}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(usuario.rol, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun EditarPerfilDialog(usuario: Usuario, onDismiss: () -> Unit, onGuardar: (String, String, String) -> Unit) {
    var telefono by remember { mutableStateOf(usuario.telefono) }
    var direccion by remember { mutableStateOf(usuario.direccion) }
    var email by remember { mutableStateOf(usuario.email) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Mis Datos") },
        text = { Column {
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        }},
        confirmButton = { Button(onClick = { onGuardar(telefono, direccion, email) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// =========================================================
// NUEVO COMPONENTE: DETALLE DE NOTICIA (POPUP GRANDE)
// =========================================================
@Composable
fun DetalleNoticiaDialog(noticia: Noticia, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f), // Ocupa el 85% de la altura
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // IMAGEN FULL WIDTH
                if (!noticia.urlImagen.isNullOrEmpty()) {
                    val fullUrl = "$BASE_URL${noticia.urlImagen}"
                    AsyncImage(
                        model = fullUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp), // Más alto que en la tarjeta pequeña
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    // TÍTULO GRANDE
                    Text(
                        text = noticia.titulo,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // AUTOR Y FECHA
                    val autorName = if (noticia.autor is Usuario) noticia.autor.nombre else "Admin"
                    Text(
                        text = "Publicado por $autorName",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // CONTENIDO COMPLETO
                    Text(
                        text = noticia.contenido,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }

                // Botón Cerrar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun AdminDashboard(
    viewModel: HomeViewModel,
    noticias: List<Noticia>,
    onIrAGestionUsuarios: () -> Unit,
    onIrAAvisos: () -> Unit,
    onVerNoticia: (Noticia) -> Unit // Nuevo parámetro
) {
    val solicitudes = viewModel.solicitudesAdmin
    val pendientes = solicitudes.filter { it.estado == EstadoSolicitud.PENDIENTE }
    var showNoticiaDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {

        Text("Panel de Control", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))

        // Botón Naranja (Secondary)
        Button(
            onClick = onIrAGestionUsuarios,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Gestionar Usuarios y Roles")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Teal Medio (Tertiary)
        Button(
            onClick = onIrAAvisos,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tablón de Avisos")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECCIÓN SOLICITUDES ---
        Text("Solicitudes Pendientes (${pendientes.size})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        if (pendientes.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("¡Todo al día! No hay pendientes.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        } else {
            pendientes.forEach { solicitud ->
                SolicitudAdminItem(
                    solicitud = solicitud,
                    onAprobar = { viewModel.responderSolicitud(solicitud.id, true) },
                    onRechazar = { viewModel.responderSolicitud(solicitud.id, false) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECCIÓN NOTICIAS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Noticias Publicadas", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            IconButton(onClick = { showNoticiaDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva", tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (noticias.isEmpty()) {
            Text("No hay noticias aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            noticias.forEach { noticia ->
                NoticiaItem(
                    noticia = noticia,
                    onClick = { onVerNoticia(noticia) } // Conectado el click
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showNoticiaDialog) {
        NuevaNoticiaDialog(
            onDismiss = { showNoticiaDialog = false },
            onPublicar = { titulo, contenido, uri ->
                viewModel.publicarNoticia(context, titulo, contenido, uri)
                showNoticiaDialog = false
            }
        )
    }
}

@Composable
fun VecinoDashboard(
    noticias: List<Noticia>,
    onIrASolicitudes: () -> Unit,
    onIrAAvisos: () -> Unit,
    onVerNoticia: (Noticia) -> Unit // Nuevo parámetro
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Noticias de tu Barrio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(12.dp))

            // Botón Naranja
            Button(onClick = onIrASolicitudes, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("Gestionar mis Certificados")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Botón Teal
            Button(onClick = onIrAAvisos, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tablón de Avisos")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (noticias.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No hay noticias publicadas aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(noticias) { noticia ->
                NoticiaItem(
                    noticia = noticia,
                    onClick = { onVerNoticia(noticia) } // Conectado el click
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SolicitudAdminItem(solicitud: Solicitud, onAprobar: () -> Unit, onRechazar: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(solicitud.nombreSolicitante, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Certificado de ${solicitud.tipo}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onRechazar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Rechazar")
                }
                Button(
                    onClick = onAprobar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Aprobar")
                }
            }
        }
    }
}

@Composable
fun NuevaNoticiaDialog(
    onDismiss: () -> Unit,
    onPublicar: (String, String, Uri?) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publicar Noticia Oficial") },
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
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    // Usamos SurfaceVariant para diferenciar el área de carga
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (imagenUri != null) {
                            AsyncImage(
                                model = imagenUri,
                                contentDescription = "Preview",
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Foto seleccionada", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Agregar foto (Opcional)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank()) {
                        onPublicar(titulo, contenido, imagenUri)
                    }
                }
            ) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun NoticiaItem(
    noticia: Noticia,
    onClick: () -> Unit // Nuevo parámetro
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // AHORA ES CLICKEABLE
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!noticia.urlImagen.isNullOrEmpty()) {
                val fullUrl = "$BASE_URL${noticia.urlImagen}"
                AsyncImage(
                    model = fullUrl,
                    contentDescription = noticia.titulo,
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            // Título fuerte
            Text(noticia.titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                val autorName = if (noticia.autor is Usuario) noticia.autor.nombre else "Admin"
                // Texto secundario
                Text("| Por: $autorName", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Contenido limitado
            Text(
                text = noticia.contenido,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3, // Limitamos para que no sea eterno
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}