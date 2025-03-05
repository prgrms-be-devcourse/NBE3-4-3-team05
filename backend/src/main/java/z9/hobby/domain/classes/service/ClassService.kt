package z9.hobby.domain.classes.service;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.classes.dto.ClassRequest
import z9.hobby.domain.classes.dto.ClassResponse
import z9.hobby.domain.classes.entity.ClassEntity
import z9.hobby.domain.classes.entity.ClassUserEntity
import z9.hobby.domain.classes.repository.ClassBlackListRepository
import z9.hobby.domain.classes.repository.ClassRepository
import z9.hobby.domain.classes.repository.ClassUserRepository
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.model.user.UserRepository
import java.util.*

@Service
class ClassService(
    private val classRepository: ClassRepository,
    private val classUserRepository: ClassUserRepository,
    private val userRepository: UserRepository,
    private val classBlackListRepository: ClassBlackListRepository
) {
    @Transactional
    fun save(requestData: ClassRequest.ClassRequestData, userId: Long): ClassResponse.ClassResponseData {
        // 회원 당 모임 생성 개수 제한 : 3개
        val existingClasses: MutableList<ClassEntity> = classRepository.findByMasterId(userId)
        if (existingClasses.size >= 3) {
            throw CustomException(ErrorCode.CLASS_CREATE_LIMIT_EXCEEDED)
        }

        val newClass = ClassEntity(
            name = requestData.name,
            favorite = requestData.favorite,
            description = requestData.description,
            masterId = userId
        )

        newClass.addMember(userId)

        val classEntity: ClassEntity = classRepository.save(newClass)

        return ClassResponse.ClassResponseData.from(classEntity)
    }

    @Transactional
    fun getClassInfo(classId: Long, userId: Long): ClassResponse.EntryResponseData {
        // 1. 모임 존재 여부 확인
        val classEntity: ClassEntity = findByClassId(classId)

        // 2. 유저가 모임 멤버인지 확인
        if (!classUserRepository.existsByUserIdAndClassesId(userId, classId)) {
            throw CustomException(ErrorCode.CLASS_ACCESS_DENIED);
        }
        return ClassResponse.EntryResponseData.from(classEntity);
    }

    @Transactional
    fun joinMembership(classId: Long, userId: Long): ClassResponse.JoinResponseData {
        // 해당 모임이 존재하는지 확인
        val classEntity: ClassEntity = findByClassId(classId);

        // 이미 가입된 회원인지 검증
        if (classUserRepository.existsByClasses_IdAndUserId(classId, userId)) {
            throw CustomException(ErrorCode.CLASS_EXISTS_MEMBER);
        }

        // 강퇴된 회원 재가입 방지
        if (classBlackListRepository.existsByClasses_IdAndUserId(classId, userId)) {
            throw CustomException(ErrorCode.CLASS_USER_BANNED);
        }

        classEntity.addMember(userId);

        return ClassResponse.JoinResponseData.from(classEntity);
    }

    @Transactional
    fun deleteMembership(classId: Long, userId: Long) {
        // 해당 모임이 존재하는지 확인
        val classEntity: ClassEntity = findByClassId(classId);

        // 가입된 회원인지 검증
        val user: ClassUserEntity = classUserRepository.findByClassesIdAndUserId(classId, userId)
            .orElseThrow { CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER) }

        // 모임장인지 검증 (모임장은 권한을 위임해야 탈퇴할 수 있음)
        if (userId == classEntity.masterId) {
            throw CustomException(ErrorCode.CLASS_MASTER_TRANSFER_REQUIRED);
        }

        classEntity.removeMember(user);
    }

    @Transactional
    fun modifyClassInfo(classId: Long, userId: Long, requestData: ClassRequest.ModifyRequestData) {
        // 1. 모임 존재 여부 확인
        val classEntity: ClassEntity = findByClassId(classId)

        // 2. 모임장 권한 확인
        if (classEntity.masterId != userId) {
            throw CustomException(ErrorCode.CLASS_MODIFY_DENIED);
        }

        // 3. 모임 정보 수정
        classEntity.updateClassInfo(requestData.name, requestData.description)
    }

    @Transactional
    fun getUserListByClassId(classId: Long): ClassResponse.ClassUserListData {
        // 해당 모임이 존재하는지 확인
        val classEntity = findByClassId(classId)
        val classUserList = classUserRepository.findByClassesId(classId)
        val userIds = classUserList.map { it.userId }
        val users = userRepository.findAllById(userIds)
        return ClassResponse.ClassUserListData.from(classEntity, users)
    }

    @Transactional
    fun deleteClass(classId: Long, userId: Long) {
        // 1. 모임 존재 여부 확인
        val classEntity: ClassEntity = findByClassId(classId)

        // 2. 모임장 권한 확인
        if (classEntity.masterId != userId) {
            throw CustomException(ErrorCode.CLASS_USER_FORBIDDEN)
        }

        // 3. 모임장을 제외한 회원 존재 여부 확인
        val memberCount: Long = classUserRepository.countByClassesIdAndUserIdNot(classId, userId)
        if (memberCount > 0) {
            throw CustomException(ErrorCode.CLASS_DELETE_DENIED_WITH_MEMBERS)
        }

        // 4. 모임 삭제
        classRepository.delete(classEntity)
    }

    @Transactional
    fun transferMaster(classId: Long, userId: Long, currentUserId: Long) {
        // 모임 존재 여부 확인
        val classEntity: ClassEntity = findByClassId(classId)

        // 현재 회원이 모임장인지 체크
        if (classEntity.masterId != currentUserId) {
            throw CustomException(ErrorCode.CLASS_USER_FORBIDDEN)
        }

        // 해당 회원이 모임에 속해있는지 체크
        if (!classUserRepository.existsByClasses_IdAndUserId(classId, userId)) {
            throw CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER);
        }

        // 본인에게 권한 위임 시 에러발생
        if (userId == currentUserId) {
            throw CustomException(ErrorCode.CLASS_MASTER_TRANSFER_ME)
        }

        classEntity.masterId = userId
    }

    @Transactional
    fun kickOut(classId: Long, userId: Long, currentUserId: Long) {
        // 모임 존재 여부 확인
        val classEntity: ClassEntity = findByClassId(classId)

        // 현재 회원이 모임장인지 체크
        if (classEntity.masterId != currentUserId) {
            throw CustomException(ErrorCode.CLASS_USER_FORBIDDEN)
        }

        // 해당 회원이 모임에 속해있는지 체크
        val user: ClassUserEntity = classUserRepository.findByClassesIdAndUserId(classId, userId)
            .orElseThrow { CustomException(ErrorCode.CLASS_NOT_EXISTS_MEMBER) }

        // 모임장은 강퇴 되지 않도록 체크
        if (userId == classEntity.masterId) {
            throw CustomException(ErrorCode.FAIL)
        }

        classEntity.removeMember(user)

        classEntity.addBlackList(userId)
    }

    @Transactional(readOnly = true)
    fun checkMember(classId: Long, currentUserId: Long): ClassResponse.CheckMemberData {
        // 해당 모임이 존재하는지 확인
        findByClassId(classId)

        // 가입된 회원인지 검증 -> 가입된 회원이면 true, 아니면 false
        val isMember: Boolean = classUserRepository.existsByClasses_IdAndUserId(classId, currentUserId)

        return ClassResponse.CheckMemberData.from(isMember)
    }

    @Transactional(readOnly = true)
    fun checkBlackList(classId: Long, currentUserId: Long): ClassResponse.CheckBlackListData {
        // 해당 모임이 존재하는지 확인
        findByClassId(classId)

        // 강퇴된 회원 검증
        val isBlackList: Boolean = classBlackListRepository.existsByClasses_IdAndUserId(classId, currentUserId)

        return ClassResponse.CheckBlackListData.from(isBlackList)
    }

    // 가장 마지막에 등록된 모임 조회
    fun findLatest(): Optional<ClassEntity> {
        return classRepository.findFirstByOrderByIdDesc()
    }

    // 모임이 존재하는지 확인하는 메서드
    fun findByClassId(classId: Long): ClassEntity {
        return classRepository.findById(classId)
            .orElseThrow { CustomException(ErrorCode.CLASS_NOT_FOUND) }
    }
}
