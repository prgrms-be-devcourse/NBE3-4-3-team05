package z9.hobby.domain.schedules.service;

import org.junit.jupiter.api.*;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.schedules.base.SchedulesBaseTest;
import z9.hobby.domain.schedules.dto.SchedulesRequestDto;
import z9.hobby.domain.schedules.dto.SchedulesResponseDto;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SchedulesServiceTest extends SchedulesBaseTest {
    private User masterUser;
    private User memberUser;
    private ClassEntity classEntity;
    private SchedulesEntity scheduleEntity;
    private SchedulesRequestDto.CreateRequest scheduleRequest;

    @BeforeEach
    void setUp() {
        // 마스터 유저와 멤버 유저 생성
        masterUser = createTestUser("master@email.com", "모임장");
        memberUser = createTestUser("member@email.com", "멤버");

        // 테스트 모임 생성 (마스터 기준)
        classEntity = createTestClass(masterUser.getId());

        // 테스트 스케줄 생성
        scheduleEntity = createTestSchedule(classEntity);

        // 요청 데이터 생성
        scheduleRequest = createScheduleRequest(classEntity.getId());
    }

    @Test
    @Order(1)
    @DisplayName("일정 생성 - 모든 멤버의 체크인도 함께 생성")
    void create() {
        // given
        addMemberToClass(memberUser, classEntity);

        // when
        SchedulesResponseDto.ResponseData response = schedulesService.create(scheduleRequest, masterUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMeetingTitle()).isEqualTo(TEST_MEETING_TITLE);

        // 생성된 일정 조회
        SchedulesEntity savedSchedule = schedulesRepository.findById(response.getScheduleId())
                .orElseThrow();
    }

    @DisplayName("일정 생성 실패 - 존재하지 않는 모임")
    @Test
    @Order(2)
    void create_ClassNotFound() {
        // given
        String futureDate = LocalDate.now().plusDays(7)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        SchedulesRequestDto.CreateRequest request = SchedulesRequestDto.CreateRequest.builder()
                .classId(999L)
                .meetingTime(futureDate)  // 현재 날짜 + 7일
                .meetingTitle("테스트 일정")
                .meetingPlace("테스트 장소")
                .lat(37.5665)
                .lng(126.9780)
                .build();

        // when & then
        assertThatThrownBy(() -> schedulesService.create(request, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_NOT_FOUND);
    }

    @DisplayName("전체 일정 조회")
    @Test
    @Order(3)
    void getSchedulesList() {
        // when
        List<SchedulesResponseDto.ResponseData> schedules =
                schedulesService.getSchedulesList(classEntity.getId(), masterUser.getId());

        // then
        assertThat(schedules).hasSize(1);
        assertThat(schedules.getFirst().getMeetingTitle()).isEqualTo("테스트 일정");
    }

    @DisplayName("특정 일정 상세 조회")
    @Test
    @Order(4)
    void getScheduleDetail() {
        // when
        SchedulesResponseDto.ResponseData response =
                schedulesService.getScheduleDetail(scheduleEntity.getId(), classEntity.getId(), masterUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMeetingTitle()).isEqualTo("테스트 일정");
    }

    @DisplayName("일정 생성 실패 - 모임장이 아닌 멤버")
    @Test
    @Order(5)
    void create_NotMaster() {
        // given
        addMemberToClass(memberUser, classEntity);

        // when & then
        assertThatThrownBy(() -> schedulesService.create(scheduleRequest, memberUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_ACCESS_DENIED);
    }

    @DisplayName("전체 일정 조회 - 모임 멤버로 조회 성공")
    @Test
    @Order(6)
    void getSchedulesList_AsMember() {
        // given
        addMemberToClass(memberUser, classEntity);

        // when
        List<SchedulesResponseDto.ResponseData> schedules =
                schedulesService.getSchedulesList(classEntity.getId(), memberUser.getId());

        // then
        assertThat(schedules).hasSize(1);
        assertThat(schedules.getFirst().getMeetingTitle()).isEqualTo("테스트 일정");
    }

    @DisplayName("특정 일정 상세 조회 - 모임 멤버로 조회 성공")
    @Test
    @Order(7)
    void getScheduleDetail_AsMember() {
        // given
        addMemberToClass(memberUser, classEntity);

        // when
        SchedulesResponseDto.ResponseData response =
                schedulesService.getScheduleDetail(scheduleEntity.getId(), classEntity.getId(), memberUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMeetingTitle()).isEqualTo("테스트 일정");
    }

    @Test
    @Order(8)
    @DisplayName("일정 수정 성공 - 모임장 권한")
    void modify_Success() {
        // given
        String newMeetingTime = getTestMeetingTime();
        SchedulesRequestDto.UpdateRequest updateRequest = SchedulesRequestDto.UpdateRequest.builder()
                .meetingTime(newMeetingTime)
                .meetingTitle("수정된 테스트 일정")
                .meetingPlace("수정된 테스트 장소")
                .lat(37.5665)
                .lng(126.9780)
                .build();

        // when
        SchedulesResponseDto.ResponseData response =
                schedulesService.modify(scheduleEntity.getId(), classEntity.getId(), updateRequest, masterUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMeetingTitle()).isEqualTo("수정된 테스트 일정");
        assertThat(response.getMeetingTime()).isEqualTo(newMeetingTime);

        // DB에 실제로 수정되었는지 확인
        SchedulesEntity updatedSchedule = schedulesRepository.findById(scheduleEntity.getId()).orElseThrow();
        assertThat(updatedSchedule.getMeetingTitle()).isEqualTo("수정된 테스트 일정");
        assertThat(updatedSchedule.getMeetingTime()).isEqualTo(newMeetingTime);
    }

    @Test
    @Order(9)
    @DisplayName("일정 수정 실패 - 존재하지 않는 일정")
    void modify_ScheduleNotFound() {
        // given
        SchedulesRequestDto.UpdateRequest updateRequest = SchedulesRequestDto.UpdateRequest.builder()
                .meetingTime(getTestMeetingTime())
                .meetingTitle("수정된 테스트 일정")
                .meetingPlace("수정된 테스트 장소")
                .lat(37.5665)
                .lng(126.9780)
                .build();

        // when & then
        assertThatThrownBy(() ->
                schedulesService.modify(999L, classEntity.getId(), updateRequest, masterUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.SCHEDULE_NOT_FOUND);
    }

    @Test
    @Order(10)
    @DisplayName("일정 수정 실패 - 권한 없는 멤버")
    void modify_AccessDenied() {
        // given
        addMemberToClass(memberUser, classEntity);
        SchedulesRequestDto.UpdateRequest updateRequest = SchedulesRequestDto.UpdateRequest.builder()
                .meetingTime(getTestMeetingTime())
                .meetingTitle("수정된 테스트 일정")
                .meetingPlace("수정된 테스트 장소")
                .lat(37.5665)
                .lng(126.9780)
                .build();

        // when & then
        assertThatThrownBy(() ->
                schedulesService.modify(scheduleEntity.getId(), classEntity.getId(), updateRequest, memberUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_ACCESS_DENIED);
    }

    @Test
    @Order(11)
    @DisplayName("일정 삭제 성공 - 모임장 권한")
    void delete_Success() {
        // when
        schedulesService.delete(scheduleEntity.getId(), classEntity.getId(), masterUser.getId());

        // then
        // DB에서 실제로 삭제되었는지 확인
        assertThat(schedulesRepository.findById(scheduleEntity.getId())).isEmpty();
    }

    @Test
    @Order(12)
    @DisplayName("일정 삭제 실패 - 존재하지 않는 일정")
    void delete_ScheduleNotFound() {
        // when & then
        assertThatThrownBy(() ->
                schedulesService.delete(999L, classEntity.getId(), masterUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.SCHEDULE_NOT_FOUND);
    }

    @Test
    @Order(13)
    @DisplayName("일정 삭제 실패 - 권한 없는 멤버")
    void delete_AccessDenied() {
        // given
        addMemberToClass(memberUser, classEntity);

        // when & then
        assertThatThrownBy(() ->
                schedulesService.delete(scheduleEntity.getId(), classEntity.getId(), memberUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_ACCESS_DENIED);
    }
}