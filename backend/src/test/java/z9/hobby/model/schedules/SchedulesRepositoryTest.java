package z9.hobby.model.schedules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.model.checkIn.CheckInEntity;
import z9.hobby.model.user.User;

@Transactional
class SchedulesRepositoryTest extends SpringBootTestSupporter {

    @DisplayName("회원 아이디로, 회원이 참석하기로 한 모임 일정을 찾습니다.")
    @Test
    void findUserSchedulesInfoByUserId1() {
        // given
        //사용자 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        //방 생성
        List<ClassEntity> saveClassList =
                classFactory.saveAndCreateClassData(1, saveUser, saveFavorite);
        ClassEntity saveClass = saveClassList.getFirst();

        //스케줄 생성
        List<SchedulesEntity> saveSchedulesList =
                schedulesFactory.saveAndCreateClassData(2, saveClass);
        SchedulesEntity saveSchedule = saveSchedulesList.getFirst();

        //체크인 등록
        List<CheckInEntity> saveCheckInList =
                checkInFactory.saveAndCreateCheckInData(2, saveSchedule, saveUser, List.of(true, false));

        // when
        List<SchedulesEntity> findData = schedulesRepository.findUserSchedulesInfoByUserId(
                saveUser.getId());

        // then
        assertThat(findData).hasSize(1);
        assertThat(findData.getFirst())
                .extracting("meetingTitle")
                .isEqualTo(saveSchedule.getMeetingTitle());
    }

    @DisplayName("회원 아이디로, 회원이 참석하기로 한 모임 일정을 찾습니다. 없으면 빈 배열이 반환됩니다.")
    @Test
    void findUserSchedulesInfoByUserId2() {
        // given
        //사용자 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        //방 생성
        List<ClassEntity> saveClassList =
                classFactory.saveAndCreateClassData(1, saveUser, saveFavorite);
        ClassEntity saveClass = saveClassList.getFirst();

        // when
        List<SchedulesEntity> findData =
                schedulesRepository.findUserSchedulesInfoByUserId(saveUser.getId());

        // then
        assertThat(findData).hasSize(0);
    }
}