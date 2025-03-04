package z9.hobby.global.security.jwt

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import z9.hobby.global.security.constant.JWTConstant.CLAIM_KEY_USER_CATEGORY
import z9.hobby.global.security.constant.JWTConstant.CLAIM_KEY_USER_ID
import z9.hobby.global.security.constant.JWTConstant.CLAIM_KEY_USER_ROLE
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JWTUtil(
    @Value("\${jwt.secret}") private val secret: String
) {

    private val secretKey: SecretKey = SecretKeySpec(
        secret.toByteArray(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().algorithm
    )

    fun getUserId(token: String): String {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).payload[CLAIM_KEY_USER_ID, String::class.java]
    }

    fun getRole(token: String): String {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).payload[CLAIM_KEY_USER_ROLE, String::class.java]
    }

    fun getCategory(token: String): String {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).payload[CLAIM_KEY_USER_CATEGORY, String::class.java]
    }

    fun isExpired(token: String): Boolean {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).payload.expiration.before(Date())
    }

    fun createJwt(category: String, userId: String, role: String, expiredMs: Long): String {
        return Jwts.builder()
            .claim(CLAIM_KEY_USER_CATEGORY, category)
            .claim(CLAIM_KEY_USER_ID, userId)
            .claim(CLAIM_KEY_USER_ROLE, role)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact()
    }
}