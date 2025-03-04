package z9.hobby.domain.classes.base;

import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.integration.SpringBootTestSupporter;
import z9.hobby.model.user.User;

@Transactional
public abstract class ClassBaseTest extends SpringBootTestSupporter {
    // 공통으로 사용되는 상수
    protected static final String TEST_PASSWORD = "!test1234";

    protected User createTestUser(String email, String nickname) {
        return userRepository.save(User.createNewUser(
                email,
                passwordEncoder.encode(TEST_PASSWORD),
                nickname
        ));
    }

    protected ClassEntity createTestClass(Long masterId) {
        ClassEntity newClass = classRepository.save(ClassEntity.builder()
                .masterId(masterId)
                .name("테스트 모임")
                .favorite("취미")
                .description("테스트 모임설명입니다")
                .build());

        newClass.addMember(masterId);

        return newClass;
    }
}
