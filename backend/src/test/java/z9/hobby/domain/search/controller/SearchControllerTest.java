package z9.hobby.domain.search.controller;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.authentication.dto.AuthenticationRequest;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.search.base.SearchBaseTest;
import z9.hobby.model.user.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchControllerTest extends SearchBaseTest {
    private User user;
    private String token;
    private ClassEntity class1;
    private ClassEntity class2;

    @BeforeEach
    void setUp() throws Exception {
        // 1. 테스트 유저 생성
        user = createTestUser("test@test.com", "테스터");

        // 2. 관심사 생성
        createTestFavorite("운동");
        createTestFavorite("음악");

        // 3. 로그인 토큰 발급
        token = getLoginToken("test@test.com");

        // 4. 테스트 모임 생성
        class1 = createTestClass(user.getId(), "운동");
        class2 = createTestClass(user.getId(), "음악");
    }

    @Test
    @Order(1)
    @DisplayName("1. 로그인 상태 - 관심사 기반 정렬")
    void searchByFavoriteWithLogin() throws Exception {
        // given
        addUserFavorite(user.getId(), "운동");

        // when
        mockMvc.perform(get("/api/v1/search/classes")
                        .param("sortBy", "FAVORITE")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].favorite").value("운동"));
    }

    @Test
    @Order(2)
    @DisplayName("2. 비로그인 상태 - 관심사별 정렬")
    void searchByFavoriteWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/v1/search/classes")
                        .param("sortBy", "FAVORITE"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("3. 가나다순 정렬")
    void searchByName() throws Exception {
        mockMvc.perform(get("/api/v1/search/classes")
                        .param("sortBy", "NAME_ASC"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("4. 참여인원순 정렬")
    void searchByParticipants() throws Exception {
        // given : 참여 인원 추가
        User member1 = createTestUser("member1@test.com", "멤버1");
        User member2 = createTestUser("member2@test.com", "멤버2");
        addMemberToClass(member1, class1);  // class1: 2명
        addMemberToClass(member2, class1);
        addMemberToClass(member1, class2);  // class2: 1명

        // when & then
        mockMvc.perform(get("/api/v1/search/classes")
                        .param("sortBy", "PARTICIPANT_DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(class1.getId()))
                .andExpect(jsonPath("$.data[1].id").value(class2.getId()));
    }

    @Test
    @Order(5)
    @DisplayName("5. 최근등록순 정렬")
    void searchByNewest() throws Exception {
        mockMvc.perform(get("/api/v1/search/classes")
                        .param("sortBy", "CREATED_DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(class2.getId()))
                .andExpect(jsonPath("$.data[1].id").value(class1.getId()));
    }

    @Test
    @Order(6)
    @DisplayName("6. 오래된순 정렬")
    void searchByOldest() throws Exception {
        mockMvc.perform(get("/api/v1/search/classes")
                        .param("sortBy", "CREATED_ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(class1.getId()))
                .andExpect(jsonPath("$.data[1].id").value(class2.getId()));
    }

    // 로그인 토큰 발급 메서드
    private String getLoginToken(String email) throws Exception {
        AuthenticationRequest.Login request = AuthenticationRequest.Login.of(email, TEST_PASSWORD);
        ResultActions result = mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        return result.andReturn().getResponse().getHeader("Authorization");
    }
}
