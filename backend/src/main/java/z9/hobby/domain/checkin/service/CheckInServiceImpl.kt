import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.checkin.dto.CheckInRequestDto
import z9.hobby.domain.checkin.dto.CheckInResponseDto
import z9.hobby.domain.checkin.service.CheckInService
import z9.hobby.domain.classes.repository.ClassUserRepository
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.model.checkIn.CheckInEntity
import z9.hobby.model.checkIn.CheckInEntityRepository
import z9.hobby.model.schedules.SchedulesRepository

@Service
class CheckInServiceImpl(
    private val checkInEntityRepository: CheckInEntityRepository,
    private val schedulesRepository: SchedulesRepository,
    private val classUserRepository: ClassUserRepository
) : CheckInService {

    @Transactional
    override fun createCheckIn(userId: Long, requestDto: CheckInRequestDto) {
        if (checkInEntityRepository.existsByUserIdAndSchedulesId(userId, requestDto.scheduleId!!)) {
            throw CustomException(ErrorCode.CHECK_IN_ALREADY_EXISTS)
        }
        checkInProcess(userId, requestDto)
    }

    @Transactional
    override fun updateCheckIn(userId: Long, requestDto: CheckInRequestDto) {
        checkInProcess(userId, requestDto)
    }

    @Transactional
    override fun checkInProcess(userId: Long, requestDto: CheckInRequestDto) {
        val findSchedulesEntity = schedulesRepository.findById(requestDto.scheduleId!!)
            .orElseThrow { CustomException(ErrorCode.SCHEDULE_NOT_FOUND) }

        if (!classUserRepository.existsByClassesAndUserId(findSchedulesEntity.classes, userId)) {
            throw CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER)
        }

        val meetingDateTime = LocalDate.parse(findSchedulesEntity.meetingTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        if (meetingDateTime.isBefore(LocalDate.now())) {
            throw CustomException(ErrorCode.INVALID_PASSED_CHECK_IN)
        }

        val newCheckIn = CheckInEntity(
            schedules = findSchedulesEntity,
            userId = userId,
            checkIn = requestDto.checkIn!!
        )
        checkInEntityRepository.save(newCheckIn)
    }

    @Transactional
    override fun getAllCheckIns(scheduleId: Long): List<CheckInResponseDto> {
        val checkInEntities = checkInEntityRepository.findBySchedulesId(scheduleId)
        if (checkInEntities.isEmpty()) {
            throw CustomException(ErrorCode.SCHEDULE_NOT_FOUND)
        }
        return checkInEntities.map { CheckInResponseDto.from(it) }
    }

    @Transactional
    override fun getMyCheckIn(scheduleId: Long, userId: Long): CheckInResponseDto {
        val checkIn = checkInEntityRepository.findBySchedulesIdAndUserId(scheduleId, userId)
            .orElse(null)
        return checkIn?.let { CheckInResponseDto.from(it) } ?: CheckInResponseDto(
            checkInId = 0L,
            scheduleId = scheduleId,
            userId = userId,
            checkIn = false,
            isCheckIn = false
        )
    }
}