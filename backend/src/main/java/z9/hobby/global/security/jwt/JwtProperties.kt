package z9.hobby.global.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt.token")
class JwtProperties {
    var accessExpiration: Long = 0L
    var refreshExpiration: Long = 0L
}
