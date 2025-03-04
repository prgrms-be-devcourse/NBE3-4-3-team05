package z9.hobby.model.checkIn;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckInEntityRepository extends JpaRepository<CheckInEntity, Long> {
    boolean existsByUserIdAndSchedulesId(Long scheduleId,Long userId);
    List<CheckInEntity> findBySchedulesId(Long scheduleId);
    Optional<CheckInEntity> findBySchedulesIdAndUserId(Long scheduleId, Long userId);
}
