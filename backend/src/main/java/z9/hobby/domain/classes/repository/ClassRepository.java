package z9.hobby.domain.classes.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import z9.hobby.domain.classes.entity.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    Optional<ClassEntity> findFirstByOrderByIdDesc();

    List<ClassEntity> findByMasterId(Long userId);

    boolean existsByMasterId(Long userId);

    @Query("SELECT c FROM ClassEntity c JOIN ClassUserEntity cu ON c.id = cu.classes.id WHERE cu.userId = :userId")
    List<ClassEntity> findByUserId(@Param("userId") Long userId);

    // 관심사 기반 정렬 (로그인) - 사용자의 관심사 목록과 일치하는 모임을 찾는 쿼리
    @Query("SELECT c FROM ClassEntity c " +
            "WHERE c.favorite IN :userFavorites " +
            "ORDER BY c.favorite ASC, c.name ASC")
    List<ClassEntity> findByUserFavorites(@Param("userFavorites") List<String> userFavorites);

    // 관심사 기반 정렬 (비로그인) - 관심사별로 가나다순 정렬
    @Query("SELECT c FROM ClassEntity c " +
            "ORDER BY c.favorite ASC, c.name ASC")
    List<ClassEntity> findByFavorites();

    // 가나다순 정렬
    List<ClassEntity> findAllByOrderByName();

    // 참여인원순 정렬
    @Query("SELECT c FROM ClassEntity c " +
            "LEFT JOIN c.users u " +
            "GROUP BY c.id, c.name, c.favorite, c.description, c.masterId " +
            "ORDER BY COUNT(u) DESC")
    List<ClassEntity> findByParticipantSort();

    // 최신순 정렬
    List<ClassEntity> findAllByOrderByCreatedAtDesc();

    // 오래된순 정렬
    List<ClassEntity> findAllByOrderByCreatedAtAsc();
}
