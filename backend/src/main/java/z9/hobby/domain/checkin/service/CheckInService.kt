package z9.hobby.domain.checkin.service

import z9.hobby.domain.checkin.dto.CheckInRequestDto
import z9.hobby.domain.checkin.dto.CheckInResponseDto

interface CheckInService {
    fun createCheckIn(userId: Long, requestDto: CheckInRequestDto)
    fun updateCheckIn(userId: Long, requestDto: CheckInRequestDto)
    fun checkInProcess(userId: Long, requestDto: CheckInRequestDto)
    fun getAllCheckIns(scheduleId: Long): List<CheckInResponseDto>
    fun getMyCheckIn(scheduleId: Long, userId: Long): CheckInResponseDto
}
