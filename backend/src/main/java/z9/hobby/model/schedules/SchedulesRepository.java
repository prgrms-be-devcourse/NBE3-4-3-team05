package z9.hobby.model.schedules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SchedulesRepository extends JpaRepository<SchedulesEntity, Long> {
    // 클래스에 속한 모든 일정 조회
    @Query("SELECT s FROM SchedulesEntity s JOIN FETCH s.classes WHERE s.classes.id = :classId")
    List<SchedulesEntity> findSchedulesByClassesId(@Param("classId") Long classId);

    // 특정 클래스의 특정 일정 조회 (modify, delete, getScheduleDetail에서 사용)
    @Query("SELECT s FROM SchedulesEntity s JOIN FETCH s.classes c WHERE s.id = :scheduleId AND c.id = :classId")
    Optional<SchedulesEntity> findScheduleByIdAndClassesId(@Param("scheduleId") Long scheduleId, @Param("classId") Long classId);

    @Query("SELECT s FROM SchedulesEntity s " +
            "JOIN FETCH s.checkins sc " +
            "WHERE sc.userId = :userId AND sc.checkIn = true " +
            "ORDER BY s.meetingTime DESC")
    List<SchedulesEntity> findUserSchedulesInfoByUserId(@Param("userId") Long userId);
}
