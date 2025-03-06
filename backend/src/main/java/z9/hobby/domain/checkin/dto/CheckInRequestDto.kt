package z9.hobby.domain.checkin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CheckInRequestDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckInDto {
        @NotNull(message = "Schedule ID must not be null")
        private Long scheduleId;

        @NotNull(message = "Check-in status must not be null")
        private Boolean checkIn;
    }
}
