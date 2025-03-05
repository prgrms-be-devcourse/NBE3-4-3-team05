package z9.hobby.domain.authentication.dto

import jakarta.validation.constraints.Size
import z9.hobby.global.annotation.validation.user.UserLoginId
import z9.hobby.global.annotation.validation.user.UserNickname
import z9.hobby.global.annotation.validation.user.UserPassword

class AuthenticationRequest {

    data class Login(
        @UserLoginId
        val loginId: String,

        @UserPassword
        val password: String
    )

    data class Signup(
        @UserLoginId
        val loginId: String,

        @UserPassword
        val password: String,

        @field:Size(min = 1, message = "관심사는 하나 이상 등록되어야 합니다.")
        val favorite: List<String>,

        @UserNickname
        val nickname: String
    )
}
