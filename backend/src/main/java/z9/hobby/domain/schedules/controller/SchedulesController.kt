package z9.hobby.domain.schedules.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import z9.hobby.domain.schedules.dto.SchedulesRequestDto;
import z9.hobby.domain.schedules.dto.SchedulesResponseDto;
import z9.hobby.domain.schedules.service.SchedulesService;
import z9.hobby.global.response.BaseResponse;
import z9.hobby.global.response.SuccessCode;

@RestController
@RequestMapping("/api/v1/schedules")
@Tag(name = "Schedules Controller", description = "모임 일정 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class SchedulesController {
    private final SchedulesService schedulesService;

    @PostMapping("/classes")
    @Operation(
            summary = "모임 일정 생성",
            description = "새로운 모임 일정을 생성합니다. 모임장만 생성 가능합니다."
    )
    public BaseResponse<SchedulesResponseDto.ResponseData> create(
            @RequestBody @Valid SchedulesRequestDto.CreateRequest requestData,
            Principal principal
    ) {
        // 모임장 권한 체크를 Service에서 처리하도록 userId 전달
        SchedulesResponseDto.ResponseData response = schedulesService.create(requestData, extractUserId(principal));
        return BaseResponse.Companion.ok(SuccessCode.SCHEDULE_CREATE_SUCCESS, response);
    }

    @PutMapping("/{scheduleId}/classes/{classId}")
    @Operation(
            summary = "모임 일정 수정",
            description = "모임의 일정을 수정합니다."
    )
    public BaseResponse<SchedulesResponseDto.ResponseData> modify(
            @Parameter(description = "일정 ID", required = true)
            @PathVariable Long scheduleId,
            @Parameter(description = "모임 ID", required = true)
            @PathVariable Long classId,
            @RequestBody @Valid SchedulesRequestDto.UpdateRequest requestData,
            Principal principal
    ) {
        SchedulesResponseDto.ResponseData response =
                schedulesService.modify(scheduleId, classId, requestData, extractUserId(principal));
        return BaseResponse.Companion.ok(SuccessCode.SCHEDULE_MODIFY_SUCCESS, response);
    }

    @DeleteMapping("/{scheduleId}/classes/{classId}")
    @Operation(
            summary = "모임 일정 삭제",
            description = "모임의 일정을 삭제합니다."
    )
    public BaseResponse<Void> delete(
            @Parameter(description = "일정 ID", required = true)
            @PathVariable Long scheduleId,
            @Parameter(description = "모임 ID", required = true)
            @PathVariable Long classId,
            Principal principal
    ) {
        schedulesService.delete(scheduleId, classId, extractUserId(principal));
        return BaseResponse.Companion.ok(SuccessCode.SCHEDULE_DELETE_SUCCESS);
    }

    @GetMapping("/classes/{classId}")
    @Operation(summary = "모임 전체 일정 조회")
    public BaseResponse<List<SchedulesResponseDto.ResponseData>> getSchedulesList(
            @Parameter(description = "모임 ID", required = true)
            @PathVariable Long classId,
            Principal principal
    ) {
        List<SchedulesResponseDto.ResponseData> schedules = schedulesService.getSchedulesList(classId, extractUserId(principal));
        return BaseResponse.Companion.ok(SuccessCode.SCHEDULE_READ_SUCCESS, schedules);
    }

    @GetMapping("/{scheduleId}/classes/{classId}")
    @Operation(summary = "모임 일정 상세 조회")
    public BaseResponse<SchedulesResponseDto.ResponseData> getScheduleDetail(
            @Parameter(description = "일정 ID", required = true)
            @PathVariable Long scheduleId,
            @Parameter(description = "모임 ID", required = true)
            @PathVariable Long classId,
            Principal principal
    ) {
        SchedulesResponseDto.ResponseData schedule = schedulesService.getScheduleDetail(scheduleId,classId, extractUserId(principal));
        return BaseResponse.Companion.ok(SuccessCode.SCHEDULE_READ_SUCCESS, schedule);
    }

    // userId 추출 공통 메소드
    private Long extractUserId(Principal principal) {
        return Long.parseLong(principal.getName());
    }
}