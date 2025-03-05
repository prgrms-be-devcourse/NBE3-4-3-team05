package z9.hobby.domain.classes.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ClassRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassRequestData {
        @Size(min = 3, message = "제목은 3글자 이상이어야 합니다.")
        private String name;

        @Size(min = 1, message = "관심사는 1가지를 입력하셔야 합니다.")
        private String favorite;

        @Size(min = 10, message = "내용은 10글자 이상이어야 합니다.")
        private String description;

        public static ClassRequestData of(String name, String favorite, String description) {
            return ClassRequestData
                    .builder()
                    .name(name)
                    .favorite(favorite)
                    .description(description)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModifyRequestData {
        @Size(min = 3, message = "제목은 3글자 이상이어야 합니다.")
        private String name;

        @Size(min = 10, message = "내용은 10글자 이상이어야 합니다.")
        private String description;

        public static ModifyRequestData of(String name, String description) {
            return ModifyRequestData
                    .builder()
                    .name(name)
                    .description(description)
                    .build();
        }
    }
}
