package z9.hobby.domain.classes.dto;

import jakarta.validation.constraints.Size

class ClassRequest {
    data class ClassRequestData(
        @field:Size(min = 3, message = "제목은 3글자 이상이어야 합니다.")
        val name: String,

        @field:Size(min = 1, message = "관심사는 1가지를 입력하셔야 합니다.")
        val favorite: String,

        @field:Size(min = 10, message = "내용은 10글자 이상이어야 합니다.")
        val description: String
    ) {
        companion object {
            @JvmStatic
            fun of(name: String, favorite: String, description: String): ClassRequestData {
                return ClassRequestData(
                    name = name,
                    favorite = favorite,
                    description = description
                )
            }
        }
    }

    data class ModifyRequestData(
        @field:Size(min = 3, message = "제목은 3글자 이상이어야 합니다.")
        val name: String,

        @field:Size(min = 10, message = "내용은 10글자 이상이어야 합니다.")
        val description: String
    ) {
        companion object {
            @JvmStatic
            fun of(name: String, description: String): ModifyRequestData {
                return ModifyRequestData(
                    name = name,
                    description = description
                )
            }
        }
    }
}
