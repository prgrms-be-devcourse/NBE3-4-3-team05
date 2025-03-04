package z9.hobby.global.response

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus

data class BaseResponse<T>(
    @get:JsonIgnore val httpStatus: HttpStatus,
    val isSuccess: Boolean,
    val message: String,
    val code: String,
    val data: T?
) {

    companion object {
        // 성공 응답 생성 팩토리 메서드
        fun <T> ok(code: SuccessCode, data: T): BaseResponse<T> {
            return BaseResponse(
                code.httpStatus,
                code.isSuccess,
                code.message,
                code.code,
                data
            )
        }

        // 성공 응답 생성 팩토리 메서드 (매개변수 없이)
        fun ok(): BaseResponse<Nothing> {
            return BaseResponse(
                SuccessCode.SUCCESS.httpStatus,
                SuccessCode.SUCCESS.isSuccess,
                SuccessCode.SUCCESS.message,
                SuccessCode.SUCCESS.code,
                null
            )
        }

        // 특정 SuccessCode로 성공 응답 생성
        fun ok(code: SuccessCode): BaseResponse<Nothing> {
            return BaseResponse(
                code.httpStatus,
                code.isSuccess,
                code.message,
                code.code,
                null
            )
        }

        // 실패 응답 생성 팩토리 메서드
        fun fail(code: ErrorCode): BaseResponse<Nothing> {
            return BaseResponse(
                code.httpStatus,
                code.isSuccess,
                code.message,
                code.code,
                null
            )
        }

        // 실패 응답 생성 팩토리 메서드 (메시지 추가)
        fun fail(code: ErrorCode, message: String): BaseResponse<Nothing> {
            return BaseResponse(
                code.httpStatus,
                code.isSuccess,
                message,
                code.code,
                null
            )
        }
    }
}
