package cl.duoc.sut_backend.services

import cl.duoc.sut_backend.models.Usuario
import org.openpdf.text.*
import org.openpdf.text.pdf.PdfWriter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter
import java.time.LocalDate

@Service
class PdfService {

    fun generarCertificadoResidencia(usuario: Usuario): ByteArray {
        //crear el documento
        val documento = Document(PageSize.LETTER)
        val out = ByteArrayOutputStream()

        try {
            PdfWriter.getInstance(documento,out)
            documento.open()

            //añadir fuentes de letra
            val fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f, Font.UNDERLINE)
            val fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 12F)
            val fuenteNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD,12F)

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

            

        }        }

    }