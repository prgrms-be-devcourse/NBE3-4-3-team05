package z9.hobby.domain.checkin.dto

import z9.hobby.model.checkIn.CheckInEntity

data class CheckInResponseDto(
    val checkInId: Long,
    val scheduleId: Long,
    val userId: Long,
    val checkIn: Boolean,
    val isCheckIn: Boolean
) {
    companion object {
        fun from(checkInEntity: CheckInEntity): CheckInResponseDto {
            return CheckInResponseDto(
                checkInId = checkInEntity.id ?: throw IllegalArgumentException("ID cannot be null"),
                scheduleId = checkInEntity.schedules?.id ?: throw IllegalArgumentException("Schedule ID cannot be null"),
                userId = checkInEntity.userId,
                checkIn = checkInEntity.checkIn,
                isCheckIn = true
            )
        }
    }
}
