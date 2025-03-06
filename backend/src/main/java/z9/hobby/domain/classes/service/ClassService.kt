package z9.hobby.domain.classes.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.dto.ClassRequest;
import z9.hobby.domain.classes.dto.ClassResponse;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.entity.ClassUserEntity;
import z9.hobby.domain.classes.repository.ClassBlackListRepository;
import z9.hobby.domain.classes.repository.ClassRepository;
import z9.hobby.domain.classes.repository.ClassUserRepository;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.user.User;
import z9.hobby.model.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final ClassUserRepository classUserRepository;
    private final UserRepository userRepository;
    private final ClassBlackListRepository classBlackListRepository;

    @Transactional
    public ClassResponse.ClassResponseData save(ClassRequest.ClassRequestData requestData, Long userId) {
        // 회원 당 모임 생성 개수 제한 : 3개
        List<ClassEntity> existingClasses = classRepository.findByMasterId(userId);
        if (existingClasses.size() >= 3) {
            throw new CustomException(ErrorCode.CLASS_CREATE_LIMIT_EXCEEDED);
        }

        ClassEntity newClass = ClassEntity.builder()
                .name(requestData.getName())
                .favorite(requestData.getFavorite())
                .description(requestData.getDescription())
                .masterId(userId)
                .build();

        newClass.addMember(userId);

        ClassEntity classEntity = classRepository.save(newClass);

        return ClassResponse.ClassResponseData.from(classEntity);
    }

    @Transactional
    public ClassResponse.EntryResponseData getClassInfo(Long classId, Long userId) {
        // 1. 모임 존재 여부 확인
        ClassEntity classEntity = findByClassId(classId);

        // 2. 유저가 모임 멤버인지 확인
        if (!classUserRepository.existsByUserIdAndClassesId(userId, classId)) {
            throw new CustomException(ErrorCode.CLASS_ACCESS_DENIED);
        }
        return ClassResponse.EntryResponseData.from(classEntity);
    }

    @Transactional
    public ClassResponse.JoinResponseData joinMembership(Long classId, Long userId) {
        // 해당 모임이 존재하는지 확인
        ClassEntity classEntity = findByClassId(classId);

        // 이미 가입된 회원인지 검증
        if (classUserRepository.existsByClasses_IdAndUserId(classId, userId)) {
            throw new CustomException(ErrorCode.CLASS_EXISTS_MEMBER);
        }

        // 강퇴된 회원 재가입 방지
        if (classBlackListRepository.existsByClasses_IdAndUserId(classId, userId)) {
            throw new CustomException(ErrorCode.CLASS_USER_BANNED);
        }

        classEntity.addMember(userId);

        return ClassResponse.JoinResponseData.from(classEntity);
    }

    @Transactional
    public void deleteMembership(Long classId, Long userId) {
        // 해당 모임이 존재하는지 확인
        ClassEntity classEntity = findByClassId(classId);

        // 가입된 회원인지 검증
        ClassUserEntity user = classUserRepository.findByClassesIdAndUserId(classId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER));

        // 모임장인지 검증 (모임장은 권한을 위임해야 탈퇴할 수 있음)
        if (userId.equals(classEntity.getMasterId())) {
            throw new CustomException(ErrorCode.CLASS_MASTER_TRANSFER_REQUIRED);
        }

        classEntity.removeMember(user);
    }

    @Transactional
    public void modifyClassInfo(Long classId, Long userId, ClassRequest.ModifyRequestData requestData) {
        // 1. 모임 존재 여부 확인
        ClassEntity classEntity = findByClassId(classId);

        // 2. 모임장 권한 확인
        if (!classEntity.getMasterId().equals(userId)) {
            throw new CustomException(ErrorCode.CLASS_MODIFY_DENIED);
        }

        // 3. 모임 정보 수정
        classEntity.updateClassInfo(requestData.getName(), requestData.getDescription());
    }

    @Transactional
    public ClassResponse.ClassUserListData getUserListByClassId(Long classId) {
        // 해당 모임이 존재하는지 확인
        ClassEntity classEntity = findByClassId(classId);

        List<ClassUserEntity> classUserList = classUserRepository.findByClassesId(classId);

        List<Long> userIds = classUserList.stream().map(ClassUserEntity::getUserId).toList();

        List<User> users = userRepository.findAllById(userIds);

        return ClassResponse.ClassUserListData.from(classEntity, users);
    }

    @Transactional
    public void deleteClass(Long classId, Long userId) {
        // 1. 모임 존재 여부 확인
        ClassEntity classEntity = findByClassId(classId);

        // 2. 모임장 권한 확인
        if (!classEntity.getMasterId().equals(userId)) {
            throw new CustomException(ErrorCode.CLASS_USER_FORBIDDEN);
        }

        // 3. 모임장을 제외한 회원 존재 여부 확인
        long memberCount = classUserRepository.countByClassesIdAndUserIdNot(classId, userId);
        if (memberCount > 0) {
            throw new CustomException(ErrorCode.CLASS_DELETE_DENIED_WITH_MEMBERS);
        }

        // 4. 모임 삭제
        classRepository.delete(classEntity);
    }

    @Transactional
    public void transferMaster(Long classId, Long userId, Long currentUserId) {
        // 모임 존재 여부 확인
        ClassEntity classEntity = findByClassId(classId);

        // 현재 회원이 모임장인지 체크
        if (!classEntity.getMasterId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.CLASS_USER_FORBIDDEN);
        }

        // 해당 회원이 모임에 속해있는지 체크
        if (!classUserRepository.existsByClasses_IdAndUserId(classId, userId)) {
            throw new CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER);
        }

        // 본인에게 권한 위임 시 에러발생
        if (userId.equals(currentUserId)) {
            throw new CustomException(ErrorCode.CLASS_MASTER_TRANSFER_ME);
        }

        classEntity.setMasterId(userId);
    }

    @Transactional
    public void kickOut(Long classId, Long userId, Long currentUserId) {
        // 모임 존재 여부 확인
        ClassEntity classEntity = findByClassId(classId);

        // 현재 회원이 모임장인지 체크
        if (!classEntity.getMasterId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.CLASS_USER_FORBIDDEN);
        }

        // 해당 회원이 모임에 속해있는지 체크
        ClassUserEntity user = classUserRepository.findByClassesIdAndUserId(classId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER));

        // 모임장은 강퇴 되지 않도록 체크
        if (userId.equals(classEntity.getMasterId())) {
            throw new CustomException(ErrorCode.FAIL);
        }

        classEntity.removeMember(user);

        classEntity.addBlackList(userId);
    }

    @Transactional(readOnly = true)
    public ClassResponse.CheckMemberData checkMember(Long classId, Long currentUserId) {
        // 해당 모임이 존재하는지 확인
        findByClassId(classId);

        // 가입된 회원인지 검증 -> 가입된 회원이면 true, 아니면 false
        boolean isMember = classUserRepository.existsByClasses_IdAndUserId(classId, currentUserId);

        return ClassResponse.CheckMemberData.from(isMember);
    }

    @Transactional(readOnly = true)
    public ClassResponse.CheckBlackListData checkBlackList(Long classId, Long currentUserId) {
        // 해당 모임이 존재하는지 확인
        findByClassId(classId);

        // 강퇴된 회원 검증
        boolean isBlackList = classBlackListRepository.existsByClasses_IdAndUserId(classId, currentUserId);

        return ClassResponse.CheckBlackListData.from(isBlackList);
    }

    // 가장 마지막에 등록된 모임 조회
    public Optional<ClassEntity> findLatest() {
        return classRepository.findFirstByOrderByIdDesc();
    }

    // 모임이 존재하는지 확인하는 메서드
    public ClassEntity findByClassId(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));
    }
}
