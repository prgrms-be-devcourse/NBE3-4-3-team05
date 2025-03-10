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
        val meetingTitle: String? = null,

        @field:NotNull(message = "meeting_place must not be null")
        @field:Size(
            min = 1,
            max = 100,
            message = "모임 장소는 1글자 이상 100자 이하이어야 합니다."
        )
        @field:Pattern(
            regexp = "^[\\p{L}\\p{N}\\s,.!?()-]+$",
            message = "모임 장소에는 문자, 숫자, 기본 특수문자만 사용할 수 있습니다"
        )
        val meetingPlace: String? = null,

        @field:NotNull(message = "latitude must not be null")
        val lat: Double? = null,

        @field:NotNull(message = "longitude must not be null")
        val lng: Double? = null
    ) {
        companion object {
            @JvmStatic
            fun builder() = Builder()
        }

        class Builder {
            private var classId: Long? = null
            private var meetingTime: String? = null
            private var meetingTitle: String? = null
            private var meetingPlace: String? = null
            private var lat: Double? = null
            private var lng: Double? = null

            fun classId(classId: Long?) = apply { this.classId = classId }
            fun meetingTime(meetingTime: String?) = apply { this.meetingTime = meetingTime }
            fun meetingTitle(meetingTitle: String?) = apply { this.meetingTitle = meetingTitle }
            fun meetingPlace(meetingPlace: String?) = apply { this.meetingPlace = meetingPlace }
            fun lat(lat: Double?) = apply { this.lat = lat }
            fun lng(lng: Double?) = apply { this.lng = lng }

            fun build() = CreateRequest(
                classId = classId,
                meetingTime = meetingTime,
                meetingTitle = meetingTitle,
                meetingPlace = meetingPlace,
                lat = lat,
                lng = lng
            )
        }
    }

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
        val meetingTitle: String? = null,

        @field:NotNull(message = "meeting_place must not be null")
        @field:Size(
            min = 1,
            max = 100,
            message = "모임 장소는 1글자 이상 100자 이하이어야 합니다."
        )
        @field:Pattern(
            regexp = "^[\\p{L}\\p{N}\\s,.!?()-]+$",
            message = "모임 장소에는 문자, 숫자, 기본 특수문자만 사용할 수 있습니다"
        )
        val meetingPlace: String? = null,

        @field:NotNull(message = "latitude must not be null")
        val lat: Double? = null,

        @field:NotNull(message = "longitude must not be null")
        val lng: Double? = null
    ) {
        companion object {
            @JvmStatic
            fun builder() = Builder()
        }

        class Builder {
            private var meetingTime: String? = null
            private var meetingTitle: String? = null
            private var meetingPlace: String? = null
            private var lat: Double? = null
            private var lng: Double? = null

            fun meetingTime(meetingTime: String?) = apply { this.meetingTime = meetingTime }
            fun meetingTitle(meetingTitle: String?) = apply { this.meetingTitle = meetingTitle }
            fun meetingPlace(meetingPlace: String?) = apply { this.meetingPlace = meetingPlace }
            fun lat(lat: Double?) = apply { this.lat = lat }
            fun lng(lng: Double?) = apply { this.lng = lng }

            fun build() = UpdateRequest(
                meetingTime = meetingTime,
                meetingTitle = meetingTitle,
                meetingPlace = meetingPlace,
                lat = lat,
                lng = lng
            )
        }
    }
}
