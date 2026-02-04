package cl.duoc.sut_mobile.model

data class CrearAvisoRequest(
    val titulo: String,
    val descripcion: String,
    val tipo: TipoAviso,
    val precio: Int?,
    val urlImagen: String? = null
)