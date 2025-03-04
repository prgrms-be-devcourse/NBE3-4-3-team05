package z9.hobby.global.security.oauth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "oauth.kakao")
class OAuthProperties {
    var clientId: String = ""
    var redirectUri: String = ""
    var clientSecret: String = ""
    var contentType: String = ""
}
