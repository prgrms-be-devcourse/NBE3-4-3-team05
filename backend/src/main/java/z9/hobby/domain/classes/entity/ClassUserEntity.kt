package z9.hobby.domain.classes.entity

import jakarta.persistence.*

@Entity
class ClassUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cu_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    var classes: ClassEntity,

    @Column(name = "user_id", nullable = false)
    var userId: Long?
) {}
