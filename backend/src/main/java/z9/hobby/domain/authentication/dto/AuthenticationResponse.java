package z9.hobby.domain.authentication.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AuthenticationResponse {

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserToken {
        private String accessToken;
        private String refreshToken;

        public static UserToken of(String accessToken, String refreshToken) {
            return UserToken
                    .builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }
}
