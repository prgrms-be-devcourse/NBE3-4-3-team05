package z9.hobby.domain.classes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.base.ClassBaseTest;
import z9.hobby.domain.classes.dto.ClassRequest;
import z9.hobby.domain.classes.dto.ClassResponse;
import z9.hobby.domain.classes.entity.ClassBlackListEntity;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.entity.ClassUserEntity;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.user.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
class ClassServiceTest extends ClassBaseTest {
    private User masterUser;
    private User memberUser;
    private ClassEntity classEntity;

    @BeforeEach
    void setUp() {
        // 마스터와 멤버 유저 생성
        masterUser = createTestUser("test@email.com", "테스터");
        memberUser = createTestUser("member@email.com", "멤버");
    }

    @Test
    @DisplayName("모임 생성")
    void createClass() {
        // given
        ClassRequest.ClassRequestData requestData =
                ClassRequest.ClassRequestData.of("테스트 모임 제목", "관심사1", "테스트 모임 설명 10글자 이상");
        Long masterId = masterUser.getId();

        // when
        ClassResponse.ClassResponseData newClass = classService.save(requestData, masterId);

        // then
        assertThat(newClass)
                .extracting("name", "favorite", "description")
                .containsExactly(newClass.getName(), newClass.getFavorite(), newClass.getDescription());
    }

    @Test
    @DisplayName("모임 생성 - 모임 생성한 회원의 가입 확인")
    void createClassAndAddUser() {
        // given
        ClassRequest.ClassRequestData requestData =
                ClassRequest.ClassRequestData.of("테스트 모임 제목", "관심사1", "테스트 모임 설명 10글자 이상");
        Long masterId = masterUser.getId();

        // when
        ClassResponse.ClassResponseData newClass = classService.save(requestData, masterId);

        // then
        List<ClassUserEntity> userList = classUserRepository.findByClassesId(newClass.getId());

        assertThat(userList.getFirst().getUserId()).isEqualTo(masterId);
    }

    @Test
    @DisplayName("모임 생성 - 3개 초과 시")
    void createClassLimitExceeded() {
        // given
        createTestClass(masterUser.getId());
        createTestClass(masterUser.getId());
        createTestClass(masterUser.getId());

        ClassRequest.ClassRequestData requestData =
                ClassRequest.ClassRequestData.of("테스트 모임 제목", "관심사1", "테스트 모임 설명 10글자 이상");

        // when & then
        assertThatThrownBy(() -> classService.save(requestData, masterUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_CREATE_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("모임 존재 확인")
    void findClassById() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ClassEntity findClass = classService.findByClassId(classEntity.getId());

        // then
        assertThat(findClass.getName()).isEqualTo(classEntity.getName());
    }

    @Test
    @DisplayName("모임 존재 확인 - 존재하지 않는 경우 ex. 300000번 모임 조회")
    void findClassByIdNotFound() {
        // given

        // when & then
        assertThatThrownBy(() -> classService.findByClassId(300000L))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_NOT_FOUND);
    }

    @Test
    @DisplayName("모임 정보 보기")
    void getClassInfo() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ClassResponse.EntryResponseData classInfo = classService.getClassInfo(classEntity.getId(), masterUser.getId());

        // then
        assertThat(classInfo)
                .extracting("name", "favorite", "description")
                .containsExactly(classInfo.getName(), classInfo.getFavorite(), classInfo.getDescription());
    }

