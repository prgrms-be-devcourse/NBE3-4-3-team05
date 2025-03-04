package z9.hobby.domain.checkin.service

import z9.hobby.domain.checkin.dto.CheckInRequestDto
import z9.hobby.domain.checkin.dto.CheckInResponseDto

interface CheckInService {
    fun createCheckIn(userId: Long, requestDto: CheckInRequestDto.CheckInDto)
    fun updateCheckIn(userId: Long, requestDto: CheckInRequestDto.CheckInDto)
    fun getAllCheckIns(scheduleId: Long): List<CheckInResponseDto.ResponseData>?
    fun getMyCheckIn(scheduleId: Long, userId: Long): CheckInResponseDto.ResponseData
}

