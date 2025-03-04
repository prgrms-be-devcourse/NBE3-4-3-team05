package z9.hobby.domain.checkin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import z9.hobby.domain.checkin.dto.CheckInRequestDto;
import z9.hobby.domain.checkin.dto.CheckInResponseDto;
import z9.hobby.domain.checkin.service.CheckInService;
import z9.hobby.global.response.BaseResponse;
import z9.hobby.global.response.SuccessCode;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checkin")
@Tag(name = "Checkin controller", description = "모임 참석 유무 컨트롤러")
public class CheckInController {
    private final CheckInService checkInService;

    @PostMapping
    @Operation(summary = "참석 여부 생성")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<Void> createCheckIn(
            Principal principal,
            @Valid @RequestBody CheckInRequestDto.CheckInDto requestDto
    ) {
        Long userId = Long.parseLong(principal.getName());
        checkInService.createCheckIn(userId, requestDto);
        return BaseResponse.Companion.ok(SuccessCode.CHECK_IN_CREATE_SUCCESS);
    }

    @PutMapping
    @Operation(summary = "참석 여부 변경")
    @SecurityRequirement(name="bearerAuth")
    public BaseResponse<Void> updateCheckIn(
            Principal principal,
            @Valid @RequestBody CheckInRequestDto.CheckInDto requestDto
    ){
        Long userId = Long.parseLong(principal.getName());
        checkInService.updateCheckIn(userId, requestDto);
        return BaseResponse.Companion.ok(SuccessCode.CHECK_IN_UPDATE_SUCCESS);
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "모임 인원 투표 현황 보여주기")
    @SecurityRequirement(name="bearerAuth")
    public BaseResponse<List<CheckInResponseDto.ResponseData>> getAllCheckInsForSchedule(@PathVariable Long scheduleId) {
        List<CheckInResponseDto.ResponseData> responseDataList = checkInService.getAllCheckIns(scheduleId);
        return BaseResponse.Companion.ok(SuccessCode.CHECK_IN_READ_SUCCESS, responseDataList);
    }

    @GetMapping("/{scheduleId}/my")
    @Operation(summary = "모임 내 투표 현황 보여주기")
    @SecurityRequirement(name="bearerAuth")
    public BaseResponse<CheckInResponseDto.ResponseData> getMyCheckIn(
            @PathVariable Long scheduleId,
            Principal principal
    ){
        Long userId = Long.parseLong(principal.getName());
        CheckInResponseDto.ResponseData response = checkInService.getMyCheckIn(scheduleId, userId);
        return BaseResponse.Companion.ok(SuccessCode.CHECK_IN_READ_SUCCESS, response);
    }
}

