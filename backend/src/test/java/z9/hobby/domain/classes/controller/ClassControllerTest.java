package z9.hobby.domain.classes.controller;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.authentication.dto.AuthenticationRequest;
import z9.hobby.domain.classes.base.ClassBaseTest;
import z9.hobby.domain.classes.dto.ClassRequest;
import z9.hobby.domain.classes.dto.ClassResponse;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.entity.ClassUserEntity;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.global.response.SuccessCode;
import z9.hobby.model.user.User;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_HEADER;

@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClassControllerTest extends ClassBaseTest {
    private User masterUser;
    private User memberUser;
    private String masterToken;
    private String memberToken;
    private ClassEntity classEntity;

    @BeforeEach
    void setUp() throws Exception {
        // 마스터와 멤버 유저 생성
        masterUser = createTestUser("test@email.com", "테스터");
        memberUser = createTestUser("member@email.com", "멤버");

        // 토큰 발급
        masterToken = loginAndGetToken("test@email.com");
        memberToken = loginAndGetToken("member@email.com");
    }

    // 공통 메서드
    private String loginAndGetToken(String email) throws Exception {
        AuthenticationRequest.Login request = AuthenticationRequest.Login.of(email, ClassBaseTest.TEST_PASSWORD);
        ResultActions result = mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        return result.andReturn().getResponse().getHeader(ACCESS_TOKEN_HEADER);
    }

    @Test
    @DisplayName("모임 생성")
    void createClass() throws Exception {
        // given
        ClassRequest.ClassRequestData requestData =
                ClassRequest.ClassRequestData.of("테스트 모임 제목", "관심사1", "테스트 모임 설명 10글자 이상");

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData))
                .header("Authorization", masterToken));

        // then
        ClassEntity class1 = classService.findLatest().get();

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CLASS_CREATE_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CLASS_CREATE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CLASS_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(class1.getId()))
                .andExpect(jsonPath("$.data.name").value(class1.getName()))
                .andExpect(jsonPath("$.data.favorite").value(class1.getFavorite()))
                .andExpect(jsonPath("$.data.description").value(class1.getDescription()))
        ;
    }

    @Test
    @DisplayName("모임 생성 - 3개 초과 시")
    void createClass_limitExceeded() throws Exception {
        // given
        createTestClass(masterUser.getId());
        createTestClass(masterUser.getId());
        createTestClass(masterUser.getId());

        // 모임 3개 생성 이후 추가 모임 생성
        ClassRequest.ClassRequestData requestData =
                ClassRequest.ClassRequestData.of("테스트 모임 제목", "관심사1", "테스트 모임 설명 10글자 이상");

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData))
                .header("Authorization", masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(ErrorCode.CLASS_CREATE_LIMIT_EXCEEDED.isSuccess()))
                .andExpect(jsonPath("$.message").value(ErrorCode.CLASS_CREATE_LIMIT_EXCEEDED.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.CLASS_CREATE_LIMIT_EXCEEDED.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    @DisplayName("모임 가입")
    void joinClass() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/classes/%d/membership".formatted(classEntity.getId()))
                .header("Authorization", memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CLASS_JOIN_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CLASS_JOIN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CLASS_JOIN_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value("%d".formatted(classEntity.getId())))
                .andExpect(jsonPath("$.data.name").value("%s".formatted(classEntity.getName())))
        ;
    }

    @Test
    @DisplayName("모임 탈퇴")
    void resignClass() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        ResultActions result = mockMvc.perform(delete("/api/v1/classes/%d/membership".formatted(classEntity.getId()))
                .header("Authorization", memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CLASS_RESIGN_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CLASS_RESIGN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CLASS_RESIGN_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    @DisplayName("모임방 입장")
    void entryClass() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/classes/%d".formatted(classEntity.getId()))
                .header("Authorization", masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.name").value("%s".formatted(classEntity.getName())))
                .andExpect(jsonPath("$.data.favorite").value("%s".formatted(classEntity.getFavorite())))
                .andExpect(jsonPath("$.data.description").value("%s".formatted(classEntity.getDescription())))
        ;
    }

    @Test
    @DisplayName("모임 수정")
    void modifyClass() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());
        ClassRequest.ModifyRequestData requestData =
                ClassRequest.ModifyRequestData.of("테스트 모임 제목", "테스트 모임 설명 10글자 이상");

        // when
        ResultActions result = mockMvc.perform(patch("/api/v1/classes/%d".formatted(classEntity.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData))
                .header("Authorization", masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CLASS_MODIFY_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CLASS_MODIFY_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CLASS_MODIFY_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    @DisplayName("모임에 가입한 회원 목록 조회")
    void getClassUsers() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/classes/%d/memberList".formatted(classEntity.getId()))
                .header("Authorization", masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.classId").value(classEntity.getId()))
                .andExpect(jsonPath("$.data.masterId").value(classEntity.getMasterId()))
        ;

        List<ClassUserEntity> classUserList = classUserRepository.findByClassesId(classEntity.getId());

        List<Long> userIds = classUserList.stream().map(ClassUserEntity::getUserId).toList();

        List<User> users = userRepository.findAllById(userIds);
        List<ClassResponse.ClassUserInfo> userList = users.stream()
                .map(ClassResponse.ClassUserInfo::from)
                .toList();

        for (int i = 0; i < userList.size(); i++) {
            result.andDo(print())
                    .andExpect(jsonPath("$.data.userList[%d].userId".formatted(i)).value(userList.get(i).getUserId()))
                    .andExpect(jsonPath("$.data.userList[%d].nickName".formatted(i)).value(userList.get(i).getNickName()));
        }
    }

    @Test
    @DisplayName("모임 삭제")
    void deleteClass() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ResultActions result = mockMvc.perform(delete("/api/v1/classes/%d".formatted(classEntity.getId()))
                .header("Authorization", masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CLASS_DELETE_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CLASS_DELETE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CLASS_DELETE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    @DisplayName("모임장 권한 위임")
    void transferMaster() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        ResultActions result = mockMvc.perform(patch("/api/v1/classes/%d/users/%d/role".formatted(classEntity.getId(), memberUser.getId()))
                .header("Authorization", masterToken));

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CLASS_MASTER_TRANSFER_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CLASS_MASTER_TRANSFER_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CLASS_MASTER_TRANSFER_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    @DisplayName("회원 강퇴")
    void addBlackList() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        ResultActions result = mockMvc.perform(delete("/api/v1/classes/%d/users/%d".formatted(classEntity.getId(), memberUser.getId()))
                .header("Authorization", masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CLASS_KICK_OUT_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CLASS_KICK_OUT_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CLASS_KICK_OUT_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
        ;
    }

    @Test
    @DisplayName("가입된 회원 재가입 확인 - true")
    void checkMember() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/classes/%d/checkMember".formatted(classEntity.getId()))
                .header("Authorization", memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.member").value(true))
        ;
    }

    @Test
    @DisplayName("가입된 회원 재가입 확인 - false")
    void checkMemberFalse() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/classes/%d/checkMember".formatted(classEntity.getId()))
                .header("Authorization", memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.member").value(false))
        ;
    }

    @Test
    @DisplayName("강퇴된 회원 재가입 확인 - true")
    void checkBlackList() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addBlackList(memberUser.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/classes/%d/checkBlackList".formatted(classEntity.getId()))
                .header("Authorization", memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.blackListed").value(true))
        ;
    }

    @Test
    @DisplayName("강퇴된 회원 재가입 확인 - false")
    void checkBlackListFalse() throws Exception {
        // given
        classEntity = createTestClass(masterUser.getId());
        classEntity.addMember(memberUser.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/classes/%d/checkBlackList".formatted(classEntity.getId()))
                .header("Authorization", memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.blackListed").value(false))
        ;
    }
}
