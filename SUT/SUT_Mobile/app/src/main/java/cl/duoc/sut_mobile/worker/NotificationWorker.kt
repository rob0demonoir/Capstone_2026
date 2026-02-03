package cl.duoc.sut_mobile.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.duoc.sut_mobile.MainActivity
import cl.duoc.sut_mobile.R
import cl.duoc.sut_mobile.network.ApiService
import cl.duoc.sut_mobile.network.RetrofitClient
import cl.duoc.sut_mobile.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificacionWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Instanciamos las herramientas necesarias manualmente
                // (Porque en el Worker no tenemos acceso fácil a los ViewModels)
                val context = applicationContext
                val sessionManager = SessionManager(context)
                val retrofit = RetrofitClient(context)
                val apiService = retrofit.instance.create(ApiService::class.java)

                // 2. Consultamos las noticias al servidor
                val response = apiService.getNoticias() // Usamos la llamada síncrona o suspendida

                if (response.isSuccessful && response.body() != null) {
                    val noticias = response.body()!!

                    if (noticias.isNotEmpty()) {
                        // Tomamos la noticia más reciente (la primera de la lista)
                        val ultimaNoticia = noticias.first()
                        val ultimoIdConocido = sessionManager.getLastNoticiaId()

                        // 3. ¿Es nueva? (Su ID es mayor al que teníamos guardado)
                        if (ultimaNoticia.id > ultimoIdConocido) {

                            // ¡ES NUEVA! Lanzamos la alerta
                            mostrarNotificacion(context, ultimaNoticia.titulo, ultimaNoticia.contenido)

                            // Guardamos este ID para no repetir la alerta
                            sessionManager.saveLastNoticiaId(ultimaNoticia.id)
                        }
                    }
                }

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry() // Si falla (ej: sin internet), intenta más tarde
            }
        }
    }

    private fun mostrarNotificacion(context: Context, titulo: String, contenido: String) {
        val canalId = "noticias_vecinos"

        // 1. Crear el Canal de Notificaciones (Obligatorio en Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                canalId,
                "Noticias Vecinales",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos importantes de la junta de vecinos"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Definir qué pasa al tocar la notificación (Abrir la App)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Construir la notificación
        val builder = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener un icono válido
            .setContentTitle("📢 $titulo")
            .setContentText(contenido)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 4. Mostrarla (Verificando permiso básico, aunque en Worker suele pasar)
        try {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: SecurityException) {
            // Si falta permiso en Android 13, no crashar
            e.printStackTrace()
        }
    }
}