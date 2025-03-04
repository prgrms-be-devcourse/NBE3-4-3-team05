package z9.hobby.domain.authentication.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthenticationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("로그인 Dto 유효성 검증")
    @Test
    void login1() {
        // given
        AuthenticationRequest.Login login =
                AuthenticationRequest.Login.of("test@test.com", "!asdf1234");

        // when
        Set<ConstraintViolation<AuthenticationRequest.Login>> validate =
                validator.validate(login);

        // then
        assertThat(validate).hasSize(0);
    }

    @DisplayName("잘못된 로그인 정보는 오류가 발생됩니다.")
    @Test
    void login2() {
        // given
        AuthenticationRequest.Login login =
                AuthenticationRequest.Login.of("test", "1234");

        // when
        Set<ConstraintViolation<AuthenticationRequest.Login>> validate = validator.validate(login);

        // then
        assertThat(validate).hasSize(3);
        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("이메일 형식이 아닙니다."))).isTrue();
        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("비밀번호는 8자리 ~ 20자리 사이 입니다."))).isTrue();
        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("비밀번호는 특수문자를 반드시 포함하여야 합니다."))).isTrue();
    }

    @DisplayName("회원가입 dto 유효성 검증")
    @Test
    void signup1() {
        // given
        String loginId = "test1@email.com";
        String password = "!test1234";
        List<String> favorite = List.of("관심사1", "관심사2");
        String nickname = "test1";

        AuthenticationRequest.Signup signupDto =
                AuthenticationRequest.Signup.of(loginId, password, favorite, nickname);

        // when
        Set<ConstraintViolation<AuthenticationRequest.Signup>> validate =
                validator.validate(signupDto);

        // then
        assertThat(validate).hasSize(0);
    }

    @DisplayName("회원가입 dto 유효성 검증")
    @Test
    void signup2() {
        // given
        String loginId = "test1";
        String password = "";
        List<String> favorite = new ArrayList<>();
        String nickname = "a";

        AuthenticationRequest.Signup signupDto =
                AuthenticationRequest.Signup.of(loginId, password, favorite, nickname);

        // when
        Set<ConstraintViolation<AuthenticationRequest.Signup>> validate =
                validator.validate(signupDto);

        // then
        assertThat(validate).hasSize(5);
    }
}