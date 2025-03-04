package z9.hobby.global.security.entrypoint

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.ErrorCode
import java.io.IOException

@Component
class CustomAccessDeniedEntryPoint(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {

    @Throws(IOException::class, ServletException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val errorResponse = BaseResponse.fail(ErrorCode.FORBIDDEN)
        response.contentType = "application/json"
        response.status = ErrorCode.FORBIDDEN.httpStatus.value()
        response.characterEncoding = "utf-8"
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
