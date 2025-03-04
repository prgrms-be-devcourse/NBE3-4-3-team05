package z9.hobby.global.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_CATEGORY
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_HEADER
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_PREFIX
import z9.hobby.global.security.jwt.JWTUtil
import z9.hobby.global.security.user.CustomUserDetails
import z9.hobby.model.user.User
import z9.hobby.model.user.UserRole
import java.io.IOException

@Component
class AuthenticationFilter(
    private val jwtUtil: JWTUtil,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1. AccessToken 추출
        var accessToken = FilterUtil.extractAccessToken(request)

        // 2. AccessToken 유무 확인
        if (accessToken == null) {
            filterChain.doFilter(request, response)
            return
        }

        // 2-2. 만약 response 에 header 값이 있다면 token 이 재발급 된 것.
        // 해당 token 으로 인증 진행할 것
        val newAccessToken = response.getHeader(ACCESS_TOKEN_HEADER)
        if (newAccessToken != null && newAccessToken.startsWith(ACCESS_TOKEN_PREFIX)) {
            accessToken = newAccessToken.substring(7)
        }

        // 3. AccessToken 검증2
        try {
            validateAccessToken(accessToken)
        } catch (e: JwtException) {
            FilterUtil.handleJwtException(response, e, objectMapper)
            return
        }

        // 4. 토큰으로 회원 인증 진행.
        setAuthentication(accessToken)

        filterChain.doFilter(request, response)
    }

    /**
     * 토큰 유효성 검증
     * @param accessToken
     * @throws JwtException
     */
    @Throws(JwtException::class)
    private fun validateAccessToken(accessToken: String) {
        // 1. expired 확인
        jwtUtil.isExpired(accessToken)

        // 2. category 확인
        val category = jwtUtil.getCategory(accessToken)
        if (ACCESS_TOKEN_CATEGORY != category) {
            throw JwtException("Invalid access token category")
        }
    }

    private fun setAuthentication(accessToken: String) {
        val userId = jwtUtil.getUserId(accessToken).toLong()
        val role = jwtUtil.getRole(accessToken)

        val authUser = User.createSecurityContextUser(userId, UserRole.valueOf(role))
        val customUserDetails = CustomUserDetails(authUser)
        val authToken: Authentication = UsernamePasswordAuthenticationToken(
            customUserDetails, null, customUserDetails.authorities
        )

        SecurityContextHolder.getContext().authentication = authToken
    }
}
