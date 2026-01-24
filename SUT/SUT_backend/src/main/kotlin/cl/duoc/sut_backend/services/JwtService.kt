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

    private val SECRET_KEY = "llave67secreta67ultra67hiper67mega67seguraxD676767creador67"
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
        val llavecita = Decoders.BASE64.decode(SECRET_KEY)
        return Keys.hmacShaKeyFor(llavecita)
    }

    fun extraerEmail(token: String): String{
        return extractClaim(token) {claims -> claims.subject}
    }

    fun <T> extractClaim(token: String, claimsResolver: (io.jsonwebtoken.Claims) -> T): T{
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    fun 

}

