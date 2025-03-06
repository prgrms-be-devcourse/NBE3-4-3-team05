package z9.hobby.domain.classes.repository

import org.springframework.data.jpa.repository.JpaRepository
import z9.hobby.domain.classes.entity.ClassBlackListEntity
import java.util.*

interface ClassBlackListRepository : JpaRepository<ClassBlackListEntity, Long> {
    fun existsByClasses_IdAndUserId(classId: Long, currentUserId: Long): Boolean

    fun findByClassesIdAndUserId(classId: Long, userId: Long): Optional<ClassBlackListEntity>
}
