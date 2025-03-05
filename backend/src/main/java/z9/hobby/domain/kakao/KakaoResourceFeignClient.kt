package z9.hobby.domain.kakao

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "kakao-resource", url = "https://kapi.kakao.com")
interface KakaoResourceFeignClient {

    @GetMapping("/v2/user/me")
    fun getUserInfo(
        @RequestHeader("Authorization") token: String,
        @RequestHeader("Content-Type") contentType: String
    ): Map<String, Any>
}