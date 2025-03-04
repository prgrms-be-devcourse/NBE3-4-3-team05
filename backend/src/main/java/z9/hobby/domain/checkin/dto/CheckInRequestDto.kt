package z9.hobby.domain.checkin.dto

import jakarta.validation.constraints.NotNull
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor

class CheckInRequestDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class CheckInDto {
        private var scheduleId: @NotNull(message = "Schedule ID must not be null") Long? = null

        private var checkIn: @NotNull(message = "Check-in status must not be null") Boolean? = null
    }
}
