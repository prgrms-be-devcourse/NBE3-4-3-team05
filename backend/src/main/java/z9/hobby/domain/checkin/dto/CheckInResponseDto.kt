package z9.hobby.domain.checkin.dto

import z9.hobby.model.checkIn.CheckInEntity

data class CheckInResponseDto(
    val checkInId: Long? = null,
    val scheduleId: Long? = null,
    val userId: Long? = null,
    val checkIn: Boolean? = null,
    val isCheckIn: Boolean? = null
) {
    companion object {
        fun from(checkInEntity: CheckInEntity): CheckInResponseDto {
            return CheckInResponseDto(
                checkInId = checkInEntity.id,
                scheduleId = checkInEntity.schedules.id,
                userId = checkInEntity.userId,
                checkIn = checkInEntity.isCheckIn,
                isCheckIn = true
            )
        }
    }
}
