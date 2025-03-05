package z9.hobby.domain.classes.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import z9.hobby.domain.classes.dto.ClassRequest.ClassRequestData
import z9.hobby.domain.classes.dto.ClassRequest.ModifyRequestData
import z9.hobby.domain.classes.dto.ClassResponse.*
import z9.hobby.domain.classes.service.ClassService
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.BaseResponse.Companion.ok
import z9.hobby.global.response.SuccessCode
import java.security.Principal

@RestController
@RequestMapping("/api/v1/classes")
@Tag(name = "Class Controller", description = "모임 컨트롤러")
class ClassController(
    private val classService: ClassService
) {
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 생성")
    fun create(
        @Valid @RequestBody requestData: ClassRequestData,
        principal: Principal
    ): BaseResponse<ClassResponseData> {
        val userId = principal.name.toLong()

        val responseData = classService.save(requestData, userId)

        return ok(SuccessCode.CLASS_CREATE_SUCCESS, responseData)
    }

    @PostMapping("/{classId}/membership")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 가입")
    fun membership(
        @PathVariable classId: Long,
        principal: Principal
    ): BaseResponse<JoinResponseData> {
        val userId = principal.name.toLong()

        val responseData = classService.joinMembership(classId, userId)

        return ok(SuccessCode.CLASS_JOIN_SUCCESS, responseData)
    }

    @DeleteMapping("/{classId}/membership")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 탈퇴")
    fun deleteMembership(
        @PathVariable classId: Long,
        principal: Principal
    ): BaseResponse<Nothing> {
        val userId = principal.name.toLong()

        classService.deleteMembership(classId, userId)

        return ok(SuccessCode.CLASS_RESIGN_SUCCESS)
    }

    @GetMapping("/{classId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임방 입장")
    fun entry(
        @PathVariable("classId") classId: Long,
        principal: Principal
    ): BaseResponse<EntryResponseData> {
        val userId = principal.name.toLong()

        val responseData = classService.getClassInfo(classId, userId)
        return ok(SuccessCode.SUCCESS, responseData)
    }

    @PatchMapping("/{classId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 수정")
    fun modifyClassInfo(
        @PathVariable("classId") classId: Long,
        @Valid @RequestBody requestData: ModifyRequestData,
        principal: Principal
    ): BaseResponse<Nothing> {
        val userId = principal.name.toLong()

        classService.modifyClassInfo(classId, userId, requestData)
        return ok(SuccessCode.CLASS_MODIFY_SUCCESS)
    }

    @GetMapping("/{classId}/memberList")
    @Operation(summary = "모임에 가입한 회원 목록 조회")
    fun getMemberList(@PathVariable classId: Long): BaseResponse<ClassUserListData> {
        val classUserListData = classService.getUserListByClassId(classId)

        return ok(SuccessCode.SUCCESS, classUserListData)
    }

    @DeleteMapping("/{classId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 삭제")
    fun deleteClass(
        @PathVariable classId: Long,
        principal: Principal
    ): BaseResponse<Nothing> {
        val userId = principal.name.toLong()
        classService.deleteClass(classId, userId)
        return ok(SuccessCode.CLASS_DELETE_SUCCESS)
    }

    @PatchMapping("/{classId}/users/{userId}/role")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임장 권한 위임")
    fun transferMaster(
        @PathVariable classId: Long,
        @PathVariable userId: Long,
        principal: Principal
    ): BaseResponse<Nothing> {
        val currentUserId = principal.name.toLong()

        classService.transferMaster(classId, userId, currentUserId)

        return ok(SuccessCode.CLASS_MASTER_TRANSFER_SUCCESS)
    }

    @DeleteMapping("/{classId}/users/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "회원 강퇴")
    fun kickOut(
        @PathVariable classId: Long,
        @PathVariable userId: Long,
        principal: Principal
    ): BaseResponse<Nothing> {
        val currentUserId = principal.name.toLong()

        classService.kickOut(classId, userId, currentUserId)

        return ok(SuccessCode.CLASS_KICK_OUT_SUCCESS)
    }

    @GetMapping("/{classId}/checkMember")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "가입된 회원 재가입 확인")
    fun checkMember(
        @PathVariable classId: Long,
        principal: Principal
    ): BaseResponse<CheckMemberData> {
        val currentUserId = principal.name.toLong()

        val checkMember = classService.checkMember(classId, currentUserId)

        return ok(SuccessCode.SUCCESS, checkMember)
    }

    @GetMapping("/{classId}/checkBlackList")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "강퇴된 회원 재가입 확인")
    fun checkBlackList(
        @PathVariable classId: Long,
        principal: Principal
    ): BaseResponse<CheckBlackListData> {
        val currentUserId = principal.name.toLong()

        val checkBlackListData = classService.checkBlackList(classId, currentUserId)

        return ok(SuccessCode.SUCCESS, checkBlackListData)
    }
}