package z9.hobby.domain.sample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import z9.hobby.model.sample.SampleEntity;

public class SampleRequest {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NewSampleData {
        @Size(min = 1, max = 3, message = "성은 1글자 이상, 3글자 이하 여야 합니다")
        private String firstName;
        @NotBlank(message = "이름은 필수 값 입니다!")
        private String secondName;
        @Size(min = 1, message = "나이는 1 이상이어야 합니다!")
        private Integer age;

        public static SampleEntity from(NewSampleData newSampleData) {
            return SampleEntity
                    .builder()
                    .firstName(newSampleData.getFirstName())
                    .secondName(newSampleData.getSecondName())
                    .age(newSampleData.getAge())
                    .build();
        }
    }
}
