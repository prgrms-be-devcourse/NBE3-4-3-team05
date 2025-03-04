package z9.hobby.domain.checkin.service

import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.checkin.dto.CheckInRequestDto
import z9.hobby.domain.checkin.dto.CheckInRequestDto.CheckInDto
import z9.hobby.domain.checkin.dto.CheckInResponseDto
import z9.hobby.domain.classes.repository.ClassUserRepository
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.model.checkIn.CheckInEntity
import z9.hobby.model.checkIn.CheckInEntityRepository
import z9.hobby.model.schedules.SchedulesRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

@Service
@RequiredArgsConstructor
class CheckInServiceImpl(
    private val checkInEntityRepository: CheckInEntityRepository,
    private val schedulesRepository: SchedulesRepository,
    private val classUserRepository: ClassUserRepository
) : CheckInService {

    @Transactional
    override fun createCheckIn(userId: Long, requestDto: CheckInDto) {
        if (checkInEntityRepository.existsByUserIdAndSchedulesId(userId, requestDto.scheduleId)) {
            throw CustomException(ErrorCode.CHECK_IN_ALREADY_EXISTS)
        }
        checkInProcess(userId, requestDto)
    }

    @Transactional
    override fun updateCheckIn(userId: Long, requestDto: CheckInDto) {
        checkInProcess(userId, requestDto)
    }

    // 공통 부분
    private fun checkInProcess(userId: Long, requestDto: CheckInDto) {
        val findSchedulesEntity = schedulesRepository.findById(requestDto.scheduleId)
            .orElseThrow { CustomException(ErrorCode.SCHEDULE_NOT_FOUND) }

        if (!classUserRepository.existsByClassesAndUserId(findSchedulesEntity.classes, userId)) {
            throw CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER)
        }

        val meetingDateTime = LocalDate.parse(
            findSchedulesEntity.meetingTime,
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        )

        if (meetingDateTime.isBefore(LocalDate.now())) {
            throw CustomException(ErrorCode.INVALID_PASSED_CHECK_IN)
        }

        val newCheckIn = CheckInEntity.builder()
            .schedules(findSchedulesEntity)
            .userId(userId)
            .checkIn(requestDto.checkIn)
            .build()

        checkInEntityRepository.save(newCheckIn)
    }

    @Transactional
    override fun getAllCheckIns(scheduleId: Long): List<CheckInResponseDto.ResponseData>? {
        val checkInEntities = checkInEntityRepository.findBySchedulesId(scheduleId)
        if (checkInEntities.isEmpty()) {
            throw CustomException(ErrorCode.SCHEDULE_NOT_FOUND)
        }
        return checkInEntities.map { CheckInResponseDto.ResponseData.from(it) }
    }

    @Transactional
    override fun getMyCheckIn(scheduleId: Long, userId: Long): CheckInResponseDto.ResponseData {
        val checkIn = checkInEntityRepository.findBySchedulesIdAndUserId(scheduleId, userId)
            .orElse(null)

        return if (checkIn == null) {
            CheckInResponseDto.ResponseData.builder()
                .checkInId(null)
                .scheduleId(scheduleId)
                .userId(userId)
                .checkIn(false)
                .isCheckIn(false)
                .build()
        } else {
            CheckInResponseDto.ResponseData.from(checkIn)
        }
    }
}
