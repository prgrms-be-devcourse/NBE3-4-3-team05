package z9.hobby.domain.classes.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import z9.hobby.domain.classes.entity.ClassEntity
import java.util.*

interface ClassRepository : JpaRepository<ClassEntity, Long> {
    fun findFirstByOrderByIdDesc(): Optional<ClassEntity>

    fun findByMasterId(userId: Long): MutableList<ClassEntity>

    fun existsByMasterId(userId: Long): Boolean

    @Query("SELECT c FROM ClassEntity c JOIN ClassUserEntity cu ON c.id = cu.classes.id WHERE cu.userId = :userId")
    fun findByUserId(@Param("userId") userId: Long): List<ClassEntity>

    // 관심사 기반 정렬 (로그인) - 사용자의 관심사 목록과 일치하는 모임을 찾는 쿼리
    @Query(
        ("SELECT c FROM ClassEntity c " +
                "WHERE c.favorite IN :userFavorites " +
                "ORDER BY c.favorite ASC, c.name ASC")
    )
    fun findByUserFavorites(@Param("userFavorites") userFavorites: List<String>): List<ClassEntity>

    // 관심사 기반 정렬 (비로그인) - 관심사별로 가나다순 정렬
    @Query(
        "SELECT c FROM ClassEntity c " +
                "ORDER BY c.favorite ASC, c.name ASC"
    )
    fun findByFavorites(): List<ClassEntity>

    // 가나다순 정렬
    fun findAllByOrderByName(): List<ClassEntity>

    // 참여인원순 정렬
    @Query(
        ("SELECT c FROM ClassEntity c " +
                "LEFT JOIN c.users u " +
                "GROUP BY c.id, c.name, c.favorite, c.description, c.masterId " +
                "ORDER BY COUNT(u) DESC")
    )
    fun findByParticipantSort(): List<ClassEntity>

    // 최신순 정렬
    fun findAllByOrderByCreatedAtDesc(): List<ClassEntity>

    // 오래된순 정렬
    fun findAllByOrderByCreatedAtAsc(): List<ClassEntity>
}
