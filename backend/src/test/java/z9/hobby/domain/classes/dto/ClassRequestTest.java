package z9.hobby.domain.classes.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("모임 생성 dto 검증")
    void createClassDto() {
        // given
        ClassRequest.ClassRequestData dto =
                ClassRequest.ClassRequestData.of("모임 제목", "관심사1", "모임내용 10글자 이상 입력");

        // when
        Set<ConstraintViolation<ClassRequest.ClassRequestData>> result =
                validator.validate(dto);

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    @DisplayName("모임 생성 dto 검증 실패")
    void createClassDto_validationFailure() {
        // given
        ClassRequest.ClassRequestData dto =
                ClassRequest.ClassRequestData.of("", "", "");

        // when
        Set<ConstraintViolation<ClassRequest.ClassRequestData>> result =
                validator.validate(dto);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("모임 수정 dto 검증")
    void modifyClassDto() {
        // given
        ClassRequest.ModifyRequestData dto =
                ClassRequest.ModifyRequestData.of("name", "dasdasdasdd");

        // when
        Set<ConstraintViolation<ClassRequest.ModifyRequestData>> result =
                validator.validate(dto);

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    @DisplayName("모임 수정 dto 검증 실패")
    void modifyClassDto_validationFailure() {
        // given
        ClassRequest.ModifyRequestData dto =
                ClassRequest.ModifyRequestData.of("", "");

        // when
        Set<ConstraintViolation<ClassRequest.ModifyRequestData>> result =
                validator.validate(dto);

        // then
        assertThat(result).hasSize(2);
    }
}
