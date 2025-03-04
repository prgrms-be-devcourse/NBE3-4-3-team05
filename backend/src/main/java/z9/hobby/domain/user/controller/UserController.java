package z9.hobby.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import z9.hobby.domain.user.dto.UserRequest;
import z9.hobby.domain.user.dto.UserResponse;
import z9.hobby.domain.user.service.UserService;
import z9.hobby.global.response.BaseResponse;
import z9.hobby.global.response.SuccessCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller", description = "회원 관련 기능")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "회원 정보 조회")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<UserResponse.UserInfo> findUserInfo(
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        UserResponse.UserInfo findData = userService.findUserInfo(userId);
        return BaseResponse.Companion.ok(SuccessCode.FIND_USER_INFO_SUCCESS, findData);
    }

    @PatchMapping("/profile")
    @Operation(summary = "회원 정보 수정")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<Void> modifyUserInfo(
            @Valid @RequestBody UserRequest.PatchUserInfo requestDto,
            Principal principal) {
        userService.patchUserInfo(requestDto, Long.parseLong(principal.getName()));
        return BaseResponse.Companion.ok(SuccessCode.PATCH_USER_INFO_SUCCESS);
    }

    @GetMapping("/schedules")
    @Operation(summary = "내 모임일정 전체 조회")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<UserResponse.UserSchedule> findUserSchedules(
            Principal principal){
        //todo : 필터링 조건 추가. 참석 여부, 검색 기준일 (지나간 모임 일정은 따로 빼서 쓰는게 더 나을 수도)
        //todo : sorting 조건 추가. 현재 모임 meeting 시간 기준 내림 차순 정렬.
        UserResponse.UserSchedule findData
                = userService.findUserSchedules(Long.parseLong(principal.getName()));

        return BaseResponse.Companion.ok(SuccessCode.FIND_USER_SCHEDULES_SUCCESS, findData);
    }

    @GetMapping("/classes")
    @Operation(summary = "내 모임방 전체 조회")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<UserResponse.UserClass> findUserClasses(
            Principal principal ) {

        UserResponse.UserClass findData =
                userService.findUserClasses(Long.parseLong(principal.getName()));

        return BaseResponse.Companion.ok(SuccessCode.FIND_USER_CLASSES_SUCCESS, findData);
    }
}
