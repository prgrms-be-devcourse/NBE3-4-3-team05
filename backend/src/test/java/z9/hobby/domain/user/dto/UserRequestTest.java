package z9.hobby.domain.user.dto;


import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("회원 정보 수정 dto 검증")
    @Test
    void PatchUserInfo() {
        // given
        UserRequest.PatchUserInfo requestDto =
                new UserRequest.PatchUserInfo("test", List.of("관심사1", "관심사2"));

        // when
        Set<ConstraintViolation<UserRequest.PatchUserInfo>> result = validator.validate(
                requestDto);

        // then
        assertThat(result).hasSize(0);
    }

    @DisplayName("회원 정보 수정은, 올바른 닉네임과 하나 이상의 관심사가 등록되어야 합니다.")
    @Test
    void PatchUserInfo1() {
        // given
        UserRequest.PatchUserInfo requestDto =
                new UserRequest.PatchUserInfo("!", List.of());

        // when
        Set<ConstraintViolation<UserRequest.PatchUserInfo>> result = validator.validate(
                requestDto);

        // then
        assertThat(result).hasSize(2);
    }
}