package z9.hobby.domain.classes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import z9.hobby.domain.classes.dto.ClassRequest;
import z9.hobby.domain.classes.dto.ClassResponse;
import z9.hobby.domain.classes.service.ClassService;
import z9.hobby.global.response.BaseResponse;
import z9.hobby.global.response.SuccessCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/classes")
@Tag(name = "Class Controller", description = "모임 컨트롤러")
public class ClassController {
    private final ClassService classService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 생성")
    public BaseResponse<ClassResponse.ClassResponseData> create(
            @RequestBody @Valid ClassRequest.ClassRequestData requestData,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        ClassResponse.ClassResponseData responseData = classService.save(requestData, userId);

        return BaseResponse.Companion.ok(SuccessCode.CLASS_CREATE_SUCCESS, responseData);
    }

    @PostMapping("/{classId}/membership")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 가입")
    public BaseResponse<ClassResponse.JoinResponseData> membership(
            @PathVariable Long classId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        ClassResponse.JoinResponseData responseData = classService.joinMembership(classId, userId);

        return BaseResponse.Companion.ok(SuccessCode.CLASS_JOIN_SUCCESS, responseData);
    }

    @DeleteMapping("/{classId}/membership")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 탈퇴")
    public BaseResponse<Void> deleteMembership(
            @PathVariable Long classId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        classService.deleteMembership(classId, userId);

        return BaseResponse.Companion.ok(SuccessCode.CLASS_RESIGN_SUCCESS);
    }

    @GetMapping("/{classId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임방 입장")
    public BaseResponse<ClassResponse.EntryResponseData> entry(
            @PathVariable("classId") Long classId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        ClassResponse.EntryResponseData responseData = classService.getClassInfo(classId, userId);
        return BaseResponse.Companion.ok(SuccessCode.SUCCESS, responseData);
    }

    @PatchMapping("/{classId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 수정")
    public BaseResponse<Void> modifyClassInfo(
            @PathVariable("classId") Long classId,
            @RequestBody @Valid ClassRequest.ModifyRequestData requestData,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());

        classService.modifyClassInfo(classId, userId, requestData);
        return BaseResponse.Companion.ok(SuccessCode.CLASS_MODIFY_SUCCESS);
    }

    @GetMapping("/{classId}/memberList")
    @Operation(summary = "모임에 가입한 회원 목록 조회")
    public BaseResponse<ClassResponse.ClassUserListData> getMemberList(@PathVariable Long classId) {
        ClassResponse.ClassUserListData classUserListData = classService.getUserListByClassId(classId);

        return BaseResponse.Companion.ok(SuccessCode.SUCCESS, classUserListData);
    }

    @DeleteMapping("/{classId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임 삭제")
    public BaseResponse<Void> deleteClass(
            @PathVariable Long classId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        classService.deleteClass(classId, userId);
        return BaseResponse.Companion.ok(SuccessCode.CLASS_DELETE_SUCCESS);
    }

    @PatchMapping("/{classId}/users/{userId}/role")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "모임장 권한 위임")
    public BaseResponse<Void> transferMaster(
            @PathVariable Long classId,
            @PathVariable Long userId,
            Principal principal
    ) {
        Long currentUserId = Long.parseLong(principal.getName());

        classService.transferMaster(classId, userId, currentUserId);

        return BaseResponse.Companion.ok(SuccessCode.CLASS_MASTER_TRANSFER_SUCCESS);
    }

    @DeleteMapping("/{classId}/users/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "회원 강퇴")
    public BaseResponse<Void> kickOut(
            @PathVariable Long classId,
            @PathVariable Long userId,
            Principal principal
    ) {
        Long currentUserId = Long.parseLong(principal.getName());

        classService.kickOut(classId, userId, currentUserId);

        return BaseResponse.Companion.ok(SuccessCode.CLASS_KICK_OUT_SUCCESS);
    }

    @GetMapping("/{classId}/checkMember")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "가입된 회원 재가입 확인")
    public BaseResponse<ClassResponse.CheckMemberData> checkMember(
            @PathVariable Long classId,
            Principal principal
    ) {
        Long currentUserId = Long.parseLong(principal.getName());

        ClassResponse.CheckMemberData checkMember = classService.checkMember(classId, currentUserId);

        return BaseResponse.Companion.ok(SuccessCode.SUCCESS, checkMember);
    }

    @GetMapping("/{classId}/checkBlackList")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "강퇴된 회원 재가입 확인")
    public BaseResponse<ClassResponse.CheckBlackListData> checkBlackList(
            @PathVariable Long classId,
            Principal principal
    ) {
        Long currentUserId = Long.parseLong(principal.getName());

        ClassResponse.CheckBlackListData checkBlackListData = classService.checkBlackList(classId, currentUserId);

        return BaseResponse.Companion.ok(SuccessCode.SUCCESS, checkBlackListData);
    }
}