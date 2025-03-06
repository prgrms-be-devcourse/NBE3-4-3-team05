package z9.hobby.domain.kakao

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import z9.hobby.global.security.oauth.OAuthToken

@FeignClient(name = "kakao-auth", url = "https://kauth.kakao.com")
interface KakaoAuthFeignClient {

    @PostMapping("/oauth/token")
    fun getKakaoToken(
        @RequestParam("grant_type") grantType: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("redirect_uri") redirectUri: String,
        @RequestParam("code") code: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestHeader("Content-Type") contentType: String
    ): OAuthToken
}