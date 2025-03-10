package z9.hobby.domain.kakaoMap.service;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.kakaoMap.dto.KakaoMapDto
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.model.schedules.SchedulesEntity
import z9.hobby.model.schedules.SchedulesRepository

@Service
class KakaoMapService(
    private val schedulesRepository: SchedulesRepository
) {
    @Transactional(readOnly = true)
    fun getLatLngInfo(
        inputFilterType: String?,
        bottomLeftLat: Double,
        bottomLeftLng: Double,
        topRightLat: Double,
        topRightLng: Double,
        userId: Long?
    ): List<KakaoMapDto.SchedulesLocationData> {
        val effectiveFilterType = inputFilterType ?: if (userId != null) "FAVORITE" else "ALL"

        val locationData: List<SchedulesEntity> = if (effectiveFilterType == "FAVORITE" && userId != null) {
            schedulesRepository.findFavoriteSchedulesByUserId(
                userId,
                bottomLeftLat,
                bottomLeftLng,
                topRightLat,
                topRightLng
            )
        } else {
            schedulesRepository.findByLatLng(bottomLeftLat, bottomLeftLng, topRightLat, topRightLng)
        }

        if (locationData.isEmpty()) {
            throw CustomException(ErrorCode.SCHEDULE_NOT_FOUND)
        }

        return locationData.map { schedule ->
            KakaoMapDto.SchedulesLocationData.from(schedule.getClasses(), schedule)
        }
    }
}