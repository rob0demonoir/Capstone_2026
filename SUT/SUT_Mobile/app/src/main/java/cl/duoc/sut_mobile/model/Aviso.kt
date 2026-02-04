package cl.duoc.sut_mobile.model

// Este modelo debe calzar EXACTO con el JSON que envía AvisoController
data class Aviso(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val precio: Int?,
    val tipo: TipoAviso, // Asegúrate de tener el Enum creado en Android
    val fechaPublicacion: String, // Retrofit lo recibe como String

    val nombrePublicador: String,
    val telefonoContacto: String,
    val urlImagen: String?,

    val esMio: Boolean // ¡Esto nos servirá mucho en la UI!
)

enum class TipoAviso {
    VENTA,
    SERVICIO,
    EVENTO,
    BUSCO
}

