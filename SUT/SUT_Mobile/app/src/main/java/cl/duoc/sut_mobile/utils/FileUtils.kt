package cl.duoc.sut_mobile.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            // Crear archivo temporal
            val tempFile = File.createTempFile("upload_temp", ".jpg", context.cacheDir)
            tempFile.deleteOnExit()

            // Copiar datos
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}