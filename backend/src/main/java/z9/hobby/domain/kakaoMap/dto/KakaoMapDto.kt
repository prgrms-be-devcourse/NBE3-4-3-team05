package z9.hobby.domain.kakaoMap.dto;

import z9.hobby.domain.classes.entity.ClassEntity
import z9.hobby.model.schedules.SchedulesEntity

class KakaoMapDto {

    data class SchedulesLocationData(
        val id: Long?,
        val classTitle: String,
        val scheduleTitle: String?,
        val favorite: String,
        val date: String?,
        val lat: Double?,
        val lng: Double?
    ) {
        companion object {
            @JvmStatic
            fun from(
                classEntity: ClassEntity,
                schedulesEntity: SchedulesEntity
            ): SchedulesLocationData {
                return SchedulesLocationData(
                    classEntity.id,
                    classEntity.name,
                    schedulesEntity.getMeetingTitle(),
                    classEntity.favorite,
                    schedulesEntity.getMeetingTime(),
                    schedulesEntity.getLat(),
                    schedulesEntity.getLng()
                );
            }
        }

    }
}