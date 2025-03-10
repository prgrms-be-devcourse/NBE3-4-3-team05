package z9.hobby.domain.schedules.dto

import z9.hobby.model.schedules.SchedulesEntity

class SchedulesResponseDto {
    data class ResponseData(
        val scheduleId: Long? = null, // 생성된 일정의 ID
        val meetingTime: String? = null, // 모임 시간
        val meetingTitle: String? = null, // 모임 제목
        val meetingPlace: String? = null, // 모임 장소
        val latitude: Double? = null, // 모임 위치 경도, 위도
        val longitude: Double? = null
    ) {
        companion object {
            @JvmStatic
            fun from(schedulesEntity: SchedulesEntity): ResponseData {
                return ResponseData(
                    scheduleId = schedulesEntity.getId(),
                    meetingTime = schedulesEntity.getMeetingTime(),
                    meetingTitle = schedulesEntity.getMeetingTitle(),
                    meetingPlace = schedulesEntity.getMeetingPlace(),
                    latitude = schedulesEntity.getLatitude(),
                    longitude = schedulesEntity.getLongitude()
                )
            }
        }
    }
}
