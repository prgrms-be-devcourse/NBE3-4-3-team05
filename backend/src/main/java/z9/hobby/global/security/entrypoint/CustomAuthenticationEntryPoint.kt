package z9.hobby.global.security.entrypoint

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.ErrorCode
import java.io.IOException

/**
 * 인증 오류 handler
 * token 으로 인증 없이, 인증이 필요한 endpoint 에 접근 시, 해당 entryPoint 로 공통 response 생성
 */
@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val errorResponse = BaseResponse.fail(ErrorCode.NEED_LOGIN)
        response.contentType = "application/json"
        response.status = ErrorCode.NEED_LOGIN.httpStatus.value()
        response.characterEncoding = "utf-8"
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
