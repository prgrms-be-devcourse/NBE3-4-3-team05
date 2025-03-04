package z9.hobby.domain.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import z9.hobby.global.security.oauth.OAuthToken;

@FeignClient(name = "kakao-auth", url = "https://kauth.kakao.com")
public interface KakaoAuthFeignClient {

    @PostMapping("/oauth/token")
    OAuthToken getKakaoToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code,
            @RequestParam("client_secret") String clientSecret,
            @RequestHeader("Content-Type") String contentType);
}
