package z9.hobby.domain.user.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import z9.hobby.global.annotation.validation.user.UserNickname;

public class UserRequest {

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PatchUserInfo {
        @UserNickname
        private String nickname;

        @Size(min = 1, message = "관심사는 하나 이상 등록되어야 합니다.")
        private List<String> favorite;

        public static PatchUserInfo of(String nickname, List<String> favorite) {
            return PatchUserInfo
                    .builder()
                    .nickname(nickname)
                    .favorite(favorite)
                    .build();
        }
    }
}
