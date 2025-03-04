package z9.hobby.global.exception

import z9.hobby.global.response.ErrorCode

class CustomException(
    val code: ErrorCode
) : RuntimeException()
