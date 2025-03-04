package z9.hobby.global.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import z9.hobby.global.redis.RedisRepository
import z9.hobby.global.security.jwt.JWTUtil
import z9.hobby.global.security.jwt.JwtProperties
import z9.hobby.global.utils.ControllerUtils
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_CATEGORY
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_HEADER
import z9.hobby.global.security.constant.JWTConstant.REFRESH_TOKEN_CATEGORY
import z9.hobby.global.security.constant.JWTConstant.REFRESH_TOKEN_HEADER
import java.io.IOException

/**
 * AccessToken 이 expired 될 경우, RefreshToken 으로 재발급을 자동 진행해 줍니다.
 * 재발급 범위 : RefreshToken, AccessToken
 */
@Component
class ReissueFilter(
    private val jwtUtil: JWTUtil,
    private val objectMapper: ObjectMapper,
    private val redisRepository: RedisRepository,
    private val jwtProperties: JwtProperties
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // 1. AccessToken 확인
        // 토큰이 없거나 (인증이 필요하지 않은 api 예상), 만료되지 않았다면 재발급 할 필요 없어, 넘어감
        val accessToken = FilterUtil.extractAccessToken(request)
        if (accessToken == null || !isExpired(accessToken)) {
            filterChain.doFilter(request, response)
            return
        }

        // 2. RefreshToken 추출
        val refreshToken = FilterUtil.extractRefreshToken(request)

        // 3. RefreshToken 유무 검증 및 expired 검증
        // 토큰이 없거나, 토큰이 만료되었다면 재발급 불가로 다음 필터로 넘어감
        if (refreshToken == null || isExpired(refreshToken)) {
            filterChain.doFilter(request, response)
            return
        }

        // 4. 저장된 RefreshToken 가져오기
        val userId = jwtUtil.getUserId(refreshToken)
        val role = jwtUtil.getRole(refreshToken)
        val savedRefreshToken = redisRepository.getRefreshToken(userId)

        // 5. RefreshToken 유효성 검증
        try {
            validateRefreshToken(refreshToken, savedRefreshToken.toString())
        } catch (e: JwtException) {
            FilterUtil.handleJwtException(response, e, objectMapper)
            return
        }

        // 6. 새로운 Access Token 및 Refresh Token 발급
        val newAccessToken = jwtUtil.createJwt(ACCESS_TOKEN_CATEGORY, userId, role, jwtProperties.accessExpiration)
        val newRefreshToken = jwtUtil.createJwt(REFRESH_TOKEN_CATEGORY, userId, role, jwtProperties.refreshExpiration)
        redisRepository.deleteRefreshToken(userId)
        redisRepository.saveRefreshToken(userId, newRefreshToken, jwtProperties.refreshExpiration)

        // 7. 해당 api 요청 응답에 새로운 토큰 값 저장
        ControllerUtils.addHeaderResponse(
            ACCESS_TOKEN_HEADER,
            ControllerUtils.makeBearerToken(newAccessToken),
            response
        )
        ControllerUtils.addCookieResponse(
            REFRESH_TOKEN_HEADER,
            newRefreshToken,
            ControllerUtils.parseMsToSec(jwtProperties.refreshExpiration),
            response
        )

        filterChain.doFilter(request, response)
    }

    private fun validateRefreshToken(refreshToken: String, savedRefreshToken: String) {
        // 1. refresh Token Category 검증
        val category = jwtUtil.getCategory(refreshToken)
        if (REFRESH_TOKEN_CATEGORY != category) {
            throw JwtException("Invalid refresh token category")
        }

        // 2. 저장된 refreshToken 과 동일한지 확인
        if (refreshToken != savedRefreshToken) {
            throw JwtException("Invalid refresh token")
        }
    }

    private fun isExpired(token: String): Boolean {
        return try {
            jwtUtil.isExpired(token)
            false
        } catch (e: ExpiredJwtException) {
            true
        }
    }
}
