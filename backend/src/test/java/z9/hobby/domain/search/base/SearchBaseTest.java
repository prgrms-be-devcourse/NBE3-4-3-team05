package z9.hobby.domain.search.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.entity.ClassUserEntity;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.domain.search.service.SearchService;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.model.user.User;
import z9.hobby.model.userfavorite.UserFavorite;

@Transactional
public abstract class SearchBaseTest extends SpringBootTestSupporter {
    protected static final String TEST_PASSWORD = "!test1234";

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected SearchService searchService;

    // 테스트 유저 생성
    protected User createTestUser(String email, String nickname) {
        return userRepository.save(User.createNewUser(
                email,
                passwordEncoder.encode(TEST_PASSWORD),
                nickname
        ));
    }

    // 테스트 모임 생성
    protected ClassEntity createTestClass(Long masterId, String favorite) {
        return classRepository.save(ClassEntity.builder()
                .masterId(masterId)
                .name("테스트 모임")
                .favorite(favorite)
                .description("테스트 모임입니다")
                .build());
    }

    // 관심사 생성
    protected FavoriteEntity createTestFavorite(String name) {
        return favoriteRepository.save(FavoriteEntity.builder()
                .name(name)
                .build());
    }

    // 사용자에게 관심사 추가
    protected void addUserFavorite(Long userId, String favoriteName) {
        User user = userRepository.findById(userId).orElseThrow();
        FavoriteEntity favorite = favoriteRepository.findByName(favoriteName).orElseThrow();
        userFavoriteRepository.save(UserFavorite.createNewUserFavorite(user, favorite));
    }

    // 모임에 멤버 추가
    protected void addMemberToClass(User member, ClassEntity classEntity) {
        classUserRepository.save(ClassUserEntity.builder()
                .classes(classEntity)
                .userId(member.getId())
                .build());
    }
}