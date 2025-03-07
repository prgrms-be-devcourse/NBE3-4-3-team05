package z9.hobby.domain.checkin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.checkin.dto.CheckInRequestDto;
import z9.hobby.domain.checkin.dto.CheckInResponseDto;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;

@Transactional
class CheckInServiceImplTest extends SpringBootTestSupporter {

    @DisplayName("일정에 대한 참석 여부를 생성합니다. 이미 참석 여부가 등록되었다면 오류 메세지를 서빙합니다.")
    @Test
    void createCheckIn() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        // 모임 등록
        List<ClassEntity> saveClassList = classFactory.saveAndCreateClassData(1, saveUser, saveFavorite);
        ClassEntity saveClass = saveClassList.getFirst();

        // 일정 등록
        List<SchedulesEntity> saveSchedulesList = schedulesFactory.saveAndCreateClassData(1, saveClass);
        SchedulesEntity saveSchedule = saveSchedulesList.getFirst();

        CheckInRequestDto request = new CheckInRequestDto(
                saveSchedule.getId(), true);

        // 체크인 등록
        checkInFactory.saveAndCreateCheckInData(1, saveSchedule, saveUser, List.of(true));

        // when // then
        assertThatThrownBy(() ->  checkInService.createCheckIn(saveUser.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CHECK_IN_ALREADY_EXISTS);
    }

    @DisplayName("일정에 대한 참석 여부를 생성합니다. 모임에 가입된 사용자가 아니라면 참석여부를 등록할 수 없습니다.")
    @Test
    void createCheckIn2() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(2);
        User saveUser = saveUserList.getFirst();
        User saveUser2 = saveUserList.get(1);

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        // 모임 등록
        // 1번 사용자만 모임에 가입됨
        List<ClassEntity> saveClassList = classFactory.saveAndCreateClassData(1, saveUser, saveFavorite);
        ClassEntity saveClass = saveClassList.getFirst();

        // 일정 등록
        List<SchedulesEntity> saveSchedulesList = schedulesFactory.saveAndCreateClassData(1, saveClass);
        SchedulesEntity saveSchedule = saveSchedulesList.getFirst();

        CheckInRequestDto request = new CheckInRequestDto(
                saveSchedule.getId(), true);

        // 체크인 등록
        checkInFactory.saveAndCreateCheckInData(1, saveSchedule, saveUser, List.of(true));

        // when // then
        assertThatThrownBy(() ->  checkInService.createCheckIn(saveUser2.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_NOT_EXISTS_MEMBER);
    }

    @DisplayName("모임 인원 투표 현황 보여주기. 스케줄 정보가 없다면, 오류 메세지를 서빙합니다.")
    @Test
    void getAllCheckIns() {
        // given

        // when // then
        assertThatThrownBy(() -> checkInService.getAllCheckIns(1L))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.SCHEDULE_NOT_FOUND);
    }

    @DisplayName("모임 내 투표 현황 보여주기. 만약 등록된 체크인이 없다면 등록되어있지 않는 응답이 발생합니다.")
    @Test
    void getMyCheckIn() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(2);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        // 모임 등록
        List<ClassEntity> saveClassList = classFactory.saveAndCreateClassData(1, saveUser, saveFavorite);
        ClassEntity saveClass = saveClassList.getFirst();

        // 일정 등록
        List<SchedulesEntity> saveSchedulesList = schedulesFactory.saveAndCreateClassData(1, saveClass);
        SchedulesEntity saveSchedule = saveSchedulesList.getFirst();

        // when
        CheckInResponseDto result = checkInService.getMyCheckIn(saveSchedule.getId(), saveUser.getId());

        // then
        assertThat(result.getCheckIn()).isFalse();
    }
}