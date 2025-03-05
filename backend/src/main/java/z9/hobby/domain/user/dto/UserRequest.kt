package z9.hobby.domain.user.dto

import jakarta.validation.constraints.Size
import z9.hobby.global.annotation.validation.user.UserNickname

class UserRequest {
    data class PatchUserInfo (
        @UserNickname
        val nickname: String,

        @field:Size(min = 1, message = "관심사는 하나 이상 등록되어야 합니다.")
        val favorite: List<String>
    )
}