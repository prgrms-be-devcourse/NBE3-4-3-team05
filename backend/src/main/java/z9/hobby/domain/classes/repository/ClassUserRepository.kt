package z9.hobby.domain.classes.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.entity.ClassUserEntity;

public interface ClassUserRepository extends JpaRepository<ClassUserEntity, Long> {
    boolean existsByUserIdAndClassesId(Long userId, Long classId);
    boolean existsByClasses_IdAndUserId(Long classId, Long userId);
    Optional<ClassUserEntity> findByClassesIdAndUserId(Long classId, Long userId);

    void deleteByUserId(Long userId);

    List<ClassUserEntity> findByClassesId(Long classId);

    // 특정 모임에서 특정 유저(모임장)를 제외한 회원 수 조회
    long countByClassesIdAndUserIdNot(Long classId, Long userId);

    boolean existsByClassesAndUserId(ClassEntity classEntity, Long userId);
}
