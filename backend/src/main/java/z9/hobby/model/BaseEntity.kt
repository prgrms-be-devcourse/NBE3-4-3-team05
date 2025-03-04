package z9.hobby.model

import jakarta.persistence.*
import java.time.LocalDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at")
    var modifiedAt: LocalDateTime? = null
}
