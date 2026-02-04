package cl.duoc.sut_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
            // --- BUSCADOR ---
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.filtrar(it) },
                label = { Text("Buscar por nombre o RUT") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- LISTA DE USUARIOS ---
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(viewModel.usuariosVisibles) { usuario ->
                        UsuarioItem(
                            usuario = usuario,
                            onCambiarRol = { viewModel.alternarRol(usuario) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UsuarioItem(usuario: Usuario, onCambiarRol: () -> Unit) {
    val esAdmin = usuario.rol == "ADMINISTRADOR"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (esAdmin) Color(0xFFFFF3E0) else Color.White // Naranjo suave si es admin
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${usuario.nombre} ${usuario.apellido}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(text = usuario.rut, fontSize = 14.sp, color = Color.Gray)
                Text(text = usuario.email, fontSize = 12.sp, color = Color.LightGray)
            }

            // BOTÓN DE ROL
            Button(
                onClick = onCambiarRol,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (esAdmin) Color(0xFFEF6C00) else Color(0xFF2E7D32)
                )
            ) {
                Icon(
                    imageVector = if (esAdmin) Icons.Default.Security else Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (esAdmin) "ADMINISTRADOR" else "VECINO")
            }
        }
    }
}