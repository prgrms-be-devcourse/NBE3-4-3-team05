package z9.hobby.domain.checkin.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.security.Principal
import org.springframework.web.bind.annotation.*
import z9.hobby.domain.checkin.dto.CheckInRequestDto
import z9.hobby.domain.checkin.dto.CheckInResponseDto
import z9.hobby.domain.checkin.service.CheckInServiceImpl  // 변경
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.SuccessCode

@RestController
@RequestMapping("/api/v1/checkin")
@Tag(name = "Checkin controller", description = "모임 참석 유무 컨트롤러")
class CheckInController(private val checkInService: CheckInServiceImpl) {

    @PostMapping
    @Operation(summary = "참석 여부 생성")
    @SecurityRequirement(name = "bearerAuth")
    fun createCheckIn(
        principal: Principal,
        @Valid @RequestBody requestDto: CheckInRequestDto
    ): BaseResponse<Nothing> {
        val userId = principal.name.toLong()
        checkInService.createCheckIn(userId, requestDto)
        return BaseResponse.ok(SuccessCode.CHECK_IN_CREATE_SUCCESS)
    }

    @PutMapping
    @Operation(summary = "참석 여부 변경")
    @SecurityRequirement(name = "bearerAuth")
    fun updateCheckIn(
        principal: Principal,
        @Valid @RequestBody requestDto: CheckInRequestDto
    ): BaseResponse<Nothing> {
        val userId = principal.name.toLong()
        checkInService.updateCheckIn(userId, requestDto)
        return BaseResponse.ok(SuccessCode.CHECK_IN_UPDATE_SUCCESS)
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "모임 인원 투표 현황 보여주기")
    @SecurityRequirement(name = "bearerAuth")
    fun getAllCheckInsForSchedule(@PathVariable scheduleId: Long): BaseResponse<List<CheckInResponseDto>> {
        val responseDataList = checkInService.getAllCheckIns(scheduleId)
        return BaseResponse.ok(SuccessCode.CHECK_IN_READ_SUCCESS, responseDataList)
    }

    @GetMapping("/{scheduleId}/my")
    @Operation(summary = "모임 내 투표 현황 보여주기")
    @SecurityRequirement(name = "bearerAuth")
    fun getMyCheckIn(
        @PathVariable scheduleId: Long,
        principal: Principal
    ): BaseResponse<CheckInResponseDto> {
        val userId = principal.name.toLong()
        val response = checkInService.getMyCheckIn(scheduleId, userId)
        return BaseResponse.ok(SuccessCode.CHECK_IN_READ_SUCCESS, response)
    }
}
