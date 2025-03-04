package z9.hobby.domain.authentication.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import z9.hobby.global.annotation.validation.user.UserLoginId;
import z9.hobby.global.annotation.validation.user.UserNickname;
import z9.hobby.global.annotation.validation.user.UserPassword;

public class AuthenticationRequest {

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Login {
        @UserLoginId
        private String loginId;

        @UserPassword
        private String password;

        public static Login of(String loginId, String password) {
            return new Login(loginId, password);
        }
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Signup {
        @UserLoginId
        private String loginId;

        @UserPassword
        private String password;

        @Size(min = 1, message = "관심사는 하나 이상 등록되어야 합니다.")
        private List<String> favorite;

        @UserNickname
        private String nickname;

        public static Signup of(String loginId, String password, List<String> favorite, String nickname) {
            return Signup
                    .builder()
                    .loginId(loginId)
                    .password(password)
                    .favorite(favorite)
                    .nickname(nickname)
                    .build();
        }
    }
}
