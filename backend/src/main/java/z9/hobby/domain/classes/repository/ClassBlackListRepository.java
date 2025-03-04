package z9.hobby.domain.classes.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import z9.hobby.domain.classes.entity.ClassBlackListEntity;

public interface ClassBlackListRepository extends JpaRepository<ClassBlackListEntity, Long> {
    boolean existsByClasses_IdAndUserId(Long classId, Long currentUserId);

    Optional<ClassBlackListEntity> findByClassesIdAndUserId(Long classId, Long userId);
}
