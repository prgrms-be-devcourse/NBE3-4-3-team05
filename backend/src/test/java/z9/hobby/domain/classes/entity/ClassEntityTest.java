package z9.hobby.domain.classes.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.base.ClassBaseTest;
import z9.hobby.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class ClassEntityTest extends ClassBaseTest {
    private User masterUser;
    private User memberUser;
    private ClassEntity classEntity;

    @BeforeEach
    void setUp() {
        // 마스터와 멤버 유저 생성
        masterUser = createTestUser("test@email.com", "테스터");
        memberUser = createTestUser("member@email.com", "멤버");

        // given 모임 생성
        classEntity = createTestClass(masterUser.getId());
    }

    @Test
    @DisplayName("addMember 테스트")
    void addMember() {
        // when
        ClassUserEntity classUser = classEntity.addMember(memberUser.getId());

        // then
        assertThat(classEntity.getUsers()).contains(classUser);
    }

    @Test
    @DisplayName("removeMember 테스트")
    void removeMember() {
        // given
        classEntity.addMember(memberUser.getId());
        ClassUserEntity findUser = classEntity.getUsers().getLast();

        // when
        classEntity.removeMember(findUser);

        // then
        assertThat(classEntity.getUsers()).doesNotContain(findUser);
    }

    @Test
    @DisplayName("updateClassInfo 테스트 - 이름, 설명 변경")
    void updateClassInfo() {
        // given
        String newName = "새로운 이름";
        String newDesc = "새로운 설명으로 변경 테스트";

        // when
        classEntity.updateClassInfo(newName, newDesc);

        // then
        assertThat(classEntity)
                .extracting("name", "description")
                .containsExactly(newName, newDesc);
    }

    @Test
    @DisplayName("updateClassInfo 테스트 - 이름 변경")
    void updateClassInfoName() {
        // given
        String newName = "새로운 이름";

        // when
        classEntity.updateClassInfo(newName, null);

        // then
        assertThat(classEntity)
                .extracting("name", "description")
                .containsExactly(newName, classEntity.getDescription());
    }

    @Test
    @DisplayName("updateClassInfo 테스트 - 설명 변경")
    void updateClassInfoDesc() {
        // given
        String newDesc = "새로운 설명으로 변경 테스트";

        // when
        classEntity.updateClassInfo(null, newDesc);

        // then
        assertThat(classEntity)
                .extracting("name", "description")
                .containsExactly(classEntity.getName(), newDesc);
    }

    @Test
    @DisplayName("addBlackList 테스트")
    void addBlackList() {
        // when
        classEntity.addBlackList(memberUser.getId());

        // then
        assertThat(classEntity.getBlackLists().getFirst())
                .extracting("userId")
                .isEqualTo(memberUser.getId());
    }
}
