package z9.hobby.domain.schedules.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import z9.hobby.domain.schedules.dto.SchedulesRequestDto.CreateRequest
import z9.hobby.domain.schedules.dto.SchedulesRequestDto.UpdateRequest
import z9.hobby.domain.schedules.dto.SchedulesResponseDto
import z9.hobby.domain.schedules.service.SchedulesService
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.BaseResponse.Companion.ok
import z9.hobby.global.response.SuccessCode
import java.security.Principal

@RestController
@RequestMapping("/api/v1/schedules")
@Tag(name = "Schedules Controller", description = "모임 일정 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class SchedulesController(
    private val schedulesService: SchedulesService
) {
    @PostMapping("/classes")
    @Operation(summary = "모임 일정 생성", description = "새로운 모임 일정을 생성합니다. 모임장만 생성 가능합니다.")
    fun create(
        @RequestBody @Valid requestData: CreateRequest,
        principal: Principal
    ): BaseResponse<SchedulesResponseDto.ResponseData> {
        // 모임장 권한 체크를 Service에서 처리하도록 userId 전달
        val response = schedulesService.create(requestData, extractUserId(principal))
        return ok(SuccessCode.SCHEDULE_CREATE_SUCCESS, response)
    }

    @PutMapping("/{scheduleId}/classes/{classId}")
    @Operation(summary = "모임 일정 수정", description = "모임의 일정을 수정합니다.")
    fun modify(
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @Parameter(description = "모임 ID", required = true) @PathVariable classId: Long,
        @RequestBody @Valid requestData: UpdateRequest,
        principal: Principal
    ): BaseResponse<SchedulesResponseDto.ResponseData> {
        val response = schedulesService.modify(scheduleId, classId, requestData, extractUserId(principal))
        return ok(SuccessCode.SCHEDULE_MODIFY_SUCCESS, response)
    }

    @DeleteMapping("/{scheduleId}/classes/{classId}")
    @Operation(summary = "모임 일정 삭제", description = "모임의 일정을 삭제합니다.")
    fun delete(
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @Parameter(description = "모임 ID", required = true) @PathVariable classId: Long,
        principal: Principal
    ): BaseResponse<Nothing> {
        schedulesService.delete(scheduleId, classId, extractUserId(principal))
        return ok(SuccessCode.SCHEDULE_DELETE_SUCCESS)
    }

    @GetMapping("/classes/{classId}")
    @Operation(summary = "모임 전체 일정 조회")
    fun getSchedulesList(
        @Parameter(description = "모임 ID", required = true) @PathVariable classId: Long,
        principal: Principal
    ): BaseResponse<List<SchedulesResponseDto.ResponseData>> {
        val schedules = schedulesService.getSchedulesList(classId, extractUserId(principal))
        return ok(SuccessCode.SCHEDULE_READ_SUCCESS, schedules)
    }

    @GetMapping("/{scheduleId}/classes/{classId}")
    @Operation(summary = "모임 일정 상세 조회")
    fun getScheduleDetail(
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @Parameter(description = "모임 ID", required = true) @PathVariable classId: Long,
        principal: Principal
    ): BaseResponse<SchedulesResponseDto.ResponseData> {
        val schedule = schedulesService.getScheduleDetail(scheduleId, classId, extractUserId(principal))
        return ok(SuccessCode.SCHEDULE_READ_SUCCESS, schedule)
    }

    // userId 추출 공통 메소드
    private fun extractUserId(principal: Principal): Long = principal.name.toLong()
}