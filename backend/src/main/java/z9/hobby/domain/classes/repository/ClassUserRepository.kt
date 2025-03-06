package z9.hobby.domain.classes.repository

import org.springframework.data.jpa.repository.JpaRepository
import z9.hobby.domain.classes.entity.ClassEntity
import z9.hobby.domain.classes.entity.ClassUserEntity
import java.util.*

interface ClassUserRepository : JpaRepository<ClassUserEntity, Long> {
    fun existsByUserIdAndClassesId(userId: Long, classId: Long): Boolean
    fun existsByClasses_IdAndUserId(classId: Long, userId: Long): Boolean
    fun findByClassesIdAndUserId(classId: Long, userId: Long): Optional<ClassUserEntity>

    fun deleteByUserId(userId: Long)

    fun findByClassesId(classId: Long): List<ClassUserEntity>

    // 특정 모임에서 특정 유저(모임장)를 제외한 회원 수 조회
    fun countByClassesIdAndUserIdNot(classId: Long, userId: Long): Long

    fun existsByClassesAndUserId(classEntity: ClassEntity, userId: Long): Boolean
}
