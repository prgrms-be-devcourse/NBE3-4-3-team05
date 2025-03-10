package z9.hobby.model.schedules

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface SchedulesRepository : JpaRepository<SchedulesEntity, Long> {
    // 클래스에 속한 모든 일정 조회
    @Query("SELECT s FROM SchedulesEntity s JOIN FETCH s.classes WHERE s.classes.id = :classId")
    fun findSchedulesByClassesId(@Param("classId") classId: Long): List<SchedulesEntity>

    // 특정 클래스의 특정 일정 조회 (modify, delete, getScheduleDetail에서 사용)
    @Query("SELECT s FROM SchedulesEntity s JOIN FETCH s.classes c WHERE s.id = :scheduleId AND c.id = :classId")
    fun findScheduleByIdAndClassesId(
        @Param("scheduleId") scheduleId: Long?,
        @Param("classId") classId: Long?
    ): Optional<SchedulesEntity>

    @Query(
        ("SELECT s FROM SchedulesEntity s " +
                "JOIN FETCH s.checkins sc " +
                "WHERE sc.userId = :userId AND sc.checkIn = true " +
                "ORDER BY s.meetingTime DESC")
    )
    fun findUserSchedulesInfoByUserId(@Param("userId") userId: Long): List<SchedulesEntity>

    @Query(
        ("SELECT s FROM SchedulesEntity s " +
                "WHERE s.lat BETWEEN :bottomLeftLat AND :topRightLat " +
                "AND s.lng BETWEEN LEAST(:bottomLeftLng, :topRightLng) AND GREATEST(:bottomLeftLng, :topRightLng)")
    )
    fun findByLatLng(
        @Param("bottomLeftLat") bottomLeftLat: Double,
        @Param("bottomLeftLng") bottomLeftLng: Double,
        @Param("topRightLat") topRightLat: Double,
        @Param("topRightLng") topRightLng: Double
    ): List<SchedulesEntity>

    @Query(
        "SELECT s FROM SchedulesEntity s JOIN FETCH s.classes c " +
                "WHERE s.lat BETWEEN :bottomLeftLat AND :topRightLat " +
                "AND s.lng BETWEEN LEAST(:bottomLeftLng, :topRightLng) AND GREATEST(:bottomLeftLng, :topRightLng) " +
                "AND c.favorite IN (" +
                "    SELECT f.name FROM FavoriteEntity f, UserFavorite uf " +
                "    WHERE uf.favorite.id = f.id AND uf.user.id = :userId" +
                ")"
    )
    fun findFavoriteSchedulesByUserId(
        @Param("userId") userId: Long,
        @Param("bottomLeftLat") bottomLeftLat: Double,
        @Param("bottomLeftLng") bottomLeftLng: Double,
        @Param("topRightLat") topRightLat: Double,
        @Param("topRightLng") topRightLng: Double
    ): List<SchedulesEntity>
}
