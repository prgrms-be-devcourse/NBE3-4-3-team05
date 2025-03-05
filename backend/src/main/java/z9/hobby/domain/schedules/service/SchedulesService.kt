package z9.hobby.domain.schedules.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.repository.ClassRepository;
import z9.hobby.domain.schedules.dto.SchedulesRequestDto;
import z9.hobby.domain.schedules.dto.SchedulesResponseDto;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.schedules.SchedulesRepository;

@Service
@RequiredArgsConstructor
public class SchedulesService {
    private final SchedulesRepository schedulesRepository;
    private final ClassRepository classesRepository;

    //생성
    @Transactional
    public SchedulesResponseDto.ResponseData create(SchedulesRequestDto.CreateRequest requestData, Long userId) {
        if (requestData.getMeetingTime() == null || requestData.getMeetingTitle() == null) {
            throw new CustomException(ErrorCode.SCHEDULE_CREATE_FAILED);
        }

        try {
            // 날짜 형식 검증 및 미래 날짜 검증
            LocalDate meetingDateTime = LocalDate.parse(
                    requestData.getMeetingTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            );

            // 현재 시간과 비교하여 과거인지 확인
            if (meetingDateTime.isBefore(LocalDate.now())) {
                throw new CustomException(ErrorCode.INVALID_MEETING_TIME);
            }

            // 모임 존재 여부 확인
            ClassEntity classes = classesRepository.findById(requestData.getClassId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

            // 모임장 권한 체크
            if (!classes.getMasterId().equals(userId)) {
                throw new CustomException(ErrorCode.CLASS_ACCESS_DENIED);
            }

            // 새로운 일정 엔티티 생성
            SchedulesEntity schedules = SchedulesEntity.builder()
                    .classes(classes)
                    .meetingTime(requestData.getMeetingTime())
                    .meetingTitle(requestData.getMeetingTitle())
                    .build();

            // DB에 저장
            SchedulesEntity savedSchedule = schedulesRepository.save(schedules);

            // 저장된 결과를 ResponseDto로 변환해서 반환
            return SchedulesResponseDto.ResponseData.from(savedSchedule);
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.INVALID_MEETING_TIME_FORMAT);
        } catch (CustomException e) {
            throw e;  // 이미 처리된 CustomException은 그대로 던지기
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SCHEDULE_CREATE_FAILED);
        }
    }

    //수정
    @Transactional
    public SchedulesResponseDto.ResponseData modify(Long scheduleId, Long classId, SchedulesRequestDto.UpdateRequest requestData, Long userId) {
        if (requestData.getMeetingTime() == null || requestData.getMeetingTitle() == null) {
            throw new CustomException(ErrorCode.SCHEDULE_UPDATE_FAILED);
        }

        try {
            // 날짜 형식 검증
            LocalDate meetingDateTime = LocalDate.parse(
                    requestData.getMeetingTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            );

            // 현재 시간과 비교하여 과거인지 확인
            if (meetingDateTime.isBefore(LocalDate.now())) {
                throw new CustomException(ErrorCode.INVALID_MEETING_TIME);
            }

            // 일정 조회 및 모임 Id 조회
            SchedulesEntity schedule = schedulesRepository.findScheduleByIdAndClassesId(scheduleId, classId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

            // 모임장 권한 체크
            if (!schedule.getClasses().getMasterId().equals(userId)) {
                throw new CustomException(ErrorCode.CLASS_ACCESS_DENIED);
            }

            // 일정 정보 업데이트
            schedule.updateSchedule(requestData.getMeetingTime(), requestData.getMeetingTitle());
            // DB에 저장
            SchedulesEntity savedSchedule = schedulesRepository.save(schedule);

            // 수정된 결과를 ResponseDto로 변환해서 반환
            return SchedulesResponseDto.ResponseData.from(savedSchedule);
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.INVALID_MEETING_TIME_FORMAT);
        } catch (CustomException e) {
            throw e;  // 이미 처리된 CustomException은 그대로 던지기
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SCHEDULE_UPDATE_FAILED);
        }
    }

    public void delete(Long scheduleId, Long classId, Long userId) {
        // 일정 조회 및 모임 id 조회
        SchedulesEntity schedule = schedulesRepository.findScheduleByIdAndClassesId(scheduleId, classId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 모임장 권한 체크
        if (!schedule.getClasses().getMasterId().equals(userId)) {
            throw new CustomException(ErrorCode.CLASS_ACCESS_DENIED);
        }

        try {
            // 일정 삭제
            schedulesRepository.delete(schedule);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SCHEDULE_DELETE_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public List<SchedulesResponseDto.ResponseData> getSchedulesList(Long classId, Long userId) {
        try {
            ClassEntity classes = classesRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

            if (!classes.getMasterId().equals(userId) &&
                    classes.getUsers().stream()
                            .noneMatch(user -> user.getUserId().equals(userId))) {
                throw new CustomException(ErrorCode.CLASS_ACCESS_DENIED);
            }

            // ResponseDto로 변환하여 반환
            return schedulesRepository.findSchedulesByClassesId(classId).stream()
                    .map(SchedulesResponseDto.ResponseData::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SCHEDULE_READ_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public SchedulesResponseDto.ResponseData getScheduleDetail(Long scheduleId, Long classId, Long userId) {
        try {
            ClassEntity classes = classesRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

            if (!classes.getMasterId().equals(userId) &&
                    classes.getUsers().stream()
                            .noneMatch(user -> user.getUserId().equals(userId))) {
                throw new CustomException(ErrorCode.CLASS_ACCESS_DENIED);
            }

            // 특정 일정 조회
            SchedulesEntity schedule = schedulesRepository.findScheduleByIdAndClassesId(scheduleId, classId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

            // ResponseDto로 변환하여 반환
            return SchedulesResponseDto.ResponseData.from(schedule);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SCHEDULE_READ_FAILED);
        }
    }
}
