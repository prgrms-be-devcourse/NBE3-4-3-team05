package z9.hobby.model.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.integration.SpringBootTestSupporter;

@Transactional
class UserRepositoryTest extends SpringBootTestSupporter {

    @DisplayName("로그인 아이디로, 사용자 정보를 조회합니다.")
    @Test
    void findByLoginId() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // when
        Optional<User> findOptionalData = userRepository.findByLoginId(saveUser.getLoginId());

        // then
        assertThat(findOptionalData).isPresent();
        User findData = findOptionalData.get();
        assertThat(findData)
                .extracting("loginId", "nickname", "status", "role", "id")
                .containsExactly(
                        saveUser.getLoginId(), saveUser.getNickname(), UserStatus.ACTIVE, UserRole.ROLE_USER, saveUser.getId());
    }

    @DisplayName("로그인 아이디와 닉네임으로 사용자 정보를 조회합니다.")
    @Test
    void findByLoginIdOrNickname1() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // when
        Optional<User> findOptionalData =
                userRepository.findByLoginIdOrNickname(saveUser.getLoginId(), saveUser.getNickname());

        // then
        assertThat(findOptionalData).isPresent();
        User findData = findOptionalData.get();
        assertThat(findData)
                .extracting("loginId", "nickname")
                .containsExactly(saveUser.getLoginId(), saveUser.getNickname());
    }

    @DisplayName("로그인 아이디와 닉네임으로 사용자 정보를 조회합니다. 없으면 Optional.null 반환됩니다.")
    @Test
    void findByLoginIdOrNickname2() {
        // given
        String loginId = "test@email.com";
        String nickname ="테스터";

        // when
        Optional<User> findOptionalData = userRepository.findByLoginIdOrNickname(loginId, nickname);

        // then
        assertThat(findOptionalData).isEmpty();
    }
}