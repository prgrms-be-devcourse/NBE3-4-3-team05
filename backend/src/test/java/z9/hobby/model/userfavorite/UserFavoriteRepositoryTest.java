package z9.hobby.model.userfavorite;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.model.user.User;

@Transactional
class UserFavoriteRepositoryTest extends SpringBootTestSupporter {

    @DisplayName("회원 아이디로, 관심사 목록을 조회합니다.")
    @Test
    void findFavoriteNamesByUserId1() {
        // given
        //사용자 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        List<String> favoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

        // 회원-관심사 등록
        userFactory.saveUserFavorite(saveUser, saveFavoriteList);

        // when
        List<String> findDataList = userFavoriteRepository.findFavoriteNamesByUserId(saveUser.getId());

        // then
        assertThat(findDataList).hasSize(2);
        assertThat(findDataList).containsAll(favoriteNameList);
    }
}