package z9.hobby.global.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.ErrorCode
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_HEADER
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_PREFIX
import z9.hobby.global.security.constant.JWTConstant.REFRESH_TOKEN_HEADER

object FilterUtil {
    fun extractAccessToken(request: HttpServletRequest): String? {
        val accessToken = request.getHeader(ACCESS_TOKEN_HEADER)
        return if (accessToken != null && accessToken.startsWith(ACCESS_TOKEN_PREFIX)) {
            accessToken.substring(7)
        } else null
    }

    fun extractRefreshToken(request: HttpServletRequest): String? {
        return request.cookies?.firstOrNull { it.name == REFRESH_TOKEN_HEADER }?.value
    }

    fun handleJwtException(response: HttpServletResponse, e: JwtException, objectMapper: ObjectMapper) {
        val errorCode = if (e is ExpiredJwtException) ErrorCode.TOKEN_EXPIRED else ErrorCode.INVALID_TOKEN
        val message = e.message ?: errorCode.message
        writeErrorResponse(response, errorCode, objectMapper, message)
    }

    private fun writeErrorResponse(
        response: HttpServletResponse,
        errorCode: ErrorCode,
        objectMapper: ObjectMapper,
        message: String
    ) {
        val errorResponse = BaseResponse.fail(errorCode, message)
        response.contentType = "application/json"
        response.status = errorCode.httpStatus.value()
        response.characterEncoding = "utf-8"
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
