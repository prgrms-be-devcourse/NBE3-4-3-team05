package z9.hobby.domain.search.service;

import org.junit.jupiter.api.*;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.search.SortBy;
import z9.hobby.domain.search.base.SearchBaseTest;
import z9.hobby.domain.search.dto.SearchResponseDto;
import z9.hobby.model.user.User;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchServiceTest extends SearchBaseTest {
    private User user;
    private ClassEntity class1;
    private ClassEntity class2;

    @BeforeEach
    void setUp() throws InterruptedException {
        // 1. 테스트 유저 생성
        user = createTestUser("test@test.com", "테스터");

        // 2. 관심사 생성
        createTestFavorite("운동");
        createTestFavorite("음악");

        // 3. 테스트 모임 생성 (시간차를 두어 생성)
        class1 = createTestClass(user.getId(), "운동");
        Thread.sleep(100);
        class2 = createTestClass(user.getId(), "음악");
    }

    @Test
    @Order(1)
    @DisplayName("1. 로그인 상태 - 관심사 기반 정렬")
    void searchByFavoriteWithLogin() {
        // given
        addUserFavorite(user.getId(), "운동");

        // when
        List<SearchResponseDto> result = searchService.searchClasses(SortBy.FAVORITE, user.getId());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getFavorite()).isEqualTo("운동");
    }

    @Test
    @Order(2)
    @DisplayName("2. 비로그인 상태 - 관심사별 정렬")
    void searchByFavoriteWithoutLogin() {
        // when
        List<SearchResponseDto> result = searchService.searchClasses(SortBy.FAVORITE, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isSortedAccordingTo(Comparator.comparing(SearchResponseDto::getFavorite));
    }

    @Test
    @Order(3)
    @DisplayName("3. 가나다순 정렬")
    void searchByName() {
        // when
        List<SearchResponseDto> result = searchService.searchClasses(SortBy.NAME_ASC, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isSortedAccordingTo(Comparator.comparing(SearchResponseDto::getName));
    }

    @Test
    @Order(4)
    @DisplayName("4. 참여인원순 정렬")
    void searchByParticipants() {
        // given
        User member1 = createTestUser("member1@test.com", "멤버1");
        User member2 = createTestUser("member2@test.com", "멤버2");
        addMemberToClass(member1, class1);  // class1: 2명
        addMemberToClass(member2, class1);
        addMemberToClass(member1, class2);  // class2: 1명

        // when
        List<SearchResponseDto> result = searchService.searchClasses(SortBy.PARTICIPANT_DESC, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("id")
                .containsExactly(class1.getId(), class2.getId());
    }

    @Test
    @Order(5)
    @DisplayName("5. 최근등록순 정렬")
    void searchByNewest() {
        // when
        List<SearchResponseDto> result = searchService.searchClasses(SortBy.CREATED_DESC, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("id")
                .containsExactly(class2.getId(), class1.getId());
    }

    @Test
    @Order(6)
    @DisplayName("6. 오래된순 정렬")
    void searchByOldest() {
        // when
        List<SearchResponseDto> result = searchService.searchClasses(SortBy.CREATED_ASC, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("id")
                .containsExactly(class1.getId(), class2.getId());
    }
}
