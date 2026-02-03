package cl.duoc.sut_mobile.utils

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("sut_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "auth_token"
    }

    // Guardar token
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    // Obtener token (listo para usar con "Bearer ")
    fun getToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null)
        return if (token != null) "Bearer $token" else null
    }

    fun logout() {
        val editor = prefs.edit()
        editor.remove("auth_token") // Borra el token
        editor.apply()
    }
}