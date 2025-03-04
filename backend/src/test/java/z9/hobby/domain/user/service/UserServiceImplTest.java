package z9.hobby.domain.user.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.domain.user.dto.UserRequest;
import z9.hobby.domain.user.dto.UserResponse;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.model.checkIn.CheckInEntity;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;
import z9.hobby.model.user.UserRole;
import z9.hobby.model.user.UserType;

@Transactional
class UserServiceImplTest extends SpringBootTestSupporter {

    @DisplayName("회원 정보를 찾습니다.")
    @Test
    void findUserInfo() {
        // given
        //사용자 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        List<String> saveFavoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        // when
        UserResponse.UserInfo findData = userService.findUserInfo(saveUser.getId());

        // then
        assertThat(findData)
                .extracting("nickname", "type", "role")
                .containsExactly(saveUser.getNickname(), UserType.NORMAL.getValue(), UserRole.ROLE_USER.getValue());
        assertThat(findData.getCreatedAt()).matches("\\d{4}-\\d{2}-\\d{2}");
        assertThat(findData.getFavorite())
                .hasSize(2)
                .containsAll(saveFavoriteNameList);
    }

    @DisplayName("회원 정보를 찾습니다. 관심사가 없을 경우, 빈 배열이 출력되어야 합니다.")
    @Test
    void findUserInfo2() {
        // given
        //사용자 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // when
        UserResponse.UserInfo findData = userService.findUserInfo(saveUser.getId());

        // then
        assertThat(findData)
                .extracting("nickname", "type", "role")
                .containsExactly(saveUser.getNickname(), UserType.NORMAL.getValue(), UserRole.ROLE_USER.getValue());
        assertThat(findData.getCreatedAt()).matches("\\d{4}-\\d{2}-\\d{2}");
        assertThat(findData.getFavorite())
                .hasSize(0);
    }

    @DisplayName("회원 정보를 찾습니다. 없는 회원으로 조회 시, 오류 메세지가 서빙됩니다.")
    @Test
    void findUserInfo3() {
        // given

        // when // then
        assertThatThrownBy(() -> userService.findUserInfo(1L))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("회원 정보를 수정 합니다. 닉네임을 변경할 수 있습니다.")
    @Test
    void patchUserInfo1() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        List<String> saveFavoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        String changeNickname = "변경된닉네임";
        UserRequest.PatchUserInfo request = UserRequest.PatchUserInfo.of(changeNickname, saveFavoriteNameList);

        // when
        userService.patchUserInfo(request, saveUser.getId());

        // then
        Optional<User> findUserOptional = userRepository.findById(saveUser.getId());
        assertThat(findUserOptional).isPresent();
        User findUser = findUserOptional.get();
        List<String> findFavoriteList = userFavoriteRepository.findFavoriteNamesByUserId(saveUser.getId());
        assertThat(findUser)
                .extracting("nickname")
                .isEqualTo(changeNickname);
        assertThat(findFavoriteList)
                .hasSize(2)
                .containsAll(saveFavoriteNameList);
    }

    @DisplayName("회원 정보를 수정 합니다. 원래 있던 관심사가 입력되지 않으면 그전 데이터는 삭제 됩니다.")
    @Test
    void patchUserInfo2() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        String changeNickname = "변경된닉네임";
        UserRequest.PatchUserInfo request = UserRequest.PatchUserInfo.of(changeNickname, List.of(saveFavorite.getName()));

        // when
        userService.patchUserInfo(request, saveUser.getId());

        // then
        Optional<User> findUserOptional = userRepository.findById(saveUser.getId());
        assertThat(findUserOptional).isPresent();
        User findUser = findUserOptional.get();

        List<String> findFavoriteList = userFavoriteRepository.findFavoriteNamesByUserId(saveUser.getId());

        assertThat(findUser)
                .extracting("nickname")
                .isEqualTo(changeNickname);
        assertThat(findFavoriteList)
                .hasSize(1)
                .containsExactlyInAnyOrder(request.getFavorite().getFirst());
    }

    @DisplayName("회원 정보를 수정 합니다. 서버에 등록된 관심사가 아니라면, 오류가 발생합니다.")
    @Test
    void patchUserInfo3() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        String changeNickname = "변경된닉네임";
        UserRequest.PatchUserInfo request =
                UserRequest.PatchUserInfo.of(changeNickname, List.of("미등록 관심사"));

        // when // then
        assertThatThrownBy( () -> userService.patchUserInfo(request, saveUser.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.NOT_EXIST_FAVORITE);
    }

    @DisplayName("회원이 참석 예정인 일정을 찾습니다.")
    @Test
    void findUserSchedules1() {
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
        UserResponse.UserSchedule findData = userService.findUserSchedules(saveUser.getId());

        // then
        assertThat(findData.getSchedule())
                .hasSize(1);
        assertThat(findData.getSchedule().getFirst())
                .extracting("meetingTitle")
                .isEqualTo(saveSchedule.getMeetingTitle());
    }

    @DisplayName("로그인 한 회원의 모든 모임을 조회 합니다.")
    @Test
    void findUserClasses1() {
        // given
        // 회원 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(1);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // 모임 등록
        List<ClassEntity> saveClassList =
                classFactory.saveAndCreateClassData(1, saveUser, saveFavorite);
        ClassEntity saveClass = saveClassList.getFirst();

        // when
        UserResponse.UserClass findData = userService.findUserClasses(saveUser.getId());

        // then
        assertThat(findData.getClassInfo()).hasSize(1);
        assertThat(findData.getClassInfo().getFirst())
                .extracting("name", "description", "favorite")
                .containsExactly(saveClass.getName(), saveClass.getDescription(), saveClass.getFavorite());
    }
}