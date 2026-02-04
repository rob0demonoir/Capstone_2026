package cl.duoc.sut_mobile.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.sut_mobile.model.Aviso
import cl.duoc.sut_mobile.model.TipoAviso
import cl.duoc.sut_mobile.ui.viewmodel.AvisosViewModel
// --- IMPORTANTE: Usamos Coil 3 ---
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.text.NumberFormat
import java.util.Locale

// --- CAMBIA ESTO POR LA IP DE TU SERVIDOR ---
const val BASE_URL_IMAGENES = "http://192.168.100.19:8082"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvisosScreen(
    viewModel: AvisosViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarAvisos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tablón de la Comunidad") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Publicar Aviso")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.avisos.isEmpty()) {
                Text("No hay avisos aún.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(viewModel.avisos) { aviso ->
                        AvisoItem(
                            aviso = aviso,
                            onContactar = {
                                try {
                                    val numero = formatearParaWhatsapp(aviso.telefonoContacto)
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse("https://wa.me/$numero")
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onBorrar = { viewModel.borrarAviso(aviso.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showDialog) {
        CrearAvisoDialog(
            onDismiss = { showDialog = false },
            onPublicar = { titulo, desc, tipo, precio, uri ->
                // --- AQUÍ ESTABA EL ERROR ---
                // Ahora pasamos 'context' primero y 'uri' al final, coincidiendo con el ViewModel
                viewModel.publicarAviso(context, titulo, desc, tipo, precio, uri)

                showDialog = false
            }
        )
    }
}

@Composable
fun AvisoItem(aviso: Aviso, onContactar: () -> Unit, onBorrar: () -> Unit) {
    val colorTipo = when(aviso.tipo) {
        TipoAviso.VENTA -> Color(0xFFE57373)
        TipoAviso.SERVICIO -> Color(0xFF64B5F6)
        TipoAviso.EVENTO -> Color(0xFFAED581)
        TipoAviso.BUSCO -> Color(0xFFFFD54F)
    }

    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column {
            // IMAGEN
            if (!aviso.urlImagen.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("$BASE_URL_IMAGENES${aviso.urlImagen}")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto del aviso",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(aviso.tipo.name) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = colorTipo)
                    )
                    if (aviso.esMio) {
                        IconButton(onClick = onBorrar) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                        }
                    }
                }

                Text(aviso.titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                if (aviso.precio != null && aviso.precio > 0) {
                    val formatoPrecio = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(aviso.precio)
                    Text(formatoPrecio, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(aviso.descripcion, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Publicado por:", fontSize = 12.sp, color = Color.Gray)
                        Text(aviso.nombrePublicador, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Button(onClick = onContactar, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WhatsApp")
                    }
                }
            }
        }
    }
}

@Composable
fun CrearAvisoDialog(
    onDismiss: () -> Unit,
    onPublicar: (String, String, TipoAviso, String, Uri?) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoAviso.VENTA) }
    var expanded by remember { mutableStateOf(false) }

    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Aviso") },
        text = {
            Column {
                Box {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Tipo: ${tipoSeleccionado.name}")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        TipoAviso.values().forEach { tipo ->
                            DropdownMenuItem(text = { Text(tipo.name) }, onClick = { tipoSeleccionado = tipo; expanded = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = precio, onValueChange = { if (it.all { char -> char.isDigit() }) precio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN FOTO
                if (imagenUri == null) {
                    OutlinedButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar Foto")
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        AsyncImage(
                            model = imagenUri,
                            contentDescription = "Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { imagenUri = null },
                            modifier = Modifier.align(Alignment.TopEnd).background(Color.White.copy(alpha = 0.7f), shape = MaterialTheme.shapes.small)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Quitar foto", tint = Color.Red)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (titulo.isNotBlank() && descripcion.isNotBlank()) {
                    onPublicar(titulo, descripcion, tipoSeleccionado, precio, imagenUri)
                }
            }) { Text("Publicar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

fun formatearParaWhatsapp(numeroSucio: String): String {
    val soloNumeros = numeroSucio.filter { it.isDigit() }
    val ultimos8 = if (soloNumeros.length >= 8) soloNumeros.takeLast(8) else soloNumeros
    return "569$ultimos8"
}