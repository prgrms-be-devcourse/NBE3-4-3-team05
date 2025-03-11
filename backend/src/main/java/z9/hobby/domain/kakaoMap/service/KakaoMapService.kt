package z9.hobby.domain.kakaoMap.service;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.kakaoMap.dto.KakaoMapDto
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.model.schedules.SchedulesEntity
import z9.hobby.model.schedules.SchedulesRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class KakaoMapService(
    private val schedulesRepository: SchedulesRepository
) {
    @Transactional(readOnly = true)
    fun getLatLngInfo(
        inputFilterType: String?,
        dataRange: String?,
        bottomLeftLat: Double,
        bottomLeftLng: Double,
        topRightLat: Double,
        topRightLng: Double,
        userId: Long?
    ): List<KakaoMapDto.SchedulesLocationData> {
        val effectiveFilterType = inputFilterType ?: if (userId != null) "FAVORITE" else "ALL"
        val effectiveDataRange = dataRange ?: "ALL"
        val today = LocalDate.now()
        val todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val plusWeekStr = today.plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val plusMonthStr = today.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val locationData: List<SchedulesEntity> = when (effectiveFilterType) {
            "FAVORITE" -> {
                if (userId == null) {
                    throw CustomException(ErrorCode.USER_NOT_FOUND)
                }

                when (effectiveDataRange) {
                    "ALL" -> schedulesRepository.findFavoriteSchedulesByUserId(
                        userId, bottomLeftLat, bottomLeftLng, topRightLat, topRightLng
                    )

                    "TODAY" -> schedulesRepository.findFavoriteSchedulesByUserIdForToday(
                        userId, bottomLeftLat, bottomLeftLng, topRightLat, topRightLng, todayStr
                    )

                    "WEEK" -> schedulesRepository.findFavoriteSchedulesByUserIdForWeek(
                        userId, bottomLeftLat, bottomLeftLng, topRightLat, topRightLng, todayStr,
                        plusWeekStr
                    )

                    "MONTH" -> schedulesRepository.findFavoriteSchedulesByUserIdForMonth(
                        userId, bottomLeftLat, bottomLeftLng, topRightLat, topRightLng, todayStr,
                        plusMonthStr
                    )

                    else -> throw CustomException(ErrorCode.INVALID_DATA_RANGE)
                }
            }

            "ALL" -> {
                when (effectiveDataRange) {
                    "ALL" -> schedulesRepository.findByLatLng(
                        bottomLeftLat,
                        bottomLeftLng,
                        topRightLat,
                        topRightLng
                    )

                    "TODAY" -> schedulesRepository.findBySchedulesForToday(
                        bottomLeftLat, bottomLeftLng, topRightLat, topRightLng, todayStr
                    )

                    "WEEK" -> schedulesRepository.findBySchedulesForWeek(
                        bottomLeftLat,
                        bottomLeftLng,
                        topRightLat,
                        topRightLng,
                        todayStr,
                        plusWeekStr
                    )

                    "MONTH" -> schedulesRepository.findBySchedulesForMonth(
                        bottomLeftLat,
                        bottomLeftLng,
                        topRightLat,
                        topRightLng,
                        todayStr,
                        plusMonthStr
                    )

                    else -> throw CustomException(ErrorCode.INVALID_DATA_RANGE)
                }
            }

            else -> throw CustomException(ErrorCode.INVALID_FILTER_TYPE)
        }

        if (locationData.isEmpty()) {
            throw CustomException(ErrorCode.SCHEDULE_NOT_FOUND)
        }

        return locationData.map { schedule ->
            KakaoMapDto.SchedulesLocationData.from(schedule.getClasses(), schedule)
        }
    }
}