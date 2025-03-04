package z9.hobby.domain.schedules.base;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.entity.ClassUserEntity;
import z9.hobby.domain.schedules.dto.SchedulesRequestDto;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Transactional
public abstract class SchedulesBaseTest extends SpringBootTestSupporter {
    // 공통으로 사용되는 상수
    protected static final String TEST_PASSWORD = "!test1234";
    protected static final String TEST_MEETING_TITLE = "테스트 일정";

    // 고정된 시간 대신 현재 시간 기준 미래 시간 생성
    protected String getTestMeetingTime() {
        // 현재 시간으로부터 7일 후로 설정
        LocalDate futureTime = LocalDate.now().plusDays(7);
        return futureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    protected User createTestUser(String email, String nickname) {
        return userRepository.save(User.createNewUser(
                email,
                passwordEncoder.encode(TEST_PASSWORD),
                nickname
        ));
    }

    protected ClassEntity createTestClass(Long masterId) {
        return classRepository.save(ClassEntity.builder()
                .masterId(masterId)
                .name("테스트 모임")
                .favorite("취미")
                .description("테스트 모임입니다")
                .build());
    }

    protected SchedulesEntity createTestSchedule(ClassEntity classEntity) {
        return schedulesRepository.save(SchedulesEntity.builder()
                .classes(classEntity)
                .meetingTime(getTestMeetingTime())
                .meetingTitle(SchedulesBaseTest.TEST_MEETING_TITLE)
                .build());
    }

    // 멤버 추가 공통 메서드
    protected void addMemberToClass(User member, ClassEntity classEntity) {
        ReflectionTestUtils.setField(classEntity, "users", new ArrayList<>());
        ClassUserEntity classUser = ClassUserEntity.builder()
                .classes(classEntity)
                .userId(member.getId())
                .build();
        classUserRepository.save(classUser);
        classEntity.getUsers().add(classUser);
    }

    protected SchedulesRequestDto.CreateRequest createScheduleRequest(Long classId) {
        return SchedulesRequestDto.CreateRequest.builder()
                .classId(classId)
                .meetingTime(getTestMeetingTime())
                .meetingTitle(TEST_MEETING_TITLE)
                .build();
    }
}
