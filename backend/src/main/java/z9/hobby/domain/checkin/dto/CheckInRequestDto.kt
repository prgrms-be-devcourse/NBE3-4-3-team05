package z9.hobby.domain.checkin.dto

import jakarta.validation.constraints.NotNull

data class CheckInRequestDto(
    @field:NotNull(message = "Schedule ID must not be null")
    val scheduleId: Long? = null,

    @field:NotNull(message = "Check-in status must not be null")
    val checkIn: Boolean? = null
)
