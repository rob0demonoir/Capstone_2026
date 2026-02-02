package cl.duoc.sut_backend.services

import cl.duoc.sut_backend.models.Usuario
import org.openpdf.text.*
import org.openpdf.text.pdf.PdfWriter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter
import java.time.LocalDate
//importé esta librería porque el certificado quedaba con el mes de la fecha en inglés jaja.
import java.util.Locale

@Service
class PdfService {

    fun generarCertificadoResidencia(usuario: Usuario): ByteArray {
        //crear el documento
        val documento = Document(PageSize.LETTER)
        val out = ByteArrayOutputStream()

        try {
            PdfWriter.getInstance(documento, out)
            documento.open()

            //añadir fuentes de letra
            val fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f, Font.UNDERLINE)
            val fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 12F)
            val fuenteNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12F)

            //insertar el titulo
            val titulo = Paragraph("CERTIFICADO DE RESIDENCIA", fuenteTitulo)
            titulo.alignment = Element.ALIGN_CENTER
            titulo.spacingAfter = 50f
            documento.add(titulo)

            val parrafo = Paragraph().apply {
                alignment = Element.ALIGN_JUSTIFIED
                spacingAfter = 20f
                leading = 25f
            }

            parrafo.add(Chunk("La Administración de la Junta de Vecinos certifica que el/la Sr./Sra.", fuenteCuerpo))
            parrafo.add(Chunk("${usuario.nombre} ${usuario.apellido}", fuenteNegrita))
            parrafo.add(Chunk(", RUT número ", fuenteCuerpo))
            parrafo.add(Chunk(usuario.rut ?: "no informado", fuenteNegrita))
            parrafo.add(
                Chunk(
                    ", figura en nuestros registros como un residente activo de nuestra comunidad.",
                    fuenteCuerpo
                )
            )
            documento.add(parrafo)

            val pieDePagina = Paragraph(
                "Este documento se extiende a solicitud del interesado para los fines que estime conveniente.",
                fuenteCuerpo
            )
            pieDePagina.alignment = Element.ALIGN_JUSTIFIED
            pieDePagina.spacingAfter = 80f
            documento.add(pieDePagina)

            //datos del certificado
            val fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("es","ES")))
            val firmaCertificado =
                Paragraph("Emitido el $fechaActual\n\n\n__________________________\nFirma Administración", fuenteCuerpo)
            firmaCertificado.alignment = Element.ALIGN_CENTER
            documento.add(firmaCertificado)

            documento.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return out.toByteArray()

    }

}