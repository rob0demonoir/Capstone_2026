package cl.duoc.sut_mobile.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import cl.duoc.sut_mobile.utils.SessionManager


/**object RetrofitClient {

    //private const val BASE_URL = "http://103.67.45.103:8080"
    private const val BASE_URL = "http://192.168.100.19:8082"

    private val logging = HttpLoggingInterceptor().apply{
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
} **/

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getToken()

        // CAMBIO CLAVE: Verificamos que no sea nulo Y que no esté vacío/blanco
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}

// En tu objeto RetrofitClient, necesitamos pasarle el contexto para acceder al SessionManager
class RetrofitClient(context: Context) {

    private val sessionManager = SessionManager(context)

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .addInterceptor(AuthInterceptor(sessionManager)) // <--- AQUÍ ESTÁ LA CLAVE
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.100.19:8082/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}