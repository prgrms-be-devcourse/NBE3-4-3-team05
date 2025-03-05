package z9.hobby.domain.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import z9.hobby.domain.user.dto.UserRequest
import z9.hobby.domain.user.dto.UserResponse
import z9.hobby.domain.user.service.UserService
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.SuccessCode
import java.security.Principal

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller", description = "회원 관련 기능")
class UserController(private val userService: UserService) {

    @GetMapping
    @Operation(summary = "회원 정보 조회")
    @SecurityRequirement(name = "bearerAuth")
    fun findUserInfo(principal: Principal): BaseResponse<UserResponse.UserInfo> {
        val userId = principal.name.toLong()
        val findData = userService.findUserInfo(userId)
        return BaseResponse.ok(SuccessCode.FIND_USER_INFO_SUCCESS, findData)
    }

    @PatchMapping("/profile")
    @Operation(summary = "회원 정보 수정")
    @SecurityRequirement(name = "bearerAuth")
    fun modifyUserInfo(
        @Valid @RequestBody requestDto: UserRequest.PatchUserInfo,
        principal: Principal
    ): BaseResponse<Nothing> {
        userService.patchUserInfo(requestDto, principal.name.toLong())
        return BaseResponse.ok(SuccessCode.PATCH_USER_INFO_SUCCESS)
    }

    @GetMapping("/schedules")
    @Operation(summary = "내 모임일정 전체 조회")
    @SecurityRequirement(name = "bearerAuth")
    fun findUserSchedules(principal: Principal): BaseResponse<UserResponse.UserSchedule> {
        // todo: 필터링 조건 추가. 참석 여부, 검색 기준일 (지나간 모임 일정은 따로 빼서 쓰는게 더 나을 수도)
        // todo: sorting 조건 추가. 현재 모임 meeting 시간 기준 내림 차순 정렬.
        val findData = userService.findUserSchedules(principal.name.toLong())
        return BaseResponse.ok(SuccessCode.FIND_USER_SCHEDULES_SUCCESS, findData)
    }

    @GetMapping("/classes")
    @Operation(summary = "내 모임방 전체 조회")
    @SecurityRequirement(name = "bearerAuth")
    fun findUserClasses(principal: Principal): BaseResponse<UserResponse.UserClass> {
        val findData = userService.findUserClasses(principal.name.toLong())
        return BaseResponse.ok(SuccessCode.FIND_USER_CLASSES_SUCCESS, findData)
    }
}