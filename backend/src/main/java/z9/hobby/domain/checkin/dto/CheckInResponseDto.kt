package z9.hobby.domain.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import z9.hobby.model.checkIn.CheckInEntity;


public class CheckInResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ResponseData {
        private final Long checkInId;
        private final Long scheduleId;
        private final Long userId;
        private final Boolean checkIn;
        private final Boolean isCheckIn;

        public static ResponseData from(CheckInEntity checkInEntity) {
            return ResponseData.builder()
                    .checkInId(checkInEntity.getId())
                    .scheduleId(checkInEntity.getSchedules().getId())
                    .userId(checkInEntity.getUserId())
                    .checkIn(checkInEntity.isCheckIn())
                    .isCheckIn(true)
                    .build();
        }
    }
}