    @Test
    @DisplayName("모임 정보 찾기 - 모임 멤버가 아닌 경우")
    void getClassInfoNoMember() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.getClassInfo(classEntity.getId(), 3000L))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_ACCESS_DENIED);
    }

    @Test
    @DisplayName("모임 가입")
    void joinMembership() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ClassResponse.JoinResponseData joinMember = classService.joinMembership(classEntity.getId(), memberUser.getId());

        // then
        assertThat(joinMember)
                .extracting("id", "name")
                .containsExactly(joinMember.getId(), joinMember.getName());
    }

    @Test
    @DisplayName("모임 가입 - 이미 가입된 회원 확인")
    void joinMembershipExistMember() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.joinMembership(classEntity.getId(), memberUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_EXISTS_MEMBER);
    }

    @Test
    @DisplayName("모임 가입 - 강퇴된 회원 확인")
    void joinMembershipKickout() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addBlackList(memberUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.joinMembership(classEntity.getId(), memberUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_USER_BANNED);
    }

    @Test
    @DisplayName("모임 탈퇴")
    void deleteMembership() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        classService.deleteMembership(classEntity.getId(), memberUser.getId());

        // then
        Optional<ClassUserEntity> opClassUser = classUserRepository.findByClassesIdAndUserId(classEntity.getId(), memberUser.getId());

        assertThat(opClassUser).isEmpty();
    }

    @Test
    @DisplayName("모임 탈퇴 - 가입된 회원이 아닌 경우")
    void deleteMembershipNoMember() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.deleteMembership(classEntity.getId(), memberUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_NOT_EXISTS_MEMBER);
    }

    @Test
    @DisplayName("모임 탈퇴 - 모임장이 탈퇴하려는 경우")
    void deleteMembershipMaster() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.deleteMembership(classEntity.getId(), masterUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_MASTER_TRANSFER_REQUIRED);
    }

    @Test
    @DisplayName("모임 수정")
    void modifyClassInfo() {
        // given
        classEntity = createTestClass(masterUser.getId());
        ClassRequest.ModifyRequestData requestData = ClassRequest.ModifyRequestData.of("testName", "testDescription");

        // when
        classService.modifyClassInfo(classEntity.getId(), masterUser.getId(), requestData);

        // then
        ClassEntity findClass = classService.findByClassId(classEntity.getId());
        assertThat(findClass)
                .extracting("name", "description")
                .containsExactly("testName", "testDescription");
    }

    @Test
    @DisplayName("모임 수정 - 권한 확인 실패")
    void modifyClassInfoNoMaster() {
        // given
        classEntity = createTestClass(masterUser.getId());
        ClassRequest.ModifyRequestData requestData = ClassRequest.ModifyRequestData.of("testName", "testDescription");

        // when & then
        assertThatThrownBy(() -> classService.modifyClassInfo(classEntity.getId(), memberUser.getId(), requestData))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_MODIFY_DENIED);
    }

    @Test
    @DisplayName("모임에 가입한 회원 목록 조회")
    void getUserListByClassId() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        ClassResponse.ClassUserListData getUserList = classService.getUserListByClassId(classEntity.getId());

        //then
        assertThat(getUserList)
                .extracting("classId", "masterId", "userList")
                .containsExactly(getUserList.getClassId(), getUserList.getMasterId(), getUserList.getUserList());
    }

    @Test
    @DisplayName("모임 삭제")
    void deleteClass() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        classService.deleteClass(classEntity.getId(), masterUser.getId());

        // then
        Optional<ClassEntity> opClass = classRepository.findById(classEntity.getId());

        assertThat(opClass).isEmpty();
    }

    @Test
    @DisplayName("모임 삭제 - 권한 확인")
    void deleteClassNoMaster() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.deleteClass(classEntity.getId(), memberUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_USER_FORBIDDEN);
    }

    @Test
    @DisplayName("모임 삭제 - 회원이 존재할때")
    void deleteClassExistMember() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.deleteClass(classEntity.getId(), masterUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_DELETE_DENIED_WITH_MEMBERS);
    }

    @Test
    @DisplayName("모임장 권한 위임")
    void transferMaster() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        classService.transferMaster(classEntity.getId(), memberUser.getId(), masterUser.getId());

        // then
        ClassEntity findClass = classService.findByClassId(classEntity.getId());

        assertThat(findClass.getMasterId()).isEqualTo(memberUser.getId());
    }

    @Test
    @DisplayName("모임장 권한 위임 - 모임장이 아닐때")
    void transferMasterNotMaster() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.transferMaster(classEntity.getId(), memberUser.getId(), memberUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_USER_FORBIDDEN);
    }

    @Test
    @DisplayName("모임장 권한 위임 - 회원이 모임에 속해 있지 않을때")
    void transferMasterNotMember() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.transferMaster(classEntity.getId(), memberUser.getId(), masterUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_NOT_EXISTS_MEMBER);
    }

    @Test
    @DisplayName("모임 강퇴")
    void kickOut() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        classService.kickOut(classEntity.getId(), memberUser.getId(), masterUser.getId());

        // then
        Optional<ClassUserEntity> opUser = classUserRepository.findByClassesIdAndUserId(classEntity.getId(), memberUser.getId());
        assertThat(opUser).isEmpty();

        ClassBlackListEntity blackUser = classBlackListRepository.findByClassesIdAndUserId(classEntity.getId(), memberUser.getId()).get();
        assertThat(blackUser.getUserId()).isEqualTo(memberUser.getId());
    }

    @Test
    @DisplayName("모임 강퇴 - 권한 확인")
    void kickOutNotMaster() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());
        Long nonMasterUserId = 999L;

        // when & then
        assertThatThrownBy(() -> classService.kickOut(classEntity.getId(), memberUser.getId(), nonMasterUserId))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_USER_FORBIDDEN);
    }

    @Test
    @DisplayName("모임 강퇴 - 모임장을 강퇴하는 경우")
    void kickOutMe() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when & then
        assertThatThrownBy(() -> classService.kickOut(classEntity.getId(), masterUser.getId(), masterUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.FAIL);
    }

    @Test
    @DisplayName("모임에 가입 되어있는지 확인 - true")
    void checkMember() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        ClassResponse.CheckMemberData checkMember = classService.checkMember(classEntity.getId(), memberUser.getId());

        // then
        assertThat(checkMember.isMember()).isTrue();
    }

    @Test
    @DisplayName("모임에 가입 되어있는지 확인 - false")
    void checkMemberFalse() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ClassResponse.CheckMemberData checkMember = classService.checkMember(classEntity.getId(), memberUser.getId());

        // then
        assertThat(checkMember.isMember()).isFalse();
    }

    @Test
    @DisplayName("블랙리스트에 있는지 확인 - true")
    void checkBlackList() {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addBlackList(memberUser.getId());

        // when
        ClassResponse.CheckBlackListData checkBlackList = classService.checkBlackList(classEntity.getId(), memberUser.getId());

        // then
        assertThat(checkBlackList.isBlackListed()).isTrue();
    }

    @Test
    @DisplayName("블랙리스트에 있는지 확인 - false")
    void checkBlackListFalse() {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ClassResponse.CheckBlackListData checkBlackList = classService.checkBlackList(classEntity.getId(), memberUser.getId());

        // then
        assertThat(checkBlackList.isBlackListed()).isFalse();
    }
}
