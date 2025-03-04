package z9.hobby.domain.checkin.service;

import java.util.List;
import z9.hobby.domain.checkin.dto.CheckInRequestDto;
import z9.hobby.domain.checkin.dto.CheckInResponseDto;


public interface CheckInService {
    void createCheckIn(Long userId, CheckInRequestDto.CheckInDto requestDto);
    void updateCheckIn(Long userId, CheckInRequestDto.CheckInDto requestDto);
    List<CheckInResponseDto.ResponseData> getAllCheckIns(Long scheduleId);
    CheckInResponseDto.ResponseData getMyCheckIn(Long scheduleId, Long userId);
}