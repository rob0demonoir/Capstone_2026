package cl.duoc.sut_backend.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService {

    private val SECRETKEY = "llave67secreta67ultra67hiper67mega67seguraxD676767creador67"
    //ESTA LLAVE SECRETA FUE CREADA CON AYUDA DE MI HIJITO, QUE EN ROBLOX SE LLAMA leong4m3r_exe <3

    fun generarToken(email: String): String{
        return Jwts.builder()
            .setSubject(email)//el usuario deberá loguearse con email y contraseña
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis()*1000*60*60*24))
            .signWith(obtenerFirma(), SignatureAlgorithm.HS256)
            .compact()
    }

    private fun obtenerFirma(): SecretKey{
        val llavecita = Decoders.BASE64.decode(SECRETKEY)
        return Keys.hmacShaKeyFor(llavecita)
    }

    fun extraerEmail(token: String): String{
        return extraerClaim(token) {claims -> claims.subject}
    }

    fun <T> extraerClaim(token: String, claimsResolver: (Claims) -> T): T{
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    fun validarToken(token: String, detalleUsuario: org.springframework.security.core.userdetails.UserDetails): Boolean {
        val email = extraerEmail(token)
        return (email == detalleUsuario.username && !tokenExpirado(token))
    }

    private fun tokenExpirado(token: String): Boolean {
        return extraerExpiracion(token).before(Date())
    }

    private fun extraerExpiracion(token: String): Date {
        return extraerClaim(token) {claims -> claims.expiration}
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(obtenerFirma())
            .build()
            .parseClaimsJws(token)
            .body
    }


}

