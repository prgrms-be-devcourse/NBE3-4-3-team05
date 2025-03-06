package z9.hobby.domain.checkin.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.checkin.dto.CheckInRequestDto;
import z9.hobby.domain.checkin.dto.CheckInResponseDto;
import z9.hobby.domain.classes.repository.ClassUserRepository;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.checkIn.CheckInEntity;
import z9.hobby.model.checkIn.CheckInEntityRepository;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.schedules.SchedulesRepository;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {
    private final CheckInEntityRepository checkInEntityRepository;
    private final SchedulesRepository schedulesRepository;
    private final ClassUserRepository classUserRepository;

    // 처음 투표 시 생성
    @Transactional
    @Override
    public void createCheckIn(Long userId, CheckInRequestDto.CheckInDto requestDto) {
        if (checkInEntityRepository.existsByUserIdAndSchedulesId(userId, requestDto.getScheduleId())) {
            throw new CustomException(ErrorCode.CHECK_IN_ALREADY_EXISTS);
        }
        checkInProcess(userId, requestDto);
    }
    // 투표 결과 변경
    @Transactional
    @Override
    public void updateCheckIn(Long userId, CheckInRequestDto.CheckInDto requestDto) {
        checkInProcess(userId, requestDto);
    }

    // 공통 부분
    private void checkInProcess(Long userId, CheckInRequestDto.CheckInDto requestDto) {
        SchedulesEntity findSchedulesEntity = schedulesRepository.findById(requestDto.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!classUserRepository.existsByClassesAndUserId(findSchedulesEntity.getClasses(), userId)) {
            throw new CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER);
        }
        LocalDate meetingDateTime = LocalDate.parse(
                findSchedulesEntity.getMeetingTime(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );

        if (meetingDateTime.isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_PASSED_CHECK_IN);
        }
        CheckInEntity newCheckIn = CheckInEntity
                .builder()
                .schedules(findSchedulesEntity)
                .userId(userId)
                .checkIn(requestDto.getCheckIn())
                .build();
//        findSchedulesEntity.getCheckins().add(newCheckIn);

        checkInEntityRepository.save(newCheckIn);
    }
    @Transactional
    @Override
    public List<CheckInResponseDto.ResponseData> getAllCheckIns(Long scheduleId) {
        List<CheckInEntity> checkInEntities = checkInEntityRepository.findBySchedulesId(scheduleId);
        if (checkInEntities == null || checkInEntities.isEmpty()) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
        return checkInEntities.stream()
                .map(CheckInResponseDto.ResponseData::from)
                .collect(Collectors.toList());
    }
    @Transactional
    @Override
    public CheckInResponseDto.ResponseData getMyCheckIn(Long scheduleId, Long userId) {
        CheckInEntity checkIn = checkInEntityRepository.findBySchedulesIdAndUserId(scheduleId, userId)
                .orElse(null);
        if (checkIn == null) {
            return CheckInResponseDto.ResponseData.builder()
                    .checkInId(null)
                    .scheduleId(scheduleId)
                    .userId(userId)
                    .checkIn(false)
                    .isCheckIn(false)
                    .build();
        }else{
            return CheckInResponseDto.ResponseData.from(checkIn);
        }
    }

}
