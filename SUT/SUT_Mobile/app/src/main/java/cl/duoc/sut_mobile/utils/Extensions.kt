package cl.duoc.sut_mobile.utils // Asegúrate de que coincida con tu carpeta

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// Convierte un String a RequestBody (necesario para Retrofit @Part)
fun String.toRequestBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

// Convierte un Int a RequestBody
fun Int.toRequestBody(): RequestBody {
    return this.toString().toRequestBody("text/plain".toMediaTypeOrNull())
}

// Opcional: Si necesitas convertir Double o Long también
fun Long.toRequestBody(): RequestBody {
    return this.toString().toRequestBody("text/plain".toMediaTypeOrNull())
}