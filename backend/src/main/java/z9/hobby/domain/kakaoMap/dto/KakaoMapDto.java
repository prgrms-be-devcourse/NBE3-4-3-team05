package z9.hobby.domain.kakaoMap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.model.schedules.SchedulesEntity;

public class KakaoMapDto {

    @Getter
    @AllArgsConstructor
    public static class SchedulesLocationData {
        private final Long id;
        private final String classTitle;
        private final String scheduleTitle;
        private final String favorite;
        private final String date;
        private final Double lat;
        private final Double lng;

        public static SchedulesLocationData from(
                ClassEntity classEntity,
                SchedulesEntity schedulesEntity
        ) {
            return new SchedulesLocationData(
                    classEntity.getId(),
                    classEntity.getName(),
                    schedulesEntity.getMeetingTitle(),
                    classEntity.getFavorite(),
                    schedulesEntity.getMeetingTime(),
                    schedulesEntity.getLat(),
                    schedulesEntity.getLng()
            );
        }
    }
}