package z9.hobby.domain.user.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.domain.user.dto.UserRequest;
import z9.hobby.global.response.SuccessCode;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.integration.security.WithCustomUser;
import z9.hobby.model.checkIn.CheckInEntity;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;
import z9.hobby.model.user.UserRole;
import z9.hobby.model.user.UserType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class UserControllerTest extends SpringBootTestSupporter {

    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1").executeUpdate();
    }

    @WithCustomUser
    @DisplayName("로그인 정보로 회원 정보를 반환합니다.")
    @Test
    void findUserInfo() throws Exception {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        List<String> favoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/users"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.FIND_USER_INFO_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_USER_INFO_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.FIND_USER_INFO_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.nickname").value(saveUser.getNickname()))
                .andExpect(jsonPath("$.data.type").value(UserType.NORMAL.getValue()))
                .andExpect(jsonPath("$.data.role").value(UserRole.ROLE_USER.getValue()))
                .andExpect(jsonPath("$.data.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
                .andExpect(jsonPath("$.data.favorite").isArray())
                .andExpect(jsonPath("$.data.favorite.length()").value(2))
                .andExpect(jsonPath("$.data.favorite").value(Matchers.containsInAnyOrder(favoriteNameList.toArray())));
    }

    @WithCustomUser
    @DisplayName("회원의 정보를 수정 합니다. 관심사와 닉네임을 수정할 수 있습니다.")
    @Test
    void modifyUserInfo() throws Exception {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        List<String> favoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        String changeNickname = "변경된닉네임";
        UserRequest.PatchUserInfo request = UserRequest.PatchUserInfo.of(changeNickname, favoriteNameList);

        // when
        ResultActions result = mockMvc.perform(patch("/api/v1/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.PATCH_USER_INFO_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PATCH_USER_INFO_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.PATCH_USER_INFO_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.nickname").doesNotExist());
    }

    @WithCustomUser
    @DisplayName("로그인 된 회원의 참석할 모임 정보를 모두 찾습니다.")
    @Test
    void findUserSchedules() throws Exception {
        // given
        //사용자 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();
        List<String> favoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

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
        ResultActions result = mockMvc.perform(get("/api/v1/users/schedules"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.FIND_USER_SCHEDULES_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_USER_SCHEDULES_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.FIND_USER_SCHEDULES_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.schedule").isArray())
                .andExpect(jsonPath("$.data.schedule.length()").value(1))
                .andExpect(jsonPath("$.data.schedule[0].classId").isNotEmpty())
                .andExpect(jsonPath("$.data.schedule[0].meetingTime").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
                .andExpect(jsonPath("$.data.schedule[0].meetingTitle").value(saveSchedule.getMeetingTitle()));
    }

    @WithCustomUser
    @DisplayName("로그인 한 사용자의 모임방을 전체 조회 합니다.")
    @Test
    void findUserClasses() throws Exception {
        // given
        // 회원 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(1);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // 모임 등록
        List<ClassEntity> saveClassList =
                classFactory.saveAndCreateClassData(2, saveUser, saveFavorite);
        ClassEntity saveClass = saveClassList.getFirst();

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/users/classes"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.FIND_USER_CLASSES_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_USER_CLASSES_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.FIND_USER_CLASSES_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.classInfo").isArray())
                .andExpect(jsonPath("$.data.classInfo[0].name").value(saveClassList.get(0).getName()))
                .andExpect(jsonPath("$.data.classInfo[0].description").value(saveClassList.get(0).getDescription()))
                .andExpect(jsonPath("$.data.classInfo[0].favorite").value(saveClassList.get(0).getFavorite()))
                .andExpect(jsonPath("$.data.classInfo[1].name").value(saveClassList.get(1).getName()))
                .andExpect(jsonPath("$.data.classInfo[1].description").value(saveClassList.get(1).getDescription()))
                .andExpect(jsonPath("$.data.classInfo[1].favorite").value(saveClassList.get(1).getFavorite()));
    }
}