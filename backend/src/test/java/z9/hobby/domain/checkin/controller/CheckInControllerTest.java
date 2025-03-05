package z9.hobby.domain.checkin.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.checkin.dto.CheckInRequestDto;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.global.response.SuccessCode;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.integration.security.WithCustomUser;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;

@Transactional
class CheckInControllerTest extends SpringBootTestSupporter {

    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1").executeUpdate();
    }

    @WithCustomUser
    @DisplayName("회원의 참여중인 스케줄에 대한 참석 여부를 결정합니다.")
    @Test
    void createCheckIn() throws Exception {
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

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CHECK_IN_CREATE_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CHECK_IN_CREATE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CHECK_IN_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @WithCustomUser
    @DisplayName("로그인 한 사용자의 모임방을 전체 조회 합니다.")
    @Test
    void updateCheckIn() throws Exception {
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

        // 체크인 등록
        checkInFactory.saveAndCreateCheckInData(1, saveSchedule, saveUser, List.of(true));

        CheckInRequestDto request = new CheckInRequestDto(
                saveSchedule.getId(), false);

        // when
        ResultActions result = mockMvc.perform(put("/api/v1/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CHECK_IN_UPDATE_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CHECK_IN_UPDATE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CHECK_IN_UPDATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @WithCustomUser
    @DisplayName("모임 인원 투표 현황 보여주기")
    @Test
    void getAllCheckInsForSchedule() throws Exception {
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
        List<SchedulesEntity> saveSchedulesList = schedulesFactory.saveAndCreateClassData(5, saveClass);
        SchedulesEntity saveSchedule = saveSchedulesList.getFirst();

        // 체크인 등록
        checkInFactory.saveAndCreateCheckInData(1, saveSchedule, saveUser, List.of(true));

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/checkin/{scheduleId}", saveSchedule.getId()));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CHECK_IN_READ_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CHECK_IN_READ_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CHECK_IN_READ_SUCCESS.getCode()));
    }

    @WithCustomUser
    @DisplayName("모임 내 투표 현황 보여주기")
    @Test
    void getMyCheckIn() throws Exception {
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

        // 체크인 등록
        checkInFactory.saveAndCreateCheckInData(1, saveSchedule, saveUser, List.of(true));

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/checkin/{scheduleId}/my", saveSchedule.getId()));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.CHECK_IN_READ_SUCCESS.isSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CHECK_IN_READ_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.CHECK_IN_READ_SUCCESS.getCode()));
    }

}