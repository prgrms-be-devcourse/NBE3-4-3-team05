package z9.hobby.global.utils;

import static z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_PREFIX;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public abstract class ControllerUtils {

    public static void addHeaderResponse(String name, String value, HttpServletResponse response) {
        response.addHeader(name, value);
    }

    /**
     * Cookie 추가 메서드
     * 기본 httpOnly, secure 설정
     * maxAge = sec 단위
     */
    public static void addCookieResponse(String name, String value, int maxAgeSec, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSec);
        response.addCookie(cookie);
    }

    public static int parseMsToSec(Long ms) {
        return (int) (ms / 1000);
    }

    public static String makeBearerToken(String refreshToken) {
        return String.format("%s %s", ACCESS_TOKEN_PREFIX, refreshToken);
    }
}
