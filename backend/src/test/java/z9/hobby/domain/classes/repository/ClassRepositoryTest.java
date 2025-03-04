package z9.hobby.domain.classes.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.model.user.User;

@Transactional
class ClassRepositoryTest extends SpringBootTestSupporter {

    @DisplayName("회원이 가입된 모든 모임을 조회 합니다.")
    @Test
    void findByUserId1() {
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

        // when
        List<ClassEntity> findData = classRepository.findByUserId(saveUser.getId());

        // then
        assertThat(findData).hasSize(2);
        assertThat(findData)
                .usingRecursiveFieldByFieldElementComparatorOnFields("name", "favorite", "description", "masterId")
                .containsExactlyInAnyOrderElementsOf(saveClassList);
    }

    @DisplayName("회원이 가입된 모든 모임을 조회 합니다. 없다면 빈 list 가 반환 됩니다.")
    @Test
    void findByUserId2() {
        // given
        // 회원 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(1);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        // when
        List<ClassEntity> findData = classRepository.findByUserId(saveUser.getId());

        // then
        assertThat(findData).hasSize(0);
    }
}