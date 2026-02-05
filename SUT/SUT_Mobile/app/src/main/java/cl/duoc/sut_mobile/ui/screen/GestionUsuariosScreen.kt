package cl.duoc.sut_mobile.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.ui.viewmodel.GestionUsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosScreen(
    viewModel: GestionUsuariosViewModel,
    onBack: () -> Unit
) {
    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    // ESTADO PARA CONTROLAR EL DIÁLOGO DE BORRADO
    var usuarioAEliminar by remember { mutableStateOf<Usuario?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Vecinos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.filtrar(it) },
                label = { Text("Buscar por nombre o RUT") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(viewModel.usuariosVisibles) { usuario ->
                        UsuarioItem(
                            usuario = usuario,
                            onCambiarRol = { viewModel.alternarRol(usuario) },
                            // Al hacer click en borrar, guardamos el usuario en la variable para mostrar el diálogo
                            onEliminar = { usuarioAEliminar = usuario }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (usuarioAEliminar != null) {
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Eliminar Usuario") },
            text = {
                Text("¿Estás seguro de que deseas eliminar a ${usuarioAEliminar?.nombre}?\n\nEsta acción borrará todos sus datos y no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Llamamos al ViewModel para borrar
                        usuarioAEliminar?.let { viewModel.eliminarUsuario(it.id) }
                        usuarioAEliminar = null // Cerramos diálogo
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UsuarioItem(
    usuario: Usuario,
    onCambiarRol: () -> Unit,
    onEliminar: () -> Unit // Nuevo parámetro
) {
    val esAdmin = usuario.rol == "ADMINISTRADOR"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (esAdmin) Color(0xFFFFF3E0) else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // INFO DEL USUARIO
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${usuario.nombre} ${usuario.apellido}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(text = usuario.rut, fontSize = 14.sp, color = Color.Gray)
                Text(text = usuario.email, fontSize = 12.sp, color = Color.LightGray)
            }

            // BOTONES DE ACCIÓN
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Botón Rol
                Button(
                    onClick = onCambiarRol,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (esAdmin) Color(0xFFEF6C00) else Color(0xFF2E7D32)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = if (esAdmin) Icons.Default.Security else Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (esAdmin) "ADMIN" else "VECINO", fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // --- NUEVO: BOTÓN ELIMINAR ---
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Usuario",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}