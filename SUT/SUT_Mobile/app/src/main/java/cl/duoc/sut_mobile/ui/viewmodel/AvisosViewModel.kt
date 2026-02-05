/**package cl.duoc.sut_mobile.ui.viewmodel

import android.content.Context
import cl.duoc.sut_mobile.utils.FileUtils
import cl.duoc.sut_mobile.utils.toRequestBody
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.Aviso
import cl.duoc.sut_mobile.model.CrearAvisoRequest
import cl.duoc.sut_mobile.model.TipoAviso
import cl.duoc.sut_mobile.network.ApiService
import android.net.Uri
import kotlinx.coroutines.launch

class AvisosViewModel(private val apiService: ApiService) : ViewModel() {

    var avisos by mutableStateOf<List<Aviso>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun cargarAvisos() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.getAvisos()
                if (response.isSuccessful) {
                    avisos = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error al cargar el tablón"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    fun publicarAviso(context: Context, titulo: String, descripcion: String, tipo: TipoAviso, precio: String, imagenUri: Uri?) {
        viewModelScope.launch {
            isLoading = true
            try {
                var urlImagenSubida: String? = null

                // 1. CHEQUEO DE URI
                Log.d("DEBUG_UPLOAD", "Paso 1: Iniciando publicación. URI recibida: $imagenUri")

                if (imagenUri != null) {
                    // 2. CONVERSIÓN DE ARCHIVO
                    val multipartImage = FileUtils.getMultipartBodyFromUri(context, imagenUri)

                    if (multipartImage == null) {
                        Log.e("DEBUG_UPLOAD", "ERROR FATAL: FileUtils devolvió null. No se pudo leer el archivo.")
                    } else {
                        Log.d("DEBUG_UPLOAD", "Paso 2: Archivo convertido a Multipart correctamente. Enviando...")

                        // 3. LLAMADA A LA API
                        val uploadResponse = apiService.subirImagen(multipartImage)

                        Log.d("DEBUG_UPLOAD", "Paso 3: Respuesta del Servidor Código: ${uploadResponse.code()}")

                        if (uploadResponse.isSuccessful) {
                            val body = uploadResponse.body()
                            Log.d("DEBUG_UPLOAD", "Paso 4: Body recibido: $body")

                            urlImagenSubida = body?.get("url")
                            Log.d("DEBUG_UPLOAD", "Paso 5: URL extraída: $urlImagenSubida")
                        } else {
                            val errorBody = uploadResponse.errorBody()?.string()
                            Log.e("DEBUG_UPLOAD", "ERROR API: La subida falló. Mensaje: $errorBody")
                        }
                    }
                } else {
                    Log.d("DEBUG_UPLOAD", "No hay imagen seleccionada, se enviará null.")
                }

                // 4. CREAMOS EL AVISO
                val precioInt = precio.toIntOrNull()
                val request = CrearAvisoRequest(titulo, descripcion, tipo, precioInt, urlImagenSubida)

                Log.d("DEBUG_UPLOAD", "Paso 6: Enviando Request final de Aviso: $request")

                val response = apiService.publicarAviso(request)

                if (response.isSuccessful) {
                    Log.d("DEBUG_UPLOAD", "¡ÉXITO TOTAL! Aviso publicado.")
                    cargarAvisos()
                } else {
                    Log.e("DEBUG_UPLOAD", "Error al publicar aviso: ${response.errorBody()?.string()}")
                    errorMessage = "Error al publicar"
                }
            } catch (e: Exception) {
                Log.e("DEBUG_UPLOAD", "EXCEPCIÓN CRÍTICA: ${e.message}")
                e.printStackTrace()
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun borrarAviso(id: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.borrarAviso(id)
                if (response.isSuccessful) {
                    // Quitamos el aviso de la lista localmente para que se sienta rápido
                    avisos = avisos.filter { it.id != id }
                } else {
                    errorMessage = "No se pudo borrar el aviso"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            }
        }
    }
}**/
package cl.duoc.sut_mobile.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.sut_mobile.model.Aviso
import cl.duoc.sut_mobile.model.TipoAviso
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.utils.FileUtils
import cl.duoc.sut_mobile.utils.toRequestBody
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AvisosViewModel(private val apiService: ApiService) : ViewModel() {

    var avisos by mutableStateOf<List<Aviso>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun cargarAvisos() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.getAvisos()
                if (response.isSuccessful) {
                    avisos = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error al cargar el tablón"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    fun publicarAviso(context: Context, titulo: String, descripcion: String, tipo: TipoAviso, precio: String, imagenUri: Uri?) {
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d("DEBUG_UPLOAD", "Iniciando publicación Multipart...")

                // 1. PREPARAR TEXTOS (Convertir a RequestBody)
                val tituloPart = titulo.toRequestBody()
                val descPart = descripcion.toRequestBody()
                // TipoAviso es Enum, enviamos su nombre (ej: "VENTA")
                val tipoPart = tipo.name.toRequestBody()

                // Precio es opcional
                val precioInt = precio.toIntOrNull()
                val precioPart = precioInt?.toRequestBody()

                // 2. PREPARAR IMAGEN (MultipartBody.Part)
                var imagenPart: MultipartBody.Part? = null

                if (imagenUri != null) {
                    // Usamos FileUtils para obtener el archivo real desde la URI
                    val file: File? = FileUtils.getFileFromUri(context, imagenUri)

                    if (file != null && file.exists()) {
                        Log.d("DEBUG_UPLOAD", "Archivo encontrado: ${file.name} (${file.length()} bytes)")

                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

                        // IMPORTANTE: El primer parámetro "imagen" debe coincidir con el @RequestParam("imagen") del Backend
                        imagenPart = MultipartBody.Part.createFormData("imagen", file.name, requestFile)
                    } else {
                        Log.e("DEBUG_UPLOAD", "No se pudo convertir la URI a File")
                    }
                }

                // 3. LLAMADA ÚNICA AL API (Todo junto)
                val response = apiService.publicarAviso(
                    titulo = tituloPart,
                    descripcion = descPart,
                    precio = precioPart,
                    tipo = tipoPart,
                    imagen = imagenPart
                )

                if (response.isSuccessful) {
                    Log.d("DEBUG_UPLOAD", "¡ÉXITO! Aviso publicado correctamente.")
                    cargarAvisos() // Recargar la lista
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("DEBUG_UPLOAD", "Error en servidor: $errorBody")
                    errorMessage = "Error al publicar: $errorBody"
                }

            } catch (e: Exception) {
                Log.e("DEBUG_UPLOAD", "EXCEPCIÓN: ${e.message}")
                e.printStackTrace()
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun borrarAviso(id: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.borrarAviso(id)
                if (response.isSuccessful) {
                    avisos = avisos.filter { it.id != id }
                } else {
                    errorMessage = "No se pudo borrar el aviso"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            }
        }
    }
}