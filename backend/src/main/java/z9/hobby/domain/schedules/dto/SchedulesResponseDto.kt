package z9.hobby.domain.schedules.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import z9.hobby.model.schedules.SchedulesEntity;

public class SchedulesResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ResponseData {
        private final Long scheduleId;      // 생성된 일정의 ID
        private final String meetingTime;   // 모임 시간
        private final String meetingTitle;  // 모임 제목

        public static ResponseData from(SchedulesEntity schedulesEntity) {
            return ResponseData.builder()
                    .scheduleId(schedulesEntity.getId())
                    .meetingTime(schedulesEntity.getMeetingTime())
                    .meetingTitle(schedulesEntity.getMeetingTitle())
                    .build();
        }
    }
}
