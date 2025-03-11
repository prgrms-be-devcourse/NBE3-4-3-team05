package z9.hobby.domain.schedules.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.authentication.dto.AuthenticationRequest;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.schedules.base.SchedulesBaseTest;
import z9.hobby.domain.schedules.dto.SchedulesRequestDto;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_HEADER;

@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SchedulesControllerTest extends SchedulesBaseTest {
    private User masterUser;
    private User memberUser;
    private String masterToken;
    private String memberToken;
    private ClassEntity classEntity;
    private SchedulesEntity scheduleEntity;
    private SchedulesRequestDto.CreateRequest scheduleRequest;

    @BeforeEach
    void setUp() throws Exception {
        // 마스터와 멤버 유저 생성
        masterUser = createTestUser("test@email.com", "테스터");
        memberUser = createTestUser("member@email.com", "멤버");

        // 토큰 발급
        masterToken = loginAndGetToken("test@email.com");
        memberToken = loginAndGetToken("member@email.com");

        // 테스트 모임 생성
        classEntity = createTestClass(masterUser.getId());

        // 기본 스케줄과 요청 데이터 생성
        scheduleEntity = createTestSchedule(classEntity);
        scheduleRequest = createScheduleRequest(classEntity.getId());
    }

    @Test
    @Order(1)
    @DisplayName("모임 일정 생성 - 모든 멤버의 체크인도 함께 생성")
    void createSchedule() throws Exception {
        // given
        addMemberToClass(memberUser, classEntity);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/schedules/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleRequest))
                .header("Authorization", masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.meetingTitle").value("테스트 일정"));

        // 생성된 일정의 ID 추출
        String response = result.andReturn().getResponse().getContentAsString();
        Long scheduleId = JsonPath.parse(response)
                .read("$.data.scheduleId", Long.class);

        // 생성된 일정 조회
        SchedulesEntity savedSchedule = schedulesRepository.findById(scheduleId)
                .orElseThrow();
    }

    @Test
    @Order(2)
    @DisplayName("모임 일정 생성 실패 - 유효성 검증 실패")
    void createSchedule_ValidationFail() throws Exception {
        // given
        SchedulesRequestDto.CreateRequest request = SchedulesRequestDto.CreateRequest.builder()
                .classId(classEntity.getId())
                .meetingTime("2025-02-05") // 잘못된 형식
                .meetingTitle("테") // 2글자 미만
                .meetingPlace("테스트 장소")
                .lat(37.5665)
                .lng(126.9780)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/schedules/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(ACCESS_TOKEN_HEADER, masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("모임 전체 일정 조회")
    void getSchedulesList() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/schedules/classes/" + classEntity.getId())
                .header(ACCESS_TOKEN_HEADER, masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].meetingTitle").value("테스트 일정"));
    }

    @Test
    @Order(4)
    @DisplayName("모임 특정 일정 조회")
    void getScheduleDetail() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/schedules/" + scheduleEntity.getId() + "/classes/" + classEntity.getId())
                .header(ACCESS_TOKEN_HEADER, masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetingTitle").value("테스트 일정"));
    }

    @Test
    @Order(5)
    @DisplayName("모임 일정 생성 실패 - 모임장이 아닌 멤버")
    void createSchedule_NotMaster() throws Exception {
        // given
        addMemberToClass(memberUser, classEntity);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/schedules/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleRequest))
                .header(ACCESS_TOKEN_HEADER, memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    @DisplayName("모임 전체 일정 조회 - 모임 멤버로 조회 성공")
    void getSchedulesList_AsMember() throws Exception {
        // given
        addMemberToClass(memberUser, classEntity);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/schedules/classes/" + classEntity.getId())
                .header(ACCESS_TOKEN_HEADER, memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].meetingTitle").value("테스트 일정"));
    }

    @Test
    @Order(7)
    @DisplayName("모임 특정 일정 조회 - 모임 멤버로 조회 성공")
    void getScheduleDetail_AsMember() throws Exception {
        // given
        addMemberToClass(memberUser, classEntity);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/schedules/" + scheduleEntity.getId() + "/classes/" + classEntity.getId())
                .header(ACCESS_TOKEN_HEADER, memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetingTitle").value("테스트 일정"));
    }

    // 공통 메서드
    private String loginAndGetToken(String email) throws Exception {
        AuthenticationRequest.Login request = new AuthenticationRequest.Login(email, SchedulesBaseTest.TEST_PASSWORD);
        ResultActions result = mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        return result.andReturn().getResponse().getHeader(ACCESS_TOKEN_HEADER);
    }

    @Test
    @Order(8)
    @DisplayName("모임 일정 수정 - 모임장 권한으로 성공")
    void modifySchedule() throws Exception {
        // given
        SchedulesRequestDto.UpdateRequest updateRequest = SchedulesRequestDto.UpdateRequest.builder()
                .meetingTime(getTestMeetingTime())
                .meetingTitle("수정된 테스트 일정")
                .meetingPlace("수정된 테스트 장소")
                .lat(37.5665)
                .lng(126.9780)
                .build();

        // when
        ResultActions result = mockMvc.perform(put("/api/v1/schedules/{scheduleId}/classes/{classId}",
                scheduleEntity.getId(),
                classEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .header(ACCESS_TOKEN_HEADER, masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.meetingTitle").value("수정된 테스트 일정"));
    }

    @Test
    @Order(9)
    @DisplayName("모임 일정 수정 실패 - 권한 없는 멤버")
    void modifySchedule_NotMaster() throws Exception {
        // given
        addMemberToClass(memberUser, classEntity);
        SchedulesRequestDto.UpdateRequest updateRequest = SchedulesRequestDto.UpdateRequest.builder()
                .meetingTime(getTestMeetingTime())
                .meetingTitle("수정된 테스트 일정")
                .meetingPlace("수정된 테스트 장소")
                .lat(37.5665)
                .lng(126.9780)
                .build();

        // when
        ResultActions result = mockMvc.perform(put("/api/v1/schedules/{scheduleId}/classes/{classId}",
                scheduleEntity.getId(),
                classEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .header(ACCESS_TOKEN_HEADER, memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    @DisplayName("모임 일정 삭제 - 모임장 권한으로 성공")
    void deleteSchedule() throws Exception {
        // when
        ResultActions result = mockMvc.perform(delete("/api/v1/schedules/{scheduleId}/classes/{classId}",
                scheduleEntity.getId(),
                classEntity.getId())
                .header(ACCESS_TOKEN_HEADER, masterToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @Order(12)
    @DisplayName("모임 일정 삭제 실패 - 권한 없는 멤버")
    void deleteSchedule_NotMaster() throws Exception {
        // given
        addMemberToClass(memberUser, classEntity);

        // when
        ResultActions result = mockMvc.perform(delete("/api/v1/schedules/{scheduleId}/classes/{classId}",
                scheduleEntity.getId(),
                classEntity.getId())
                .header(ACCESS_TOKEN_HEADER, memberToken));

        // then
        result.andDo(print())
                .andExpect(status().isForbidden());
    }
}