package z9.hobby.domain.schedules.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class SchedulesRequestDto {
    data class CreateRequest(
        @field:NotNull(message = "Class ID must not be null")
        val classId: Long? = null, // 모임의 ID

        @field:NotNull(message = "meeting_time must not be null")
        @field:Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "날짜 형식은 yyyy-MM-dd 이어야 합니다."
        )
        val meetingTime: String? = null,

        @field:NotNull(message = "meeting_title must not be null")
        @field:Size(
            min = 2,
            max = 100,
            message = "모임 제목은 2글자 이상 100자 이하이어야 합니다."
        )
        @field:Pattern(
            regexp = "^[\\p{L}\\p{N}\\s,.!?()-]+$",
            message = "모임 제목에는 문자, 숫자, 기본 특수문자만 사용할 수 있습니다"
        )
        val meetingTitle: String? = null
    )

    data class UpdateRequest(
        // classId 제거 - PathVariable로 받기 때문
        @field:NotNull(message = "meeting_time must not be null")
        @field:Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "날짜 형식은 yyyy-MM-dd 이어야 합니다."
        )
        val meetingTime: String? = null,

        @field:NotNull(message = "meeting_title must not be null")
        @field:Size(
            min = 2,
            max = 100,
            message = "모임 제목은 2글자 이상 100자 이하이어야 합니다."
        )
        @field:Pattern(
            regexp = "^[\\p{L}\\p{N}\\s,.!?()-]+$",
            message = "모임 제목에는 문자, 숫자, 기본 특수문자만 사용할 수 있습니다"
        )
        val meetingTitle: String? = null
    )
}
