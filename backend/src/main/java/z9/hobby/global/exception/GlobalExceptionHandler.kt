package z9.hobby.global.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.ObjectError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.ErrorCode
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 사용자 설정 Exception 발생
     */
    @ExceptionHandler(CustomException::class)
    fun customException(e: CustomException): ResponseEntity<BaseResponse<Nothing>> {
        val response = BaseResponse.fail(e.code)
        return ResponseEntity.status(response.httpStatus).body(response)
    }

    /**
     * Request Dto 필드 유효성 검증 실패
     */
    @ExceptionHandler(BindException::class)
    fun validationException(e: BindException): ResponseEntity<BaseResponse<Nothing>> {
        val allErrors: List<ObjectError> = e.bindingResult.allErrors
        val message = StringBuilder()
        for (allError in allErrors) {
            if (message.isNotEmpty()) {
                message.append("\n")
            }
            message.append(allError.defaultMessage)
        }

        val response = BaseResponse(
            ErrorCode.VALIDATION_FAIL_ERROR.httpStatus,
            ErrorCode.VALIDATION_FAIL_ERROR.isSuccess,
            message.toString(),
            ErrorCode.VALIDATION_FAIL_ERROR.code,
            null
        )

        return ResponseEntity(response, response.httpStatus)
    }

    /**
     * Http Method 가 지원되지 않는 Method
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun httpRequestMethodNotSupported(e: HttpRequestMethodNotSupportedException): ResponseEntity<BaseResponse<Nothing>> {
        val response = BaseResponse(
            ErrorCode.NOT_SUPPORTED_METHOD.httpStatus,
            ErrorCode.NOT_SUPPORTED_METHOD.isSuccess,
            e.message ?: "",
            ErrorCode.NOT_SUPPORTED_METHOD.code,
            null
        )
        return ResponseEntity(response, ErrorCode.NOT_SUPPORTED_METHOD.httpStatus)
    }

    /**
     * 404
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun noResourceFoundException(e: NoResourceFoundException): ResponseEntity<BaseResponse<Nothing>> {
        val response = BaseResponse(
            ErrorCode.NOT_FOUND_URL.httpStatus,
            ErrorCode.NOT_FOUND_URL.isSuccess,
            ErrorCode.NOT_FOUND_URL.message,
            ErrorCode.NOT_FOUND_URL.code,
            null
        )
        return ResponseEntity(response, ErrorCode.NOT_FOUND_URL.httpStatus)
    }

    /**
     * 핸들링 되지 않은 Exception 발생
     * 로깅 처리 및 오류 발생 응답
     */
    @ExceptionHandler(Exception::class)
    fun notHandledException(e: Exception): ResponseEntity<BaseResponse<Nothing>> {
        val response = BaseResponse(
            ErrorCode.FAIL.httpStatus,
            ErrorCode.FAIL.isSuccess,
            ErrorCode.FAIL.message,
            ErrorCode.FAIL.code,
            null
        )

        log.error(e.stackTraceToString())
        return ResponseEntity(response, ErrorCode.FAIL.httpStatus)
    }

    /**
     * 저장 / 업데이트 등, 실행 시 데이터 무결성 검증 실패로 인한 오류 발생
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun dataIntegrityViolation(e: DataIntegrityViolationException): ResponseEntity<BaseResponse<Nothing>> {
        val response = BaseResponse(
            ErrorCode.INVALID_REQUEST_DATA.httpStatus,
            ErrorCode.INVALID_REQUEST_DATA.isSuccess,
            ErrorCode.INVALID_REQUEST_DATA.message,
            ErrorCode.INVALID_REQUEST_DATA.code,
            null
        )
        return ResponseEntity(response, ErrorCode.INVALID_REQUEST_DATA.httpStatus)
    }
}
