package z9.hobby.domain.kakao;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakao-resource", url = "https://kapi.kakao.com")
public interface KakaoResourceFeignClient {

    @GetMapping("/v2/user/me")
    Map<String, Object> getUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestHeader("Content-Type") String contentType);
}
