package z9.hobby.domain.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.authentication.dto.AuthenticationRequest;
import z9.hobby.domain.authentication.dto.AuthenticationResponse;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.integration.factory.UserFactory;
import z9.hobby.model.user.User;
import z9.hobby.model.user.UserRole;
import z9.hobby.model.user.UserStatus;
import z9.hobby.model.user.UserType;

@Transactional
class AuthenticationServiceImplTest extends SpringBootTestSupporter {

    @DisplayName("로그인 아이디로, 로그인을 진행 합니다. 성공되면, access/refresh token 을 반환합니다.")
    @Test
    void login() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // when
        AuthenticationResponse.UserToken userToken = authenticationService.login(
                AuthenticationRequest.Login.of(saveUser.getLoginId(), UserFactory.USER_LOGIN_PASSWORD));

        // then
        assertThat(userToken.getAccessToken())
                .isNotNull();
        assertThat(userToken.getRefreshToken())
                .isNotNull();
    }

    @DisplayName("로그인 아이디가 틀리거나, 등등 로그인이 불가능하다면 오류 메세지가 출력됩니다!")
    @Test
    void login2() {
        // given
        String loginId = "test@email.com";
        String password = "!test1234";

        // when // then
        assertThatThrownBy(() -> authenticationService.login(AuthenticationRequest.Login.of(loginId, password)))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.LOGIN_FAIL);
    }

    @DisplayName("탈퇴한 아이디로 로그인 시, 오류 메세지가 반환 됩니다.")
    @Test
    void login3() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();
        User resignUser = User.resign(saveUser);
        userRepository.save(resignUser);

        // when // then
        assertThatThrownBy(() -> authenticationService.login(AuthenticationRequest.Login.of(
                "test1@email.com", UserFactory.USER_LOGIN_PASSWORD)))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.LOGIN_RESIGN_USER);
    }

    @DisplayName("틀린 비밀번호가 입력되면, 오류 메세지가 반환 됩니다.")
    @Test
    void login4() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);

        // when // then
        assertThatThrownBy(() -> authenticationService.login(AuthenticationRequest.Login.of(
                "test1@email.com", "NOT_PASSWORD")))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.LOGIN_FAIL);
    }

    @DisplayName("회원가입을 진행합니다.")
    @Test
    void signup1() {
        // given
        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        List<String> saveFavoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

        String loginId = "test1@email.com";
        String password = "!test1234";
        List<String> favorite = saveFavoriteNameList;
        String nickname = "test1";
        AuthenticationRequest.Signup signupDto = AuthenticationRequest.Signup.of(loginId, password, favorite, nickname);

        // when
        authenticationService.signup(signupDto);

        // then
        Optional<User> findOptionalData = userRepository.findByLoginId(loginId);
        assertThat(findOptionalData).isPresent();
        User findData = findOptionalData.get();
        assertThat(findData)
                .extracting("loginId", "nickname", "type", "status", "role")
                .containsExactly(loginId, nickname, UserType.NORMAL, UserStatus.ACTIVE, UserRole.ROLE_USER);
    }

    @DisplayName("회원가입을 진행합니다. 중복된 회원 아이디는 회원가입 되지 않습니다. 오류메세지를 반환합니다.")
    @Test
    void signup2() {
        // given
        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        List<String> saveFavoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

        //사용자 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // request data
        String loginId = saveUser.getLoginId();
        String password = UserFactory.USER_LOGIN_PASSWORD;
        List<String> favorite = saveFavoriteNameList;
        String nickname = "중복 없는 닉네임";
        AuthenticationRequest.Signup signupDto =
                AuthenticationRequest.Signup.of(loginId, password, favorite, nickname);

        // when // then
        assertThatThrownBy(() -> authenticationService.signup(signupDto))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.DUPLICATED_LOGIN_ID);
    }

    @DisplayName("회원가입을 진행합니다. 중복된 닉네임은 회원가입 되지 않습니다. 오류메세지를 반환합니다.")
    @Test
    void signup3() {
        // given
        // 관심사 등록
        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(2);
        List<String> saveFavoriteNameList = saveFavoriteList.stream().map(FavoriteEntity::getName).toList();

        //사용자 등록
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // request data
        String loginId = "중복없는아이디@email.com";
        String password = "!test1234";
        List<String> favorite = saveFavoriteNameList;
        String nickname = saveUser.getNickname();
        AuthenticationRequest.Signup signupDto =
                AuthenticationRequest.Signup.of(loginId, password, favorite, nickname);

        // when // then
        assertThatThrownBy(() -> authenticationService.signup(signupDto))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.DUPLICATED_NICKNAME);
    }

    @DisplayName("회원가입을 진행합니다. 등록되지 않은 관심사로는 회원가입이 불가능합니다. 오류메세지를 반환합니다.")
    @Test
    void signup4() {
        // given
        String loginId = "test1@email.com";
        String password = "!test1234";
        List<String> favorite = List.of("미등록 관심사1", "미등록 관심사2");
        String nickname = "test1";

        AuthenticationRequest.Signup signupDto =
                AuthenticationRequest.Signup.of(loginId, password, favorite, nickname);

        // when // then
        assertThatThrownBy(() -> authenticationService.signup(signupDto))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.NOT_EXIST_FAVORITE);
    }

    @DisplayName("회원 탈퇴를 진행 합니다.")
    @Test
    void resign1() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        // when
        authenticationService.resign(String.valueOf(saveUser.getId()));

        // then
        User findUser = userRepository.findById(saveUser.getId()).get();
        assertThat(findUser.getStatus())
                .isEqualTo(UserStatus.DELETE);
    }

    @DisplayName("이미 탈퇴된 회원은 탈퇴 할 수 없고 오류를 발생합니다.")
    @Test
    void resign2() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();
        User resignUser = User.resign(saveUser);
        userRepository.save(resignUser);

        // when // then
        assertThatThrownBy(() -> authenticationService.resign(String.valueOf(saveUser.getId())))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.ALREADY_DELETE_USER);
    }

    @DisplayName("모임장 권한이 있는 사용자는 회원 탈퇴를 진행할 수 없습니다.")
    @Test
    void resign3() {
        // given
        List<User> saveUserList = userFactory.saveAndCreateUserData(1);
        User saveUser = saveUserList.getFirst();

        List<FavoriteEntity> saveFavoriteList = favoriteFactory.saveAndCreateFavoriteData(1);
        FavoriteEntity saveFavorite = saveFavoriteList.getFirst();

        classFactory.saveAndCreateClassData(1, saveUser, saveFavorite);

        // when // then
        assertThatThrownBy(() -> authenticationService.resign(String.valueOf(saveUser.getId())))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.CLASS_MASTER_TRANSFER_REQUIRED);
    }
}