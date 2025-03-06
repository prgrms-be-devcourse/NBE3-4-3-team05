package z9.hobby.domain.schedules.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.classes.entity.ClassUserEntity
import z9.hobby.domain.classes.repository.ClassRepository
import z9.hobby.domain.schedules.dto.SchedulesRequestDto.CreateRequest
import z9.hobby.domain.schedules.dto.SchedulesRequestDto.UpdateRequest
import z9.hobby.domain.schedules.dto.SchedulesResponseDto
import z9.hobby.domain.schedules.dto.SchedulesResponseDto.ResponseData.Companion.from
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.model.schedules.SchedulesEntity
import z9.hobby.model.schedules.SchedulesRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class SchedulesService(
    private val schedulesRepository: SchedulesRepository,
    private val classesRepository: ClassRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    //생성
    @Transactional
    fun create(requestData: CreateRequest, userId: Long): SchedulesResponseDto.ResponseData {
        if (requestData.meetingTime.isNullOrBlank() || requestData.meetingTitle.isNullOrBlank()) {
            throw CustomException(ErrorCode.SCHEDULE_CREATE_FAILED)
        }

        try {
            // 날짜 형식 검증 및 미래 날짜 검증
            val meetingDateTime = LocalDate.parse(
                requestData.meetingTime,
                dateFormatter
            )

            // 현재 시간과 비교하여 과거인지 확인
            if (meetingDateTime.isBefore(LocalDate.now())) {
                throw CustomException(ErrorCode.INVALID_MEETING_TIME)
            }

            // 모임 존재 여부 확인
            val classes = classesRepository.findById(requestData.classId!!)
                .orElseThrow { CustomException(ErrorCode.CLASS_NOT_FOUND) }

            // 모임장 권한 체크
            if (classes.masterId != userId) {
                throw CustomException(ErrorCode.CLASS_ACCESS_DENIED)
            }

            // 새로운 일정 엔티티 생성
            val schedules: SchedulesEntity = SchedulesEntity.builder()
                .classes(classes)
                .meetingTime(requestData.meetingTime)
                .meetingTitle(requestData.meetingTitle)
                .build()

            // DB에 저장
            val savedSchedule = schedulesRepository.save(schedules)

            // 저장된 결과를 ResponseDto로 변환해서 반환
            return from(savedSchedule)
        } catch (e: DateTimeParseException) {
            throw CustomException(ErrorCode.INVALID_MEETING_TIME_FORMAT)
        } catch (e: CustomException) {
            throw e // 이미 처리된 CustomException은 그대로 던지기
        } catch (e: Exception) {
            throw CustomException(ErrorCode.SCHEDULE_CREATE_FAILED)
        }
    }

    //수정
    @Transactional
    fun modify(
        scheduleId: Long?,
        classId: Long?,
        requestData: UpdateRequest,
        userId: Long?
    ): SchedulesResponseDto.ResponseData {
        if (requestData.meetingTime.isNullOrBlank() || requestData.meetingTitle.isNullOrBlank()) {
            throw CustomException(ErrorCode.SCHEDULE_UPDATE_FAILED)
        }

        try {
            // 날짜 형식 검증
            val meetingDateTime = LocalDate.parse(
                requestData.meetingTime,
                dateFormatter
            )

            // 현재 시간과 비교하여 과거인지 확인
            if (meetingDateTime.isBefore(LocalDate.now())) {
                throw CustomException(ErrorCode.INVALID_MEETING_TIME)
            }

            // 일정 조회 및 모임 Id 조회
            val schedule = schedulesRepository.findScheduleByIdAndClassesId(scheduleId, classId)
                .orElseThrow { CustomException(ErrorCode.SCHEDULE_NOT_FOUND) }

            // 모임장 권한 체크
            if (schedule.getClasses().masterId != userId) {
                throw CustomException(ErrorCode.CLASS_ACCESS_DENIED)
            }

            // 일정 정보 업데이트
            schedule.updateSchedule(requestData.meetingTime, requestData.meetingTitle)
            // DB에 저장
            val savedSchedule = schedulesRepository.save(schedule)

            // 수정된 결과를 ResponseDto로 변환해서 반환
            return from(savedSchedule)
        } catch (e: DateTimeParseException) {
            throw CustomException(ErrorCode.INVALID_MEETING_TIME_FORMAT)
        } catch (e: CustomException) {
            throw e // 이미 처리된 CustomException은 그대로 던지기
        } catch (e: Exception) {
            throw CustomException(ErrorCode.SCHEDULE_UPDATE_FAILED)
        }
    }

    @Transactional
    fun delete(scheduleId: Long?, classId: Long?, userId: Long?) {
        try {
            // 일정 조회 및 모임 id 조회
            val schedule = schedulesRepository.findScheduleByIdAndClassesId(scheduleId, classId)
                .orElseThrow { CustomException(ErrorCode.SCHEDULE_NOT_FOUND) }

            // 모임장 권한 체크
            if (schedule.getClasses().masterId != userId) {
                throw CustomException(ErrorCode.CLASS_ACCESS_DENIED)
            }

            // 일정 삭제
            schedulesRepository.delete(schedule)
        } catch (e: CustomException) {
            throw e
        } catch (e: Exception) {
            throw CustomException(ErrorCode.SCHEDULE_DELETE_FAILED)
        }
    }

    @Transactional(readOnly = true)
    fun getSchedulesList(classId: Long, userId: Long): List<SchedulesResponseDto.ResponseData> {
        try {
            val classes = classesRepository.findById(classId)
                .orElseThrow { CustomException(ErrorCode.CLASS_NOT_FOUND) }

            if (classes.masterId != userId &&
                classes.users.none { user: ClassUserEntity -> user.userId == userId }
            ) {
                throw CustomException(ErrorCode.CLASS_ACCESS_DENIED)
            }

            // ResponseDto로 변환하여 반환
            return schedulesRepository.findSchedulesByClassesId(classId)
                .map { schedulesEntity -> from(schedulesEntity) }
        } catch (e: CustomException) {
            throw e
        } catch (e: Exception) {
            throw CustomException(ErrorCode.SCHEDULE_READ_FAILED)
        }
    }

    @Transactional(readOnly = true)
    fun getScheduleDetail(scheduleId: Long?, classId: Long, userId: Long): SchedulesResponseDto.ResponseData {
        try {
            val classes = classesRepository.findById(classId)
                .orElseThrow { CustomException(ErrorCode.CLASS_NOT_FOUND) }

            if (classes.masterId != userId &&
                classes.users.none { user: ClassUserEntity -> user.userId == userId }
            ) {
                throw CustomException(ErrorCode.CLASS_ACCESS_DENIED)
            }

            // 특정 일정 조회
            val schedule = schedulesRepository.findScheduleByIdAndClassesId(scheduleId, classId)
                .orElseThrow { CustomException(ErrorCode.SCHEDULE_NOT_FOUND) }

            // ResponseDto로 변환하여 반환
            return from(schedule)
        } catch (e: CustomException) {
            throw e
        } catch (e: Exception) {
            throw CustomException(ErrorCode.SCHEDULE_READ_FAILED)
        }
    }
}
